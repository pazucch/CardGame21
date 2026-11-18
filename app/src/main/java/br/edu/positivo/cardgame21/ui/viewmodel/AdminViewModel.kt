package br.edu.positivo.cardgame21.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import br.edu.positivo.cardgame21.data.session.SessaoUsuario
import br.edu.positivo.cardgame21.logic.Seguranca
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminUiState(
    val jogadores: List<JogadorEntity> = emptyList(),
    val partidas: List<PartidaEntity> = emptyList(),
    val autorizado: Boolean = false,
    val carregando: Boolean = false,
    val mensagemErro: String? = null,
    val mensagem: String? = null
)

class AdminViewModel(
    private val jogadores: JogadorRepository,
    private val partidas: PartidaRepository,
    private val sessao: SessaoUsuario
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()
    private var observacao: Job? = null

    init {
        viewModelScope.launch {
            sessao.perfil.collect { perfil ->
                observacao?.cancel()
                _uiState.value = AdminUiState(autorizado = perfil.isAdmin)
                if (perfil.isAdmin) {
                    observacao = launch {
                        combine(jogadores.listarJogadores(), partidas.listarPlacar()) { usuarios, historico ->
                            usuarios to historico
                        }.collect { (usuarios, historico) ->
                            _uiState.update { it.copy(
                                jogadores = usuarios.getOrDefault(emptyList()).sortedBy { jogador -> jogador.nome.lowercase() },
                                partidas = historico.getOrDefault(emptyList()).sortedByDescending { partida -> partida.dataPartida },
                                mensagemErro = usuarios.exceptionOrNull()?.message ?: historico.exceptionOrNull()?.message
                            ) }
                        }
                    }
                }
            }
        }
    }

    fun limparMensagem() { _uiState.update { it.copy(mensagemErro = null, mensagem = null) } }

    // Confere a sessão e o perfil persistido também nas operações, além da rota.
    private fun executar(acao: suspend () -> Result<Unit>) {
        if (_uiState.value.carregando) return
        _uiState.update { it.copy(carregando = true, mensagemErro = null, mensagem = null) }
        viewModelScope.launch {
            val perfil = sessao.perfil.value
            val usuario = perfil.jogador
            val atual = usuario?.let { jogadores.buscarPorNome(it.nome).getOrNull() }
            if (!perfil.isAdmin || atual?.id != usuario?.id || atual?.isAdmin != true) {
                _uiState.value = AdminUiState(mensagemErro = "Acesso permitido somente ao administrador.")
                return@launch
            }
            if (sessao.perfil.value != perfil) return@launch
            val resultado = acao()
            if (sessao.perfil.value.jogador?.id != usuario?.id) return@launch
            _uiState.update { it.copy(carregando = false,
                mensagemErro = resultado.exceptionOrNull()?.message,
                mensagem = if (resultado.isSuccess) "Operação realizada com sucesso." else null) }
        }
    }

    fun atualizarJogador(id: Long, nome: String, senha: String, isAdmin: Boolean) = executar {
        val jogador = _uiState.value.jogadores.find { it.id == id }
        val erro = when {
            jogador == null -> "Jogador não encontrado."
            nome.trim().length !in 3..30 -> "O nome deve ter de 3 a 30 caracteres."
            senha.isNotEmpty() && (senha.isBlank() || senha.length < 6) -> "A senha deve ter pelo menos 6 caracteres."
            id == sessao.perfil.value.jogador?.id && !isAdmin -> "Você não pode remover seu próprio acesso de administrador."
            else -> null
        }
        if (erro != null) Result.failure(IllegalArgumentException(erro))
        else {
            val existente = requireNotNull(jogador)
            val atualizado = existente.copy(nome = nome.trim(), isAdmin = isAdmin,
                senhaHash = if (senha.isEmpty()) existente.senhaHash else Seguranca.hashSenha(senha))
            val resultado = jogadores.atualizarJogador(atualizado)
            if (resultado.isSuccess && id == sessao.perfil.value.jogador?.id) sessao.entrar(atualizado)
            resultado
        }
    }

    fun excluirJogador(id: Long) = executar {
        val jogador = _uiState.value.jogadores.find { it.id == id }
        when {
            id == sessao.perfil.value.jogador?.id -> Result.failure(IllegalArgumentException("Você não pode excluir sua própria conta."))
            jogador == null -> Result.failure(IllegalArgumentException("Jogador não encontrado."))
            else -> jogadores.deletarJogador(jogador)
        }
    }

    fun atualizarPartida(id: Long, pontos: String) = executar {
        val partida = _uiState.value.partidas.find { it.id == id }
        val valor = pontos.toIntOrNull()
        when {
            partida == null -> Result.failure(IllegalArgumentException("Partida não encontrada."))
            valor == null || valor !in 0..21 -> Result.failure(IllegalArgumentException("Informe pontos entre 0 e 21."))
            else -> partidas.atualizarPartida(partida.copy(pontos = valor))
        }
    }

    fun excluirPartida(id: Long) = executar {
        val partida = _uiState.value.partidas.find { it.id == id }
        if (partida == null) Result.failure(IllegalArgumentException("Partida não encontrada."))
        else partidas.deletarPartida(partida)
    }
}
