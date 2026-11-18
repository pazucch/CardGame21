package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import br.edu.positivo.cardgame21.ui.viewmodel.FiltroPlacar
import br.edu.positivo.cardgame21.ui.viewmodel.PlacarItem
import br.edu.positivo.cardgame21.ui.viewmodel.PlacarMetricas
import br.edu.positivo.cardgame21.ui.viewmodel.PlacarUiState
import java.util.Locale

@Composable
fun PlacarScreen(
    uiState: PlacarUiState,
    onBuscaChange: (String) -> Unit,
    onFiltroChange: (FiltroPlacar) -> Unit,
    onVoltar: () -> Unit,
    onItemClick: (PlacarItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerdeMesa)
            .systemBarsPadding()
            .padding(horizontal = Dimens.PaddingTela, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BarraTopo(
            titulo = "Ranking",
            descricaoVoltar = "Voltar",
            onVoltar = onVoltar
        ) {}

        OutlinedTextField(
            value = uiState.busca,
            onValueChange = onBuscaChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text("Buscar jogador...", color = VerdeTextoSuave)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Dourado
                )
            },
            trailingIcon = {
                if (uiState.busca.isNotEmpty()) {
                    IconButton(onClick = { onBuscaChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar busca",
                            tint = CremeTexto
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CremeTexto,
                unfocusedTextColor = CremeTexto,
                focusedContainerColor = VerdePainel,
                unfocusedContainerColor = VerdePainel,
                focusedBorderColor = Dourado,
                unfocusedBorderColor = VerdeBorda,
                cursorColor = Dourado
            ),
            shape = RoundedCornerShape(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FiltroPlacar.entries, key = { it.name }) { filtro ->
                val selecionado = uiState.filtro == filtro
                FilterChip(
                    selected = selecionado,
                    onClick = { onFiltroChange(filtro) },
                    label = {
                        Text(
                            text = filtro.rotulo,
                            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = VerdePainel,
                        labelColor = CremeTexto,
                        selectedContainerColor = Dourado,
                        selectedLabelColor = VerdeMesa
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selecionado,
                        borderColor = VerdeBorda,
                        selectedBorderColor = Dourado
                    )
                )
            }
        }

        if (!uiState.carregando && uiState.erro == null) {
            ResumoMetricasPlacar(metricas = uiState.metricas)
        }

        Text(
            text = "Melhores jogadores",
            color = CremeTexto,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        when {
            uiState.carregando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Dourado)
                }
            }

            uiState.erro != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VerdePainel)
                        .border(1.dp, Dourado, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = uiState.erro,
                        color = CremeTexto,
                        fontSize = 14.sp
                    )
                }
            }

            uiState.itens.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma partida encontrada para o filtro atual.",
                        color = VerdeTextoSuave,
                        fontSize = 15.sp
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.itens, key = { it.partidaId }) { item ->
                        CardPlacar(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumoMetricasPlacar(metricas: PlacarMetricas) {
    val mediaFormatada = String.format(Locale.getDefault(), "%.1f", metricas.mediaPontos)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VerdePainel)
            .border(1.dp, VerdeBorda, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MetricaColuna(rotulo = "Partidas", valor = metricas.totalPartidas.toString())
        MetricaColuna(rotulo = "Jogadores", valor = metricas.jogadoresDistintos.toString())
        MetricaColuna(rotulo = "Recorde", valor = "${metricas.melhorPontuacao} pts")
        MetricaColuna(rotulo = "Média", valor = mediaFormatada)
    }
}

@Composable
private fun MetricaColuna(rotulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            color = Dourado,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = rotulo,
            color = VerdeTextoSuave,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun CardPlacar(
    item: PlacarItem,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(12.dp)
    val corPosicao = when (item.posicao) {
        1 -> Dourado
        2 -> Creme
        else -> VerdeTextoSuave
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(VerdePainel)
            .border(1.dp, VerdeBorda, forma)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "#${item.posicao}",
                color = corPosicao,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.nome,
                color = CremeTexto,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = "${item.pontos} pts",
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
            uiState = PlacarUiState(
                carregando = false,
                itens = listOf(
                    PlacarItem(1, 1L, 10L, "Maria", 21, 1_700_000_000_000L),
                    PlacarItem(2, 2L, 11L, "Jogador21", 18, 1_700_000_000_000L),
                    PlacarItem(3, 3L, 12L, "João", 15, 1_700_000_000_000L)
                ),
                metricas = PlacarMetricas(
                    totalPartidas = 3,
                    jogadoresDistintos = 3,
                    melhorPontuacao = 21,
                    mediaPontos = 18.0
                )
            ),
            onBuscaChange = {},
            onFiltroChange = {},
            onVoltar = {}
        )
    }
}