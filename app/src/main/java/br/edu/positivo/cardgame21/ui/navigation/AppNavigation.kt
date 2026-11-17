package br.edu.positivo.cardgame21.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.positivo.cardgame21.CardGameApplication
import br.edu.positivo.cardgame21.ui.viewmodel.GameViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.edu.positivo.cardgame21.ui.screens.*

private data class ItemBarra(val rota: String, val rotulo: String, val icone: ImageVector)

// Só os destinos principais aparecem na barra (e só neles a barra é exibida).
// TODO (Admin/Perfil): adicionar aqui a aba de Perfil/Admin, mostrada conforme isAdmin.
private val itensBarra = listOf(
    ItemBarra(Rotas.SALA, "Sala", Icons.Filled.Home),
    ItemBarra(Rotas.PLACAR, "Ranking", Icons.Filled.Star)
)

// Troca de aba mantendo a Sala como base da pilha (e não o Login).
private fun NavHostController.irParaAba(rota: String) {
    navigate(rota) {
        popUpTo(Rotas.SALA) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Depois do login, remove o Login da pilha: o botão voltar não retorna a ele.
private fun NavHostController.entrarNaSala() {
    navigate(Rotas.SALA) {
        popUpTo(Rotas.LOGIN) { inclusive = true }
    }
}

// Sair da conta: limpa a pilha e volta ao Login.
private fun NavHostController.sairDaConta() {
    navigate(Rotas.LOGIN) {
        popUpTo(Rotas.SALA) { inclusive = true }
    }
}

@Composable
fun AppNavigation() {
    // A mesma instância mantém a mesa entre Sala, Criar Mesa e Jogo.
    val application = LocalContext.current.applicationContext as CardGameApplication
    val gameViewModel: GameViewModel = viewModel(factory = application.container.viewModelFactory)
    val jogo by gameViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (itensBarra.any { it.rota == currentRoute }) {
                NavigationBar {
                    itensBarra.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icone, contentDescription = item.rotulo) },
                            label = { Text(item.rotulo) },
                            selected = currentRoute == item.rota,
                            onClick = { navController.irParaAba(item.rota) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.LOGIN) {
                LoginScreen(
                    onEntrar = { nome, senha ->
                        gameViewModel.definirJogador(nome, senha)
                        navController.entrarNaSala()
                    },
                    onCriarConta = { navController.navigate(Rotas.CRIAR_USUARIO) },
                    onConvidado = {
                        gameViewModel.definirJogador("Convidado")
                        navController.entrarNaSala()
                    }
                )
            }

            composable(Rotas.CRIAR_USUARIO) {
                CriarUsuarioScreen(
                    onVoltar = { navController.popBackStack() },
                    onSalvarUsuario = { _, _, _ -> navController.popBackStack() }
                )
            }

            composable(Rotas.SALA) {
                SalaScreen(
                    onVoltar = {
                        gameViewModel.fecharMesa()
                        navController.sairDaConta()
                    },
                    onIniciarPartida = {
                        if (gameViewModel.iniciarPartida()) navController.navigate(Rotas.JOGO)
                    },
                    onCriarMesa = { navController.navigate(Rotas.CRIAR_MESA) },
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

            composable(Rotas.CRIAR_MESA) {
                CriarMesaScreen(
                    onVoltar = { navController.popBackStack() },
                    onCriarMesa = { aposta ->
                        if (gameViewModel.criarMesa(aposta)) navController.popBackStack()
                    }
                )
            }

            composable(Rotas.JOGO) {
                JogoRoute(
                    gameViewModel = gameViewModel,
                    onSair = { navController.popBackStack() }
                )
            }

            composable(Rotas.PLACAR) {
                PlacarScreen(
                    onVoltar = { navController.popBackStack() }
                )
            }
        }
    }
}
