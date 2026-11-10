package br.edu.positivo.cardgame21.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.theme.CremeTexto
import br.edu.positivo.cardgame21.ui.theme.Dourado
import br.edu.positivo.cardgame21.ui.theme.FonteTitulo
import br.edu.positivo.cardgame21.ui.theme.VerdeBorda
import br.edu.positivo.cardgame21.ui.theme.VerdePainel

// Botão quadrado com a seta de voltar.
@Composable
fun BotaoVoltar(descricao: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val forma = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(forma)
            .background(VerdePainel)
            .border(2.dp, VerdeBorda, forma)
            .clickable(onClickLabel = descricao, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = descricao,
            tint = CremeTexto
        )
    }
}

// Etiqueta arredondada (nome do jogador, saldo de fichas...).
@Composable
fun Pilula(
    texto: String,
    modifier: Modifier = Modifier,
    icone: (@Composable () -> Unit)? = null
) {
    val forma = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(forma)
            .background(VerdePainel)
            .border(2.dp, VerdeBorda, forma)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icone != null) icone()
        Text(texto, color = CremeTexto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Ficha dourada pequena, usada ao lado do saldo.
@Composable
fun FichaIcone(tamanho: Dp = 18.dp) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(CircleShape)
            .background(Dourado)
            .border(2.dp, VerdePainel, CircleShape)
    )
}

// Barra do topo: voltar à esquerda, título no meio e um conteúdo à direita.
@Composable
fun BarraTopo(
    titulo: String,
    descricaoVoltar: String,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    direita: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BotaoVoltar(descricao = descricaoVoltar, onClick = onVoltar)
        Text(
            text = titulo,
            color = CremeTexto,
            fontFamily = FonteTitulo,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        direita()
    }
}
