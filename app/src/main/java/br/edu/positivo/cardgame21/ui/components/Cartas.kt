package br.edu.positivo.cardgame21.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.theme.BordaCarta
import br.edu.positivo.cardgame21.ui.theme.CartaCostas
import br.edu.positivo.cardgame21.ui.theme.CartaCostasClara
import br.edu.positivo.cardgame21.ui.theme.Creme
import br.edu.positivo.cardgame21.ui.theme.Dimens
import br.edu.positivo.cardgame21.ui.theme.FonteTitulo
import br.edu.positivo.cardgame21.ui.theme.Preto
import br.edu.positivo.cardgame21.ui.theme.Vermelho
import br.edu.positivo.cardgame21.ui.theme.VerdeBorda
import br.edu.positivo.cardgame21.ui.theme.VerdeTextoSuave

// Os quatro naipes. O "︎" força o símbolo a aparecer como texto
enum class Naipe(val simbolo: String, val vermelho: Boolean) {
    ESPADAS("♠︎", false),
    COPAS("♥︎", true),
    OUROS("♦︎", true),
    PAUS("♣︎", false)
}

// Carta aberta, com valor e naipe.
@Composable
fun CartaFrente(
    valor: String,
    naipe: Naipe,
    modifier: Modifier = Modifier,
    largura: Dp = Dimens.CartaLargura,
    altura: Dp = Dimens.CartaAltura
) {
    val cor = if (naipe.vermelho) Vermelho else Preto
    val escala = if (largura < 60.dp) 0.7f else 1f
    val tamanhoValor = (18 * escala).sp
    val tamanhoNaipe = (28 * escala).sp
    val forma = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .size(largura, altura)
            .clip(forma)
            .background(Creme)
            .border(2.dp, BordaCarta, forma)
            .padding(horizontal = 7.dp, vertical = 5.dp)
            .semantics { contentDescription = "Carta $valor" },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = valor,
            color = cor,
            fontFamily = FonteTitulo,
            fontWeight = FontWeight.Bold,
            fontSize = tamanhoValor,
            lineHeight = tamanhoValor * 1.1f
        )
        Text(
            text = naipe.simbolo,
            color = cor,
            fontSize = tamanhoNaipe,
            lineHeight = tamanhoNaipe * 1.1f,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = valor,
            color = cor,
            fontFamily = FonteTitulo,
            fontWeight = FontWeight.Bold,
            fontSize = tamanhoValor,
            lineHeight = tamanhoValor * 1.1f,
            modifier = Modifier
                .align(Alignment.End)
                .rotate(180f)
        )
    }
}

// Carta pequena (para as mãos dos outros jogadores), com valor e naipe na mesma linha.
@Composable
fun CartaMini(valor: String, naipe: Naipe, modifier: Modifier = Modifier) {
    val forma = RoundedCornerShape(4.dp)
    Box(
        modifier = modifier
            .size(30.dp, 44.dp)
            .clip(forma)
            .background(Creme)
            .border(1.5.dp, BordaCarta, forma)
            .padding(horizontal = 3.dp, vertical = 2.dp)
    ) {
        Text(
            text = valor + naipe.simbolo,
            color = if (naipe.vermelho) Vermelho else Preto,
            fontFamily = FonteTitulo,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

// Carta virada para baixo.
@Composable
fun CartaCostas(
    modifier: Modifier = Modifier,
    largura: Dp = Dimens.CartaLargura,
    altura: Dp = Dimens.CartaAltura
) {
    val forma = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(largura, altura)
            .clip(forma)
            .background(CartaCostas)
            .border(2.dp, Creme, forma)
            .semantics { contentDescription = "Carta virada para baixo" }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .border(1.5.dp, CartaCostasClara, RoundedCornerShape(4.dp))
        )
    }
}

// Espaço tracejado onde a próxima carta vai aparecer
@Composable
fun CartaVazia(
    texto: String,
    modifier: Modifier = Modifier,
    largura: Dp = Dimens.CartaLargura,
    altura: Dp = Dimens.CartaAltura
) {
    Box(
        modifier = modifier
            .size(largura, altura)
            .bordaTracejada(VerdeBorda, raio = 8.dp)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = VerdeTextoSuave, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}
