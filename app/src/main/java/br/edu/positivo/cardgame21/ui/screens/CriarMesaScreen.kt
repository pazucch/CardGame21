package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.components.BarraTopo
import br.edu.positivo.cardgame21.ui.components.BotaoPrincipal
import br.edu.positivo.cardgame21.ui.theme.*

@Composable
fun CriarMesaScreen(
    onVoltar: () -> Unit,
    onCriarMesa: (aposta: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var aposta by rememberSaveable { mutableStateOf(50) }

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
            titulo = "Criar Mesa",
            descricaoVoltar = "Voltar para a Sala",
            onVoltar = onVoltar
        ) {}

        // Painel de configuração
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VerdePainel)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Configurações da Mesa", color = Dourado, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Text("Valor da Aposta Inicial", color = CremeTexto, fontSize = 15.sp)

            // Controle de aposta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotaoQuadradoCriarMesa("-", enabled = aposta > 10) { aposta -= 10 }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Vermelho),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$aposta", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(20.dp))
                BotaoQuadradoCriarMesa("+", enabled = aposta <= Int.MAX_VALUE - 10) { aposta += 10 }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BotaoPrincipal(
            texto = "ABRIR MESA",
            onClick = { onCriarMesa(aposta) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BotaoQuadradoCriarMesa(simbolo: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(RoundedCornerShape(8.dp))
            .background(VerdeMesa)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(simbolo, color = CremeTexto, fontSize = 24.sp)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CriarMesaScreenPreview() {
    Jogo21Theme {
        CriarMesaScreen(onVoltar = {}, onCriarMesa = {})
    }
}
