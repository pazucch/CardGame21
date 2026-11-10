package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.components.BarraTopo
import br.edu.positivo.cardgame21.ui.components.BotaoPrincipal
import br.edu.positivo.cardgame21.ui.components.BotaoSecundario
import br.edu.positivo.cardgame21.ui.components.CampoEntrada
import br.edu.positivo.cardgame21.ui.components.Pilula
import br.edu.positivo.cardgame21.ui.components.bordaTracejada
import br.edu.positivo.cardgame21.ui.theme.CremeTexto
import br.edu.positivo.cardgame21.ui.theme.Dimens
import br.edu.positivo.cardgame21.ui.theme.Dourado
import br.edu.positivo.cardgame21.ui.theme.FonteTitulo
import br.edu.positivo.cardgame21.ui.theme.Jogo21Theme
import br.edu.positivo.cardgame21.ui.theme.Preto
import br.edu.positivo.cardgame21.ui.theme.VerdeBorda
import br.edu.positivo.cardgame21.ui.theme.VerdeMesa
import br.edu.positivo.cardgame21.ui.theme.VerdePainel
import br.edu.positivo.cardgame21.ui.theme.VerdeTextoSuave

// Dados de exemplo só para desenhar a tela (a lista real virá depois).
private data class JogadorSala(val nome: String, val anfitriao: Boolean = false)

private const val LUGARES_NA_MESA = 4

@Composable
fun SalaScreen(
    onVoltar: () -> Unit,
    onIniciarPartida: () -> Unit,
    modifier: Modifier = Modifier,
    onCriarMesa: () -> Unit = {},
    onEntrarComCodigo: (String) -> Unit = {},
    onCompartilhar: () -> Unit = {}
) {
    var codigo by rememberSaveable { mutableStateOf("") }
    val jogadores = listOf(JogadorSala("jogador21", anfitriao = true), JogadorSala("maria"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerdeMesa)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.PaddingTela, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BarraTopo(
            titulo = "Sala",
            descricaoVoltar = "Voltar ao login",
            onVoltar = onVoltar
        ) { Pilula("jogador21") }

        BotaoPrincipal("CRIAR MESA", onCriarMesa, Modifier.fillMaxWidth())

        // Entrar em uma mesa com código
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Entrar com código", color = CremeTexto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CampoEntrada(
                    valor = codigo,
                    onValorChange = { codigo = it },
                    placeholder = "ex.: K7Q2",
                    modifier = Modifier.weight(1f)
                )
                BotaoSecundario(
                    texto = "ENTRAR",
                    onClick = { onEntrarComCodigo(codigo) },
                    modifier = Modifier.width(104.dp),
                    altura = 52.dp,
                    tamanhoTexto = 15.sp
                )
            }
        }

        // Mesa criada: código e jogadores
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VerdePainel)
                .border(2.dp, VerdeBorda, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "CÓDIGO DA MESA",
                        color = VerdeTextoSuave,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        "K7Q2",
                        color = Dourado,
                        fontFamily = FonteTitulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        letterSpacing = 4.sp
                    )
                }
                BotaoSecundario(
                    texto = "Compartilhar",
                    onClick = onCompartilhar,
                    altura = 44.dp,
                    tamanhoTexto = 14.sp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Jogadores (${jogadores.size} de $LUGARES_NA_MESA)",
                    color = CremeTexto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                repeat(LUGARES_NA_MESA) { posicao ->
                    LinhaJogador(jogadores.getOrNull(posicao))
                }
            }

            Text("Mínimo de 2 jogadores para iniciar", color = VerdeTextoSuave, fontSize = 13.sp)
        }

        BotaoPrincipal(
            texto = "INICIAR PARTIDA",
            onClick = onIniciarPartida,
            modifier = Modifier.fillMaxWidth(),
            altura = Dimens.AlturaBotaoJogo
        )
    }
}

// Uma linha da lista: jogador sentado ou lugar vazio (quando jogador é nulo).
@Composable
private fun LinhaJogador(jogador: JogadorSala?) {
    if (jogador != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(VerdeMesa)
                    .border(2.dp, if (jogador.anfitriao) Dourado else VerdeBorda, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    jogador.nome.first().uppercase(),
                    color = if (jogador.anfitriao) Dourado else CremeTexto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                Text(jogador.nome, color = CremeTexto, fontSize = 15.sp)
                if (jogador.anfitriao) {
                    Text(" · anfitrião", color = VerdeTextoSuave, fontSize = 15.sp)
                }
            }
            Text(
                text = "pronto",
                color = Preto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Dourado)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .bordaTracejada(VerdeBorda, raio = 10.dp, espessura = 1.5.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier
                .size(34.dp)
                .bordaTracejada(VerdeBorda, raio = 17.dp))
            Text(
                "Aguardando jogador…",
                color = VerdeTextoSuave,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SalaScreenPreview() {
    Jogo21Theme { SalaScreen(onVoltar = {}, onIniciarPartida = {}) }
}
