package br.edu.positivo.cardgame21.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import br.edu.positivo.cardgame21.data.repository.CartaRepository
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import br.edu.positivo.cardgame21.data.repository.FonteCartas as OrigemBaralho
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import br.edu.positivo.cardgame21.logic.Baralho
import br.edu.positivo.cardgame21.logic.Carta
import br.edu.positivo.cardgame21.logic.FonteCartas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class GameViewModel(
    private val baralho: FonteCartas = Baralho(),
    private val cartaRepository: CartaRepository? = null,
    private val partidaRepository: PartidaRepository? = null,
    private val jogadorRepository: JogadorRepository? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()
    private var jogadorId: Long? = null
    private var preparacao: Job? = null
    private var partidaPendente: PartidaEntity? = null

    fun definirJogador(nome: String, id: Long? = null) {
        val nomeNormalizado = nome.trim().ifBlank { "Convidado" }

        if (jogadorId == id && _uiState.value.nomeJogador == nomeNormalizado) return
        if (_uiState.value.salvando) return

        preparacao?.cancel()
        preparacao = null
        jogadorId = id
        partidaPendente = null

        _uiState.value = GameUiState(
            nomeJogador = nomeNormalizado,
            identificandoJogador = id != null && jogadorRepository != null
        )
        if (id != null && jogadorRepository != null) {
            viewModelScope.launch {
                val resultado = jogadorRepository.buscarSaldo(id)
                if (jogadorId != id) return@launch

                _uiState.value = _uiState.value.copy(
                    saldo = resultado.getOrNull() ?: 0,
                    identificandoJogador = false,
                    erro = if (resultado.isFailure) "Não foi possível carregar o saldo." else null
                )
            }
        }
    }

    fun criarMesa(aposta: Int): Boolean {
        val estado = _uiState.value
        if (estado.ocupado) return false
        if (estado.fase == FaseRodada.JOGADOR ||
            estado.fase == FaseRodada.BANCA) return false
        if (aposta < 10 || aposta % 10 != 0) return false
        if (aposta > estado.saldo) return false
        partidaPendente = null
        _uiState.value = GameUiState(
            codigoMesa = UUID.randomUUID().toString().take(8).uppercase(),
            nomeJogador = estado.nomeJogador,
            aposta = aposta,
            saldo = estado.saldo,
            aviso = estado.aviso
        )
        return true
    }

    fun iniciarPartida(): Boolean {
        if (!_uiState.value.podeIniciar) return false
        prepararRodada()
        return true
    }

    fun aumentarAposta() {
        val estado = _uiState.value
        if (estado.podeAlterarAposta &&
            estado.aposta <= estado.saldo - 10) {
            _uiState.value = estado.copy(aposta = estado.aposta + 10)
        }
    }

    fun diminuirAposta() {
        val estado = _uiState.value
        if (estado.podeAlterarAposta && estado.aposta > 10) {
            _uiState.value = estado.copy(aposta = estado.aposta - 10)
        }
    }

    fun sairDaPartida() {
        if (_uiState.value.salvando) return
        preparacao?.cancel()
        partidaPendente = null
        val estado = _uiState.value
        _uiState.value = GameUiState(
            codigoMesa = estado.codigoMesa, nomeJogador = estado.nomeJogador,
            aposta = estado.aposta,     saldo = estado.saldo

        )
    }

    fun fecharMesa() {
        if (_uiState.value.ocupado) return
        partidaPendente = null
        val estado = _uiState.value

        _uiState.value = GameUiState(
            nomeJogador = estado.nomeJogador,
            saldo = estado.saldo
        )
    }

    fun novaRodada() {
        val estado = _uiState.value

        if (estado.fase != FaseRodada.ENCERRADA ||
            estado.ocupado || partidaPendente != null) return

        if (estado.aposta > estado.saldo) {
            _uiState.value = estado.copy(
                aviso = "Saldo insuficiente. Diminua sua aposta."
            )
            return
        }

        prepararRodada()
    }

    private fun prepararRodada() {
        partidaPendente = null
        val repository = cartaRepository

        if (repository == null) {
            distribuirRodada()
            return
        }

        _uiState.value = _uiState.value.copy(carregando = true, erro = null)

        preparacao = viewModelScope.launch {
            try {
                val fonte = repository.iniciarBaralho()
                _uiState.value = _uiState.value.copy(
                    baralhoLocal = fonte == OrigemBaralho.LOCAL
                )
                distribuirRodada()
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: Exception) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    fase = FaseRodada.ENCERRADA,
                    erro = "Não foi possível preparar o baralho."
                )
            }
        }
    }

    private fun distribuirRodada() {
        baralho.reiniciar()
        val anterior = _uiState.value
        _uiState.value = GameUiState(
            codigoMesa = anterior.codigoMesa, nomeJogador = anterior.nomeJogador,
            aposta = anterior.aposta, saldo = anterior.saldo, fase = FaseRodada.JOGADOR,
            baralhoLocal = anterior.baralhoLocal,
            aviso = if (jogadorId == null) "Modo convidado: esta partida não será salva no histórico." else null
        )
        val jogador = mutableListOf<Carta>()
        val banca = mutableListOf<Carta>()
        repeat(2) {
            jogador.add(comprar() ?: return)
            banca.add(comprar() ?: return)
        }
        _uiState.value = _uiState.value.copy(
            cartasJogador = jogador.toList(), cartasBanca = banca.toList(),
            cartasRestantes = quantidadeRestante()
        )
        if (_uiState.value.pontosJogador == 21) parar()
    }

    fun pedirCarta() {
        val estado = _uiState.value
        if (!estado.podeJogar) return
        val carta = comprar() ?: return
        _uiState.value = estado.copy(
            cartasJogador = estado.cartasJogador + carta,
            cartasRestantes = quantidadeRestante()
        )
        when {
            _uiState.value.pontosJogador > 21 -> encerrar(ResultadoRodada.DERROTA)
            _uiState.value.pontosJogador == 21 -> parar()
        }
    }

    fun parar() {
        if (!_uiState.value.podeJogar) return
        _uiState.value = _uiState.value.copy(fase = FaseRodada.BANCA)
        // A banca para em qualquer 17, inclusive com Ás valendo 11.
        while (_uiState.value.pontosBanca < 17) {
            val carta = comprar() ?: return
            _uiState.value = _uiState.value.copy(
                cartasBanca = _uiState.value.cartasBanca + carta,
                cartasRestantes = quantidadeRestante()
            )
        }
        val estado = _uiState.value
        encerrar(when {
            estado.pontosBanca > 21 || estado.pontosJogador > estado.pontosBanca -> ResultadoRodada.VITORIA
            estado.pontosJogador < estado.pontosBanca -> ResultadoRodada.DERROTA
            else -> ResultadoRodada.EMPATE
        })
    }

    private fun comprar(): Carta? {
        val carta = if (cartaRepository != null) {
            cartaRepository.comprarCarta()
        } else {
            baralho.comprarCarta()
        }

        return carta ?: run {
            _uiState.value = _uiState.value.copy(
                fase = FaseRodada.ENCERRADA,
                cartasRestantes = 0,
                erro = "O baralho acabou. Inicie uma nova rodada."
            )
            null
        }
    }

    private fun encerrar(resultado: ResultadoRodada) {
        val estado = _uiState.value
        if (estado.fase == FaseRodada.ENCERRADA) return

        val variacao = when (resultado) {
            ResultadoRodada.VITORIA -> estado.aposta
            ResultadoRodada.DERROTA -> -estado.aposta
            ResultadoRodada.EMPATE -> 0
        }

        val novoSaldo = (estado.saldo.toLong() + variacao)
            .coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()

        val persistirSaldo = jogadorId != null &&
                jogadorRepository != null && partidaRepository != null

        _uiState.value = estado.copy(
            fase = FaseRodada.ENCERRADA,
            resultado = resultado,
            saldo = if (persistirSaldo) estado.saldo else novoSaldo
        )

        val id = jogadorId ?: return
        if (partidaRepository == null) return

        if (resultado == ResultadoRodada.VITORIA) {
            partidaPendente = PartidaEntity(
                id = (UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE)
                    .coerceAtLeast(1),
                jogadorId = id,
                pontos = if (estado.pontosJogador > 21) 0 else estado.pontosJogador
            )

            salvarPartida()
        }
    }

    private fun quantidadeRestante(): Int =
        cartaRepository?.cartasRestantes() ?: baralho.cartasRestantes()

    fun salvarPartida() {
        val partida = partidaPendente ?: return
        val repository = partidaRepository ?: return
        if (_uiState.value.salvando || _uiState.value.partidaSalva) return

        _uiState.value = _uiState.value.copy(
            salvando = true,
            erroSalvar = null
        )

        viewModelScope.launch {
            val jogadorRepo = jogadorRepository

            val variacao = when (_uiState.value.resultado) {
                ResultadoRodada.VITORIA -> _uiState.value.aposta
                ResultadoRodada.DERROTA -> -_uiState.value.aposta
                else -> 0
            }

            val resultado = if (jogadorRepo != null) {
                jogadorRepo.concluirRodada(partida, variacao)
            } else {
                repository.inserirPartida(partida)
                    .map { _uiState.value.saldo }
            }

            if (resultado.isSuccess) partidaPendente = null

            _uiState.value = _uiState.value.copy(
                saldo = resultado.getOrNull() ?: _uiState.value.saldo,
                salvando = false,
                partidaSalva = resultado.isSuccess,
                erroSalvar = if (resultado.isFailure)
                    "Não foi possível salvar a partida. Tente novamente."
                else null
            )
        }
    }

    fun descartarSalvamento() {
        if (_uiState.value.salvando) return
        partidaPendente = null
        _uiState.value = _uiState.value.copy(erroSalvar = null, aviso = "Resultado descartado do histórico.")
    }
}