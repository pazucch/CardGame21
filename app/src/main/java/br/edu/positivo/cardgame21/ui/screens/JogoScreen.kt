package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import br.edu.positivo.cardgame21.logic.Naipe
import br.edu.positivo.cardgame21.ui.components.*
import br.edu.positivo.cardgame21.ui.theme.*

data class JogadorNaMesaUIData(
    val nome: String,
    val cartas: List<Pair<String, Naipe>>,
    val situacao: String
)

@Composable
fun JogoScreen(
    onSair: () -> Unit,
    modifier: Modifier = Modifier,
    onPedirCarta: () -> Unit = {},
    onParar: () -> Unit = {},
    nomeMesa: String = "Mesa K7Q2",
    apostaAtual: Int = 50,
    saldo: Int = 1000,
    cartasVoce: List<Pair<String, Naipe>> = emptyList(),
    pontosVoce: Int = 0,
    jogadoresNaMesa: List<JogadorNaMesaUIData> = emptyList(),
    cartasBanca: List<Pair<String, Naipe>> = listOf("?" to Naipe.ESPADAS),
    pontosBanca: String = "?",
    nomeJogador: String = "Jogador",
    cartasRestantes: Int = 52,
    mensagemStatus: String = "Sua vez",
    dicaStatus: String = "Chegue o mais perto de 21 sem passar",
    onAumentarAposta: () -> Unit = {},
    onDiminuirAposta: () -> Unit = {},
    podeJogar: Boolean = true,
    rodadaEncerrada: Boolean = false,
    onNovaRodada: () -> Unit = {},
    podeAlterarAposta: Boolean = false,
    podeNovaRodada: Boolean = true,
    informacao: String? = null,
    erroSalvar: String? = null,
    onTentarSalvar: () -> Unit = {},
    onDescartarSalvamento: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize().background(VerdeMesa).systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.PaddingTela, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BarraTopo(nomeMesa, "Sair da mesa", onSair) {
            Pilula(saldo.toString(), icone = { FichaIcone() })
        }

        SecaoBanca(cartasBanca, pontosBanca, cartasRestantes)
        StatusRodada(mensagemStatus, dicaStatus)

        informacao?.let { Text(it, color = CremeTexto, fontSize = 14.sp) }
        erroSalvar?.let {
            Text(it, color = CremeTexto, fontSize = 14.sp)
            BotaoPrincipal("TENTAR SALVAR NOVAMENTE", onTentarSalvar, Modifier.fillMaxWidth())
            BotaoSecundario("CONTINUAR SEM SALVAR", onDescartarSalvamento, Modifier.fillMaxWidth())
        }

        LugaresDaMesa(jogadoresNaMesa)
        MaoJogador(nomeJogador, pontosVoce, cartasVoce, podeJogar)
        ControleAposta(apostaAtual, podeAlterarAposta, onDiminuirAposta, onAumentarAposta)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BotaoPrincipal(
                "PEDIR CARTA", onPedirCarta, Modifier.weight(1f),
                altura = Dimens.AlturaBotaoJogo, tamanhoTexto = 15.sp, enabled = podeJogar
            )
            BotaoSecundario(
                "PARAR", onParar, Modifier.weight(1f),
                altura = Dimens.AlturaBotaoJogo, tamanhoTexto = 15.sp, enabled = podeJogar
            )
        }
        if (rodadaEncerrada) {
            BotaoPrincipal("NOVA RODADA", onNovaRodada, Modifier.fillMaxWidth(), enabled = podeNovaRodada)
        }
    }
}

@Composable
private fun SecaoBanca(
    cartas: List<Pair<String, Naipe>>,
    pontos: String,
    cartasRestantes: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LinhaPontos("BANCA", pontos, VerdeTextoSuave)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            cartas.forEach { (valor, naipe) ->
                if (valor == "?") CartaCostas() else CartaFrente(valor, naipe)
            }
            Spacer(Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CartaCostas(largura = 40.dp, altura = 58.dp)
                Text("Baralho: $cartasRestantes", color = VerdeTextoSuave, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun LinhaPontos(titulo: String, pontos: String, corTitulo: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(titulo, color = corTitulo, fontSize = 13.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Text("Pontos: $pontos", color = CremeTexto, fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatusRodada(mensagem: String, dica: String) {
    Column(
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
            .clip(RoundedCornerShape(12.dp)).background(Dourado)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(mensagem, color = Preto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(dica, color = Color(0xFF2B2B2B), fontSize = 13.sp)
    }
}

@Composable
private fun LugaresDaMesa(jogadores: List<JogadorNaMesaUIData>) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        jogadores.forEach { jogador ->
            JogadorNaMesa(jogador.nome, jogador.cartas, jogador.situacao, Modifier.weight(1f))
        }
        repeat((2 - jogadores.size).coerceAtLeast(0)) {
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight()
                    .bordaTracejada(VerdeBorda, raio = 10.dp, espessura = 1.5.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Lugar vazio", color = VerdeTextoSuave, fontSize = 12.sp,
                    fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun MaoJogador(
    nome: String,
    pontos: Int,
    cartas: List<Pair<String, Naipe>>,
    podeJogar: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(VerdePainel).border(3.dp, Dourado, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        LinhaPontos("VOCÊ · $nome", pontos.toString(), Dourado)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            cartas.forEach { (valor, naipe) -> CartaFrente(valor, naipe) }
            if (podeJogar) CartaVazia("próxima carta")
        }
    }
}

@Composable
private fun ControleAposta(
    aposta: Int,
    podeAlterar: Boolean,
    diminuir: () -> Unit,
    aumentar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Aposta", color = CremeTexto, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotaoQuadrado("−", "Diminuir aposta", diminuir, podeAlterar && aposta > 10)
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(Vermelho)
                    .border(3.dp, Creme, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(aposta.toString(), color = Color.White,
                    fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            BotaoQuadrado("+", "Aumentar aposta", aumentar,
                podeAlterar && aposta <= Int.MAX_VALUE - 10)
        }
    }
}

@Composable
private fun JogadorNaMesa(
    nome: String,
    cartas: List<Pair<String, Naipe>>,
    situacao: String,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier.clip(forma).background(VerdePainel)
            .border(2.dp, VerdeBorda, forma).padding(8.dp),
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

@Composable
private fun BotaoQuadrado(
    simbolo: String,
    descricao: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val forma = RoundedCornerShape(10.dp)
    Box(
        modifier = Modifier.size(44.dp).alpha(if (enabled) 1f else 0.4f)
            .clip(forma).background(VerdePainel).border(2.dp, VerdeBorda, forma)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = descricao },
        contentAlignment = Alignment.Center
    ) {
        Text(simbolo, color = CremeTexto, fontSize = 22.sp)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun JogoScreenPreview() {
    Jogo21Theme {
        JogoScreen(
            onSair = {},
            cartasVoce = listOf("7" to Naipe.COPAS, "8" to Naipe.PAUS),
            pontosVoce = 15,
            jogadoresNaMesa = listOf(
                JogadorNaMesaUIData("maria", listOf("10" to Naipe.COPAS, "8" to Naipe.PAUS), "Parou · 18"),
                JogadorNaMesaUIData("joão", listOf("5" to Naipe.OUROS, "9" to Naipe.ESPADAS), "Aguardando · 14")
            )
        )
    }
}
