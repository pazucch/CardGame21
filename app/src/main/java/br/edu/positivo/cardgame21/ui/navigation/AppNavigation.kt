package br.edu.positivo.cardgame21.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.edu.positivo.cardgame21.ui.screens.*

// Telas onde a barra inferior deve aparecer
val telasComBottomBar = listOf(Rotas.SALA, Rotas.PLACAR, Rotas.CRIAR_MESA)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Só mostra a barra se a tela atual estiver na lista
            if (currentRoute in telasComBottomBar) {
                NavigationBar {
                    // Item 1: Sala
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Sala") },
                        label = { Text("Sala") },
                        selected = currentRoute == Rotas.SALA,
                        onClick = {
                            navController.navigate(Rotas.SALA) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    // Item 2: Ranking
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Star, contentDescription = "Ranking") },
                        label = { Text("Ranking") },
                        selected = currentRoute == Rotas.PLACAR,
                        onClick = {
                            navController.navigate(Rotas.PLACAR) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    // Item 3: Perfil (Criar Usuário)
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = currentRoute == Rotas.CRIAR_USUARIO,
                        onClick = {
                            navController.navigate(Rotas.CRIAR_USUARIO) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
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
                    onEntrar = { navController.navigate(Rotas.SALA) },
                    onCriarConta = { navController.navigate(Rotas.CRIAR_USUARIO) },
                    onConvidado = { navController.navigate(Rotas.SALA) }
                )
            }

            composable(Rotas.CRIAR_USUARIO) {
                CriarUsuarioScreen(
                    onVoltar = { navController.popBackStack() },
                    onSalvarUsuario = { _, _ -> navController.popBackStack() }
                )
            }

            composable(Rotas.SALA) {
                SalaScreen(
                    onVoltar = { navController.popBackStack() },
                    onIniciarPartida = { navController.navigate(Rotas.JOGO) },
                    onCriarMesa = { navController.navigate(Rotas.CRIAR_MESA) }
                )
            }

            composable(Rotas.CRIAR_MESA) {
                CriarMesaScreen(
                    onVoltar = { navController.popBackStack() },
                    onCriarMesa = { _ -> navController.popBackStack() }
                )
            }

            composable(Rotas.JOGO) {
                JogoScreen(
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