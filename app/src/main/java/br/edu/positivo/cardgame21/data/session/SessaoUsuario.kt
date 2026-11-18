package br.edu.positivo.cardgame21.data.session

import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PerfilSessao(val jogador: JogadorEntity? = null, val convidado: Boolean = false) {
    val autenticado: Boolean get() = jogador != null || convidado
    val isAdmin: Boolean get() = jogador?.isAdmin == true
}
class SessaoUsuario {
    private val _perfil = MutableStateFlow(PerfilSessao())
    val perfil = _perfil.asStateFlow()
    fun entrar(jogador: JogadorEntity) { _perfil.value = PerfilSessao(jogador = jogador) }
    fun entrarComoConvidado() { _perfil.value = PerfilSessao(convidado = true) }
    fun sair() { _perfil.value = PerfilSessao() }
}
