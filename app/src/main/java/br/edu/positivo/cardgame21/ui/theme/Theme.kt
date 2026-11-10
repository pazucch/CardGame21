package br.edu.positivo.cardgame21.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaDeCores = darkColorScheme(
    primary = Dourado,
    onPrimary = Preto,
    secondary = VerdeBorda,
    onSecondary = CremeTexto,
    background = VerdeMesa,
    onBackground = CremeTexto,
    surface = VerdePainel,
    onSurface = CremeTexto,
    error = Vermelho,
    onError = Color.White
)

@Composable
fun Jogo21Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaDeCores,
        typography = Typography,
        content = content
    )
}
