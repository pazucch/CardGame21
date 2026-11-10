package br.edu.positivo.cardgame21

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.positivo.cardgame21.navigation.AppNavigation
import br.edu.positivo.cardgame21.ui.theme.Jogo21Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tela inteira, barras do sistema transparentes e ícones claros

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            Jogo21Theme {
                AppNavigation()
            }
        }
    }
}
