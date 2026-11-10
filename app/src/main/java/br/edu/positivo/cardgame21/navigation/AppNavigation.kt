package br.edu.positivo.cardgame21.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.positivo.cardgame21.ui.screens.JogoScreen
import br.edu.positivo.cardgame21.ui.screens.LoginScreen
import br.edu.positivo.cardgame21.ui.screens.SalaScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Rotas.LOGIN) {

        composable(Rotas.LOGIN) {
            LoginScreen(
                onEntrar = { navController.irParaSala() },
                onConvidado = { navController.irParaSala() },
                onCriarConta = { /* TODO: tela de cadastro (ainda não existe) */ }
            )
        }

        composable(Rotas.SALA) {
            SalaScreen(
                onVoltar = { navController.voltarParaLogin() },
                onIniciarPartida = { navController.navigate(Rotas.JOGO) }
            )
        }

        composable(Rotas.JOGO) {
            JogoScreen(
                onSair = { navController.popBackStack() }
            )
        }
    }
}

// Entrar na sala tira o login da pilha: o botão "voltar" do celular não volta para o login.
private fun NavHostController.irParaSala() {
    navigate(Rotas.SALA) {
        popUpTo(Rotas.LOGIN) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.voltarParaLogin() {
    navigate(Rotas.LOGIN) {
        popUpTo(Rotas.SALA) { inclusive = true }
        launchSingleTop = true
    }
}
