package br.edu.positivo.cardgame21.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Borda tracejada com cantos arredondados (o Compose não traz uma pronta).
fun Modifier.bordaTracejada(cor: Color, raio: Dp = 8.dp, espessura: Dp = 2.dp): Modifier =
    this.drawBehind {
        drawRoundRect(
            color = cor,
            cornerRadius = CornerRadius(raio.toPx()),
            style = Stroke(
                width = espessura.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
            )
        )
    }
