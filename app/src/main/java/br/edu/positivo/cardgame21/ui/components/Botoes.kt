package br.edu.positivo.cardgame21.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.theme.CremeTexto
import br.edu.positivo.cardgame21.ui.theme.Dimens
import br.edu.positivo.cardgame21.ui.theme.Dourado
import br.edu.positivo.cardgame21.ui.theme.Preto
import br.edu.positivo.cardgame21.ui.theme.VerdeTextoSuave

// Botão dourado cheio (ação principal da tela).
@Composable
fun BotaoPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altura: Dp = Dimens.AlturaBotao,
    tamanhoTexto: TextUnit = 16.sp
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(altura),
        shape = RoundedCornerShape(Dimens.RaioBotao),
        colors = ButtonDefaults.buttonColors(containerColor = Dourado, contentColor = Preto)
    ) {
        Text(texto, fontSize = tamanhoTexto, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

// Botão só com contorno dourado (ação secundária).
@Composable
fun BotaoSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altura: Dp = Dimens.AlturaBotao,
    tamanhoTexto: TextUnit = 16.sp
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(altura),
        shape = RoundedCornerShape(Dimens.RaioBotao),
        border = BorderStroke(Dimens.Borda, Dourado),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CremeTexto)
    ) {
        Text(texto, fontSize = tamanhoTexto, fontWeight = FontWeight.SemiBold)
    }
}

// Texto sublinhado clicável.
@Composable
fun BotaoLink(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 44.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = VerdeTextoSuave)
    ) {
        Text(texto, fontSize = 15.sp, textDecoration = TextDecoration.Underline)
    }
}
