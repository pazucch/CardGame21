package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import br.edu.positivo.cardgame21.ui.components.*
import br.edu.positivo.cardgame21.ui.theme.*
import br.edu.positivo.cardgame21.ui.viewmodel.AdminUiState

@Composable
fun AdminScreen(
    estado: AdminUiState,
    onAtualizarJogador: (Long, String, String, Boolean) -> Unit,
    onExcluirJogador: (Long) -> Unit,
    onAtualizarPartida: (Long, String) -> Unit,
    onExcluirPartida: (Long) -> Unit,
    onLimparMensagem: () -> Unit
) {
    if (!estado.autorizado) return
    var jogadorEdicao by remember { mutableStateOf<JogadorEntity?>(null) }
    var partidaEdicao by remember { mutableStateOf<PartidaEntity?>(null) }
    var exclusaoJogador by remember { mutableStateOf<JogadorEntity?>(null) }
    var exclusaoPartida by remember { mutableStateOf<PartidaEntity?>(null) }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(estado.mensagem, estado.mensagemErro) {
        val mensagem = estado.mensagemErro ?: estado.mensagem
        if (mensagem != null) { snackbar.showSnackbar(mensagem); onLimparMensagem() }
    }
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }, containerColor = VerdeMesa) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
            item { Text("Administração", color = Dourado, style = MaterialTheme.typography.headlineSmall) }
            if (estado.carregando) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            item { Text("Jogadores (${estado.jogadores.size})", color = CremeTexto) }
            items(estado.jogadores, key = { "jogador-${it.id}" }) { jogador ->
                Card(colors = CardDefaults.cardColors(containerColor = VerdePainel)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(jogador.nome, color = CremeTexto)
                        Text(if (jogador.isAdmin) "Administrador" else "Jogador", color = CremeTexto)
                        Row {
                            TextButton(onClick = { jogadorEdicao = jogador }, enabled = !estado.carregando) { Text("Editar") }
                            TextButton(onClick = { exclusaoJogador = jogador }, enabled = !estado.carregando) { Text("Excluir") }
                        }
                    }
                }
            }
            item { Text("Partidas (${estado.partidas.size})", color = CremeTexto) }
            if (estado.partidas.isEmpty()) item { Text("Nenhuma partida registrada.", color = CremeTexto) }
            items(estado.partidas, key = { "partida-${it.id}" }) { partida ->
                Card(colors = CardDefaults.cardColors(containerColor = VerdePainel)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        val nome = estado.jogadores.find { it.id == partida.jogadorId }?.nome ?: "Jogador ${partida.jogadorId}"
                        Text("Partida #${partida.id} • $nome", color = CremeTexto)
                        Text("${partida.pontos} pontos · ${partida.resultado.name}", color = CremeTexto)
                        if (partida.aposta > 0) {
                            val variacao = partida.variacaoSaldo
                            val saldo = if (variacao > 0) "+$variacao" else variacao.toString()
                            Text("Aposta: ${partida.aposta} · Variação do saldo: $saldo", color = CremeTexto)
                        }
                        Row {
                            TextButton(onClick = { partidaEdicao = partida }, enabled = !estado.carregando) { Text("Editar") }
                            TextButton(onClick = { exclusaoPartida = partida }, enabled = !estado.carregando) { Text("Excluir") }
                        }
                    }
                }
            }
        }
    }
    jogadorEdicao?.let { jogador ->
        var nome by remember(jogador.id) { mutableStateOf(jogador.nome) }
        var senha by remember(jogador.id) { mutableStateOf("") }
        var admin by remember(jogador.id) { mutableStateOf(jogador.isAdmin) }
        AlertDialog(onDismissRequest = { jogadorEdicao = null }, title = { Text("Editar jogador") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(nome, { nome = it }, label = { Text("Nome") }, singleLine = true)
                    OutlinedTextField(senha, { senha = it }, label = { Text("Nova senha (opcional)") }, singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
                    Row { Text("Administrador", Modifier.weight(1f)); Switch(admin, { admin = it }) }
                }
            },
            confirmButton = { TextButton(onClick = { onAtualizarJogador(jogador.id, nome, senha, admin); jogadorEdicao = null }) { Text("Salvar") } },
            dismissButton = { TextButton(onClick = { jogadorEdicao = null }) { Text("Cancelar") } })
    }
    partidaEdicao?.let { partida ->
        var pontos by remember(partida.id) { mutableStateOf(partida.pontos.toString()) }
        AlertDialog(onDismissRequest = { partidaEdicao = null }, title = { Text("Editar partida #${partida.id}") },
            text = { OutlinedTextField(pontos, { pontos = it }, label = { Text("Pontos (0 a 21)") }, singleLine = true) },
            confirmButton = { TextButton(onClick = { onAtualizarPartida(partida.id, pontos); partidaEdicao = null }) { Text("Salvar") } },
            dismissButton = { TextButton(onClick = { partidaEdicao = null }) { Text("Cancelar") } })
    }
    if (exclusaoJogador != null || exclusaoPartida != null) {
        AlertDialog(onDismissRequest = { exclusaoJogador = null; exclusaoPartida = null },
            title = { Text("Confirmar exclusão") },
            text = { Text(if (exclusaoJogador != null) "Excluir ${exclusaoJogador?.nome}? Suas partidas também serão excluídas." else "Excluir a partida #${exclusaoPartida?.id}?") },
            confirmButton = { TextButton(onClick = {
                exclusaoJogador?.let { onExcluirJogador(it.id) }
                exclusaoPartida?.let { onExcluirPartida(it.id) }
                exclusaoJogador = null; exclusaoPartida = null
            }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { exclusaoJogador = null; exclusaoPartida = null }) { Text("Cancelar") } })
    }
}
