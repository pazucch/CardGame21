package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.positivo.cardgame21.data.session.PerfilSessao
import br.edu.positivo.cardgame21.ui.components.*
import br.edu.positivo.cardgame21.ui.theme.*

@Composable
fun PerfilScreen(perfil: PerfilSessao, onSair: () -> Unit) {
    Column(Modifier.fillMaxSize().background(VerdeMesa).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Meu perfil", color = Dourado)
        Text(perfil.jogador?.nome ?: "Convidado", color = CremeTexto)
        Text(if (perfil.isAdmin) "Administrador" else "Jogador", color = CremeTexto)
        if (perfil.convidado) Text("Entre com uma conta para identificar suas partidas.", color = CremeTexto)
        BotaoPrincipal("SAIR DA CONTA", onSair, Modifier.fillMaxWidth())
    }
}
