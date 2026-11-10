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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.components.BotaoLink
import br.edu.positivo.cardgame21.ui.components.BotaoPrincipal
import br.edu.positivo.cardgame21.ui.components.BotaoSecundario
import br.edu.positivo.cardgame21.ui.components.CampoTexto
import br.edu.positivo.cardgame21.ui.components.CaixaErro
import br.edu.positivo.cardgame21.ui.components.CartaFrente
import br.edu.positivo.cardgame21.ui.components.Naipe
import br.edu.positivo.cardgame21.ui.theme.CremeTexto
import br.edu.positivo.cardgame21.ui.theme.Dimens
import br.edu.positivo.cardgame21.ui.theme.Dourado
import br.edu.positivo.cardgame21.ui.theme.FonteTitulo
import br.edu.positivo.cardgame21.ui.theme.Jogo21Theme
import br.edu.positivo.cardgame21.ui.theme.VerdeMesa
import br.edu.positivo.cardgame21.ui.theme.VerdePainel
import br.edu.positivo.cardgame21.ui.theme.VerdeTextoSuave

@Composable
fun LoginScreen(
    onEntrar: () -> Unit,
    onCriarConta: () -> Unit,
    onConvidado: () -> Unit,
    modifier: Modifier = Modifier,
    mensagemErro: String? = null
) {
    var usuario by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerdeMesa)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.PaddingTelaLogin, vertical = Dimens.PaddingTelaLogin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Logo, nome e frase de boas-vindas
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(VerdePainel)
                    .border(3.dp, Dourado, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "21",
                    color = Dourado,
                    fontFamily = FonteTitulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp
                )
            }
            Text(
                text = "Vinte e Um",
                color = CremeTexto,
                fontFamily = FonteTitulo,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp
            )
            Text("Entre para sentar à mesa", color = VerdeTextoSuave, fontSize = 15.sp)
        }

        // Desenho decorativo: três cartas
        Row(
            modifier = Modifier.height(92.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            CartaFrente("A", Naipe.ESPADAS, Modifier.rotate(-10f), largura = 56.dp, altura = 80.dp)
            CartaFrente("10", Naipe.COPAS, largura = 56.dp, altura = 80.dp)
            CartaFrente("K", Naipe.OUROS, Modifier.rotate(10f), largura = 56.dp, altura = 80.dp)
        }

        // Formulário
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoTexto(
                rotulo = "Apelido ou e-mail",
                valor = usuario,
                onValorChange = { usuario = it },
                placeholder = "ex.: jogador21"
            )
            CampoTexto(
                rotulo = "Senha",
                valor = senha,
                onValorChange = { senha = it },
                placeholder = "••••••",
                senha = true
            )
            if (mensagemErro != null) CaixaErro(mensagemErro)
        }

        // Ações
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotaoPrincipal("ENTRAR", onEntrar, Modifier.fillMaxWidth())
            BotaoSecundario("Criar conta", onCriarConta, Modifier.fillMaxWidth())
            BotaoLink("Jogar como convidado", onConvidado)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginScreenPreview() {
    Jogo21Theme {
        LoginScreen(onEntrar = {}, onCriarConta = {}, onConvidado = {}, mensagemErro = "Usuário ou senha incorretos")
    }
}
