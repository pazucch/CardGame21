package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.components.BarraTopo
import br.edu.positivo.cardgame21.ui.components.BotaoPrincipal
import br.edu.positivo.cardgame21.ui.components.BotaoSecundario
import br.edu.positivo.cardgame21.ui.components.CartaCostas
import br.edu.positivo.cardgame21.ui.components.CartaFrente
import br.edu.positivo.cardgame21.ui.components.CartaMini
import br.edu.positivo.cardgame21.ui.components.CartaVazia
import br.edu.positivo.cardgame21.ui.components.FichaIcone
import br.edu.positivo.cardgame21.ui.components.Naipe
import br.edu.positivo.cardgame21.ui.components.Pilula
import br.edu.positivo.cardgame21.ui.components.bordaTracejada
import br.edu.positivo.cardgame21.ui.theme.Creme
import br.edu.positivo.cardgame21.ui.theme.CremeTexto
import br.edu.positivo.cardgame21.ui.theme.Dimens
import br.edu.positivo.cardgame21.ui.theme.Dourado
import br.edu.positivo.cardgame21.ui.theme.Jogo21Theme
import br.edu.positivo.cardgame21.ui.theme.Preto
import br.edu.positivo.cardgame21.ui.theme.Vermelho
import br.edu.positivo.cardgame21.ui.theme.VerdeBorda
import br.edu.positivo.cardgame21.ui.theme.VerdeMesa
import br.edu.positivo.cardgame21.ui.theme.VerdePainel
import br.edu.positivo.cardgame21.ui.theme.VerdeTextoSuave

// Só o visual da mesa: as cartas e os números abaixo são fixos, de exemplo.
@Composable
fun JogoScreen(
    onSair: () -> Unit,
    modifier: Modifier = Modifier,
    onPedirCarta: () -> Unit = {},
    onParar: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerdeMesa)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.PaddingTela, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BarraTopo(
            titulo = "Mesa K7Q2",
            descricaoVoltar = "Sair da mesa",
            onVoltar = onSair
        ) { Pilula("500", icone = { FichaIcone() }) }

        // Banca
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("BANCA", color = VerdeTextoSuave, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Text("Pontos: ?", color = CremeTexto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CartaCostas()
                CartaFrente("K", Naipe.ESPADAS)
                Spacer(Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CartaCostas(largura = 40.dp, altura = 58.dp)
                    Text("Baralho: 46", color = VerdeTextoSuave, fontSize = 12.sp)
                }
            }
        }

        // Mensagem de status
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Dourado)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sua vez", color = Preto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text("Chegue o mais perto de 21 sem passar", color = Color(0xFF2B2B2B), fontSize = 13.sp)
        }

        // Outros jogadores
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            JogadorNaMesa(
                nome = "maria",
                cartas = listOf("10" to Naipe.COPAS, "8" to Naipe.PAUS),
                situacao = "Parou · 18",
                modifier = Modifier.weight(1f)
            )
            JogadorNaMesa(
                nome = "joão",
                cartas = listOf("5" to Naipe.OUROS, "9" to Naipe.ESPADAS),
                situacao = "Aguardando · 14",
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .bordaTracejada(VerdeBorda, raio = 10.dp, espessura = 1.5.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Lugar vazio",
                    color = VerdeTextoSuave,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Sua mão
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VerdePainel)
                .border(3.dp, Dourado, RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("VOCÊ · jogador21", color = Dourado, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Text("Pontos: 15", color = CremeTexto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CartaFrente("7", Naipe.COPAS)
                CartaFrente("8", Naipe.PAUS)
                CartaVazia("próxima carta")
            }
        }

        // Aposta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Aposta", color = CremeTexto, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotaoQuadrado("−", "Diminuir aposta")
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Vermelho)
                        .border(3.dp, Creme, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("50", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
                BotaoQuadrado("+", "Aumentar aposta")
            }
        }

        // Ações
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotaoPrincipal(
                texto = "PEDIR CARTA",
                onClick = onPedirCarta,
                modifier = Modifier.weight(1f),
                altura = Dimens.AlturaBotaoJogo,
                tamanhoTexto = 15.sp
            )
            BotaoSecundario(
                texto = "PARAR",
                onClick = onParar,
                modifier = Modifier.weight(1f),
                altura = Dimens.AlturaBotaoJogo,
                tamanhoTexto = 15.sp
            )
        }
    }
}

// Painel de um adversário: nome, cartas pequenas e situação.
@Composable
private fun JogadorNaMesa(
    nome: String,
    cartas: List<Pair<String, Naipe>>,
    situacao: String,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(VerdePainel)
            .border(2.dp, VerdeBorda, forma)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(nome, color = CremeTexto, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row {
            cartas.forEachIndexed { indice, (valor, naipe) ->
                CartaMini(valor, naipe, Modifier.offset(x = if (indice > 0) (-8).dp else 0.dp))
            }
        }
        Text(situacao, color = VerdeTextoSuave, fontSize = 12.sp)
    }
}

// Botão quadrado com um símbolo (− ou +). Ainda não faz nada.
@Composable
private fun BotaoQuadrado(simbolo: String, descricao: String) {
    val forma = RoundedCornerShape(10.dp)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(forma)
            .background(VerdePainel)
            .border(2.dp, VerdeBorda, forma)
            .clickable(role = Role.Button, onClick = {})
            .semantics { contentDescription = descricao },
        contentAlignment = Alignment.Center
    ) {
        Text(simbolo, color = CremeTexto, fontSize = 22.sp)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun JogoScreenPreview() {
    Jogo21Theme { JogoScreen(onSair = {}) }
}
