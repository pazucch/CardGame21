
package br.edu.positivo.cardgame21.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
        popUpTo(Rotas.SALA) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation() {
    val container = (LocalContext.current.applicationContext as CardGameApplication).container
    val auth: AuthViewModel = viewModel(factory = container.viewModelFactory)
    val gameViewModel: GameViewModel = viewModel(factory = container.viewModelFactory)
    val estado by auth.uiState.collectAsState()
    val jogo by gameViewModel.uiState.collectAsState()
    val perfil = estado.perfil

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rota = backStackEntry?.destination?.route

    LaunchedEffect(perfil.autenticado, perfil.jogador?.id, perfil.jogador?.nome) {
        gameViewModel.definirJogador(
            nome = perfil.jogador?.nome ?: "Convidado",
            id = perfil.jogador?.id
        )
    }

    fun sairDaConta() {
        if (jogo.salvando) return
        gameViewModel.sairDaPartida()
        gameViewModel.fecharMesa()
        auth.sair()
    }

    LaunchedEffect(perfil.autenticado, perfil.isAdmin, rota) {
        val destino = when {
            !perfil.autenticado && rota != null &&
                    rota !in listOf(Rotas.LOGIN, Rotas.CRIAR_USUARIO) -> Rotas.LOGIN
            perfil.autenticado && rota in listOf(Rotas.LOGIN, Rotas.CRIAR_USUARIO) -> Rotas.SALA
            rota == Rotas.ADMIN && !perfil.isAdmin -> Rotas.SALA
            else -> null
        }

        if (destino != null) navController.navigate(destino) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    val itens = listOf(
        ItemBarra(Rotas.SALA, "Sala", Icons.Filled.Home),
        ItemBarra(Rotas.PLACAR, "Ranking", Icons.Filled.Star),
        ItemBarra(Rotas.PERFIL, "Perfil", Icons.Filled.Person)
    ) + if (perfil.isAdmin) listOf(
        ItemBarra(Rotas.ADMIN, "Admin", Icons.Filled.Settings)
    ) else emptyList()

    Scaffold(bottomBar = {
        if (perfil.autenticado && itens.any { it.rota == rota }) {
            NavigationBar {
                itens.forEach { item ->
                    NavigationBarItem(
                        selected = rota == item.rota,
                        onClick = { navController.irParaAba(item.rota) },
                        icon = { Icon(item.icone, contentDescription = item.rotulo) },
                        label = { Text(item.rotulo) }
                    )
                }
            }
        }
    }) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rotas.LOGIN) {
                LoginScreen(
                    onEntrar = auth::entrar,
                    onCriarConta = {
                        auth.limparErro()
                        navController.navigate(Rotas.CRIAR_USUARIO)
                    },
                    onConvidado = auth::entrarComoConvidado,
                    mensagemErro = estado.mensagemErro,
                    carregando = estado.carregando
                )
            }

            composable(Rotas.CRIAR_USUARIO) {
                CriarUsuarioScreen(
                    onVoltar = {
                        auth.limparErro()
                        navController.popBackStack()
                    },
                    onSalvarUsuario = { nome, senha, _ ->
                        auth.cadastrar(nome, senha)
                    },
                    mensagemErro = estado.mensagemErro,
                    carregando = estado.carregando
                )
            }

            composable(Rotas.SALA) {
                if (perfil.autenticado) {
                    SalaScreen(
                        onVoltar = { sairDaConta() },
                        onIniciarPartida = {
                            if (gameViewModel.iniciarPartida()) {
                                navController.navigate(Rotas.JOGO)
                            }
                        },
                        onCriarMesa = {
                            navController.navigate(Rotas.CRIAR_MESA)
                        },
                        nomeUsuario = jogo.nomeJogador,
                        jogadores = if (jogo.temMesa) listOf(jogo.nomeJogador) else emptyList(),
                        codigoMesa = jogo.codigoMesa,
                        modoLocal = true,
                        podeIniciar = jogo.podeIniciar,
                        aposta = jogo.aposta,
                        onAumentarAposta = gameViewModel::aumentarAposta,
                        onDiminuirAposta = gameViewModel::diminuirAposta,
                        onFecharMesa = gameViewModel::fecharMesa
                    )
                }
            }

            composable(Rotas.CRIAR_MESA) {
                if (perfil.autenticado) {
                    CriarMesaScreen(
                        onVoltar = { navController.popBackStack() },
                        onCriarMesa = { aposta ->
                            if (gameViewModel.criarMesa(aposta)) {
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }

            composable(Rotas.JOGO) {
                if (perfil.autenticado) {
                    JogoRoute(
                        gameViewModel = gameViewModel,
                        onSair = { navController.popBackStack() }
                    )
                }
            }

            composable(Rotas.PLACAR) {
                if (perfil.autenticado) {
                    PlacarScreen(onVoltar = { navController.popBackStack() })
                }
            }

            composable(Rotas.PERFIL) {
                if (perfil.autenticado) {
                    PerfilScreen(perfil = perfil, onSair = { sairDaConta() })
                }
            }

            composable(Rotas.ADMIN) {
                if (perfil.isAdmin) {
                    val admin: AdminViewModel = viewModel(factory = container.viewModelFactory)
                    val adminState by admin.uiState.collectAsState()

                    AdminScreen(
                        adminState,
                        admin::atualizarJogador,
                        admin::excluirJogador,
                        admin::atualizarPartida,
                        admin::excluirPartida,
                        admin::limparMensagem
                    )
                }
            }
        }
    }
}