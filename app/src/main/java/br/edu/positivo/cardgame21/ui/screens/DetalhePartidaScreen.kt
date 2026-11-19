package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.logic.ResultadoRodada
import br.edu.positivo.cardgame21.ui.components.BarraTopo
import br.edu.positivo.cardgame21.ui.theme.*
import br.edu.positivo.cardgame21.ui.viewmodel.PlacarItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetalhePartidaScreen(
    item: PlacarItem,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0] ?: Locale.ROOT
    val dataFormatada = remember(item.dataPartida, locale) {
        val formato = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", locale)
        formato.format(Date(item.dataPartida))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerdeMesa)
            .systemBarsPadding()
            .padding(horizontal = Dimens.PaddingTela, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BarraTopo(
            titulo = "Detalhes da partida",
            descricaoVoltar = "Voltar",
            onVoltar = onVoltar
        ) {}

        val forma = RoundedCornerShape(16.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(forma)
                .background(VerdePainel)
                .border(1.dp, VerdeBorda, forma)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.nome,
                    color = CremeTexto,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "#${item.posicao}",
                    color = Dourado,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = VerdeBorda)

            val partida = item.partida
            val resultado = when (partida?.resultado) {
                ResultadoRodada.VITORIA -> "Vitória"
                ResultadoRodada.DERROTA -> "Derrota"
                ResultadoRodada.EMPATE -> "Empate"
                ResultadoRodada.DESCONHECIDO, null -> "Desconhecido"
            }
            val temDadosDaAposta = partida != null && partida.aposta > 0
            LinhaDetalhe(
                rotulo = "Vitórias no ranking",
                valor = "${item.pontos} ${if (item.pontos == 1) "vitória" else "vitórias"}",
                destacar = true
            )
            LinhaDetalhe(
                rotulo = "Pontuação da última partida",
                valor = partida?.let { "${it.pontos} pts" } ?: "Não registrada"
            )
            LinhaDetalhe(rotulo = "Resultado", valor = resultado)
            LinhaDetalhe(
                rotulo = "Aposta",
                valor = partida?.aposta?.takeIf { temDadosDaAposta }?.toString() ?: "Não registrada"
            )
            LinhaDetalhe(
                rotulo = "Variação do saldo",
                valor = partida?.variacaoSaldo?.takeIf { temDadosDaAposta }?.let {
                    if (it > 0) "+$it" else it.toString()
                } ?: "Não registrada"
            )
            LinhaDetalhe(rotulo = "Data e hora", valor = dataFormatada)
            LinhaDetalhe(rotulo = "ID da partida", valor = "#${item.partidaId}")
            LinhaDetalhe(rotulo = "ID do jogador", valor = "#${item.jogadorId}")
        }
    }
}

@Composable
private fun LinhaDetalhe(
    rotulo: String,
    valor: String,
    destacar: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rotulo,
            color = VerdeTextoSuave,
            fontSize = 14.sp
        )
        Text(
            text = valor,
            color = if (destacar) Dourado else CremeTexto,
            fontSize = if (destacar) 18.sp else 15.sp,
            fontWeight = if (destacar) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DetalhePartidaScreenPreview() {
    Jogo21Theme {
        DetalhePartidaScreen(
            item = PlacarItem(
                posicao = 1,
                partidaId = 12L,
                jogadorId = 4L,
                nome = "Maria",
                pontos = 21,
                dataPartida = 1_731_860_000_000L
            ),
            onVoltar = {}
        )
    }
}