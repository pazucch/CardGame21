package br.edu.positivo.cardgame21.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import br.edu.positivo.cardgame21.CardGameApplication
import br.edu.positivo.cardgame21.ui.screens.*
import br.edu.positivo.cardgame21.ui.viewmodel.*

private data class ItemBarra(val rota: String, val rotulo: String, val icone: ImageVector)

private fun NavHostController.irParaAba(rota: String) {
    navigate(rota) {
        popUpTo(Rotas.SALA)
        launchSingleTop = true
    }
}

@Composable
fun AppNavigation() {
    val container = (LocalContext.current.applicationContext as CardGameApplication).container
    val auth: AuthViewModel = viewModel(factory = container.viewModelFactory)
    val estado by auth.uiState.collectAsState()
    val perfil = estado.perfil
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rota = backStackEntry?.destination?.route
    val itens = listOf(
        ItemBarra(Rotas.SALA, "Sala", Icons.Filled.Home),
        ItemBarra(Rotas.PLACAR, "Ranking", Icons.Filled.Star),
        ItemBarra(Rotas.PERFIL, "Perfil", Icons.Filled.Person)
    ) + if (perfil.isAdmin) listOf(ItemBarra(Rotas.ADMIN, "Admin", Icons.Filled.Settings)) else emptyList()

    // Não renderiza destinos protegidos enquanto redireciona (inclui restauração da pilha).
    LaunchedEffect(perfil.autenticado, perfil.isAdmin, rota) {
        val destino = when {
            !perfil.autenticado && rota != null && rota !in listOf(Rotas.LOGIN, Rotas.CRIAR_USUARIO) -> Rotas.LOGIN
            perfil.autenticado && rota in listOf(Rotas.LOGIN, Rotas.CRIAR_USUARIO) -> Rotas.SALA
            rota == Rotas.ADMIN && !perfil.isAdmin -> if (perfil.autenticado) Rotas.SALA else Rotas.LOGIN
            else -> null
        }
        if (destino != null) navController.navigate(destino) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(bottomBar = {
        if (perfil.autenticado && itens.any { it.rota == rota }) NavigationBar {
            itens.forEach { item ->
                NavigationBarItem(selected = rota == item.rota,
                    onClick = { navController.irParaAba(item.rota) },
                    icon = { Icon(item.icone, contentDescription = item.rotulo) },
                    label = { Text(item.rotulo) })
            }
        }
    }) { padding ->
        NavHost(navController, startDestination = Rotas.LOGIN, modifier = Modifier.padding(padding)) {
            composable(Rotas.LOGIN) {
                LoginScreen(onEntrar = auth::entrar,
                    onCriarConta = { auth.limparErro(); navController.navigate(Rotas.CRIAR_USUARIO) },
                    onConvidado = auth::entrarComoConvidado,
                    mensagemErro = estado.mensagemErro, carregando = estado.carregando)
            }
            composable(Rotas.CRIAR_USUARIO) {
                CriarUsuarioScreen(onVoltar = { auth.limparErro(); navController.popBackStack() },
                    onSalvarUsuario = { nome, senha, _ -> auth.cadastrar(nome, senha) },
                    mensagemErro = estado.mensagemErro, carregando = estado.carregando)
            }
            composable(Rotas.SALA) {
                if (perfil.autenticado) SalaScreen(onVoltar = auth::sair,
                    onIniciarPartida = { navController.navigate(Rotas.JOGO) },
                    onCriarMesa = { navController.navigate(Rotas.CRIAR_MESA) })
            }
            composable(Rotas.CRIAR_MESA) {
                if (perfil.autenticado) CriarMesaScreen(onVoltar = { navController.popBackStack() },
                    onCriarMesa = { _ -> navController.popBackStack() })
            }
            composable(Rotas.JOGO) {
                if (perfil.autenticado) JogoScreen(onSair = { navController.popBackStack() })
            }
            composable(Rotas.PLACAR) {
                if (perfil.autenticado) PlacarScreen(onVoltar = { navController.popBackStack() })
            }
            composable(Rotas.PERFIL) {
                if (perfil.autenticado) PerfilScreen(perfil, auth::sair)
            }
            composable(Rotas.ADMIN) {
                if (perfil.isAdmin) {
                    val admin: AdminViewModel = viewModel(factory = container.viewModelFactory)
                    val adminState by admin.uiState.collectAsState()
                    AdminScreen(adminState, admin::atualizarJogador, admin::excluirJogador,
                        admin::atualizarPartida, admin::excluirPartida, admin::limparMensagem)
                }
            }
        }
    }
}
