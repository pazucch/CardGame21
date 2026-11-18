package br.edu.positivo.cardgame21.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.session.PerfilSessao
import br.edu.positivo.cardgame21.data.session.SessaoUsuario
import br.edu.positivo.cardgame21.logic.Seguranca
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val perfil: PerfilSessao = PerfilSessao(),
    val carregando: Boolean = true,
    val mensagemErro: String? = null
)

class AuthViewModel(
    private val repository: JogadorRepository,
    private val sessao: SessaoUsuario
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessao.perfil.collect { perfil -> _uiState.update { it.copy(perfil = perfil) } }
        }
        viewModelScope.launch {
            // Conta inicial da demonstração acadêmica, criada uma única vez pelo DAO.
            val resultado = repository.criarAdminInicial("admin", Seguranca.hashSenha("admin123"))
            _uiState.update { it.copy(carregando = false, mensagemErro = resultado.exceptionOrNull()?.message) }
        }
    }

    fun limparErro() { _uiState.update { it.copy(mensagemErro = null) } }

    fun entrar(nome: String, senha: String) {
        if (_uiState.value.carregando) return
        if (nome.isBlank() || senha.isBlank()) {
            erro("Preencha o nome e a senha.")
            return
        }
        _uiState.update { it.copy(carregando = true, mensagemErro = null) }
        viewModelScope.launch {
            repository.buscarPorNome(nome.trim()).fold(
                onSuccess = { jogador ->
                    if (jogador != null && jogador.senhaHash == Seguranca.hashSenha(senha)) {
                        sessao.entrar(jogador)
                    } else erro("Usuário ou senha incorretos.")
                },
                onFailure = { erro(it.message ?: "Não foi possível entrar.") }
            )
            _uiState.update { it.copy(carregando = false) }
        }
    }

    fun cadastrar(nome: String, senha: String) {
        if (_uiState.value.carregando) return
        val validacao = validarCadastro(nome, senha)
        if (validacao != null) { erro(validacao); return }
        _uiState.update { it.copy(carregando = true, mensagemErro = null) }
        viewModelScope.launch {
            // O formulário público jamais determina privilégios de administrador.
            val jogador = JogadorEntity(nome = nome.trim(), senhaHash = Seguranca.hashSenha(senha))
            repository.inserirJogador(jogador).fold(
                onSuccess = { id -> sessao.entrar(jogador.copy(id = id)) },
                onFailure = { erro(it.message ?: "Não foi possível cadastrar.") }
            )
            _uiState.update { it.copy(carregando = false) }
        }
    }

    fun entrarComoConvidado() {
        if (_uiState.value.carregando) return
        limparErro()
        sessao.entrarComoConvidado()
    }

    fun sair() { sessao.sair(); limparErro() }
    private fun erro(mensagem: String) { _uiState.update { it.copy(mensagemErro = mensagem) } }

    companion object {
        fun validarCadastro(nome: String, senha: String): String? = when {
            nome.trim().length !in 3..30 -> "O nome deve ter de 3 a 30 caracteres."
            senha.isBlank() || senha.length < 6 -> "A senha deve ter pelo menos 6 caracteres."
            else -> null
        }
    }
}
