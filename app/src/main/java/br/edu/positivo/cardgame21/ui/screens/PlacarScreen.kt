package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.components.BarraTopo
import br.edu.positivo.cardgame21.ui.theme.*

// Modelo temporário para a UI. O ViewModel vai mapear do banco (Room) para isso.
data class PlacarItemUIData(
    val nome: String,
    val pontos: Int
)

@Composable
fun PlacarScreen(
    onVoltar: () -> Unit,
    placar: List<PlacarItemUIData> = emptyList(), // Recebendo a lista do ViewModel
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerdeMesa)
            .systemBarsPadding()
            .padding(horizontal = Dimens.PaddingTela, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BarraTopo(
            titulo = "Ranking",
            descricaoVoltar = "Voltar",
            onVoltar = onVoltar
        ) {}

        Text(
            "Melhores jogadores",
            color = CremeTexto,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        // O LazyColumn é OBRIGATÓRIO pelo enunciado
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(placar) { index, item ->
                CardPlacar(
                    posicao = index + 1, // A posição é o índice + 1
                    nome = item.nome,
                    pontos = item.pontos
                )
            }
        }
    }
}

@Composable
private fun CardPlacar(posicao: Int, nome: String, pontos: Int) {
    val forma = RoundedCornerShape(12.dp)
    val corPosicao = if (posicao == 1) Dourado else if (posicao == 2) Creme else VerdeTextoSuave

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(VerdePainel)
            .border(1.dp, VerdeBorda, forma)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Posição
            Text(
                text = "#$posicao",
                color = corPosicao,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            // Nome
            Text(
                text = nome,
                color = CremeTexto,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        // Pontos
        Text(
            text = "$pontos pts",
            color = Dourado,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PlacarScreenPreview() {
    Jogo21Theme {
        PlacarScreen(
            onVoltar = {},
            placar = listOf(
                PlacarItemUIData("maria", 21),
                PlacarItemUIData("jogador21", 18),
                PlacarItemUIData("joão", 15)
            )
        )
    }
}