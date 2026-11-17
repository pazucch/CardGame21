package br.edu.positivo.cardgame21.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import br.edu.positivo.cardgame21.data.repository.CartaRepository
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import br.edu.positivo.cardgame21.logic.Seguranca
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
    private var identificacao: Job? = null
    private var preparacao: Job? = null
    private var cartasPreparadas: ArrayDeque<Carta>? = null
    private var partidaPendente: PartidaEntity? = null

    fun definirJogador(nome: String, senha: String? = null) {
        if (_uiState.value.salvando) return
        identificacao?.cancel()
        preparacao?.cancel()
        jogadorId = null
        partidaPendente = null
        _uiState.value = GameUiState(nomeJogador = nome.trim().ifBlank { "Convidado" })
        val repository = jogadorRepository
        if (senha == null || repository == null) return
        _uiState.value = _uiState.value.copy(identificandoJogador = true)
        identificacao = viewModelScope.launch {
            val consulta = repository.buscarPorNome(nome.trim())
            val jogador = consulta.getOrNull()
            jogadorId = jogador?.takeIf { it.senhaHash == Seguranca.hashSenha(senha) }?.id
            _uiState.value = _uiState.value.copy(
                identificandoJogador = false,
                aviso = if (jogadorId == null) "Perfil não autenticado. Esta sessão não salvará partidas no histórico." else null
            )
        }
    }

    fun criarMesa(aposta: Int): Boolean {
        if (_uiState.value.ocupado) return false
        if (_uiState.value.fase == FaseRodada.JOGADOR || _uiState.value.fase == FaseRodada.BANCA) return false
        if (aposta < 10 || aposta % 10 != 0) return false
        partidaPendente = null
        _uiState.value = GameUiState(
            codigoMesa = UUID.randomUUID().toString().take(8).uppercase(),
            nomeJogador = _uiState.value.nomeJogador,
            aposta = aposta, aviso = _uiState.value.aviso
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
        if (estado.podeAlterarAposta && estado.aposta <= Int.MAX_VALUE - 10) {
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
            codigoMesa = estado.codigoMesa, nomeJogador = estado.nomeJogador, aposta = estado.aposta
        )
    }

    fun fecharMesa() {
        if (_uiState.value.ocupado) return
        partidaPendente = null
        _uiState.value = GameUiState(nomeJogador = _uiState.value.nomeJogador)
    }

    fun novaRodada() {
        if (_uiState.value.fase != FaseRodada.ENCERRADA || _uiState.value.ocupado || partidaPendente != null) return
        prepararRodada()
    }

    private fun prepararRodada() {
        partidaPendente = null
        val repository = cartaRepository
        if (repository == null) {
            cartasPreparadas = null
            distribuirRodada()
            return
        }
        _uiState.value = _uiState.value.copy(carregando = true, erro = null)
        preparacao = viewModelScope.launch {
            val preparado = repository.prepararBaralho()
            cartasPreparadas = ArrayDeque(preparado.cartas)
            _uiState.value = _uiState.value.copy(baralhoLocal = preparado.local)
            distribuirRodada()
        }
    }

    private fun distribuirRodada() {
        baralho.reiniciar()
        val anterior = _uiState.value
        _uiState.value = GameUiState(
            codigoMesa = anterior.codigoMesa, nomeJogador = anterior.nomeJogador,
            aposta = anterior.aposta, fase = FaseRodada.JOGADOR,
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
        val carta = if (cartasPreparadas != null) cartasPreparadas!!.removeFirstOrNull() else baralho.comprarCarta()
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
        _uiState.value = _uiState.value.copy(fase = FaseRodada.ENCERRADA, resultado = resultado)
        val id = jogadorId ?: return
        if (partidaRepository == null) return
        // O mesmo ID é reutilizado nas tentativas: o banco não duplica a rodada.
        partidaPendente = PartidaEntity(
            id = (UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE).coerceAtLeast(1),
            jogadorId = id,
            pontos = if (_uiState.value.pontosJogador > 21) 0 else _uiState.value.pontosJogador
        )
        salvarPartida()
    }

    private fun quantidadeRestante(): Int = cartasPreparadas?.size ?: baralho.cartasRestantes()

    fun salvarPartida() {
        val partida = partidaPendente ?: return
        val repository = partidaRepository ?: return
        if (_uiState.value.salvando || _uiState.value.partidaSalva) return
        _uiState.value = _uiState.value.copy(salvando = true, erroSalvar = null)
        viewModelScope.launch {
            val resultado = repository.inserirPartida(partida)
            if (resultado.isSuccess) partidaPendente = null
            _uiState.value = _uiState.value.copy(
                salvando = false, partidaSalva = resultado.isSuccess,
                erroSalvar = if (resultado.isFailure) "Não foi possível salvar a partida. Tente novamente." else null
            )
        }
    }

    fun descartarSalvamento() {
        if (_uiState.value.salvando) return
        partidaPendente = null
        _uiState.value = _uiState.value.copy(erroSalvar = null, aviso = "Resultado descartado do histórico.")
    }
}
