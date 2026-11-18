package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.components.BarraTopo
import br.edu.positivo.cardgame21.ui.components.BotaoPrincipal
import br.edu.positivo.cardgame21.ui.components.CaixaErro
import br.edu.positivo.cardgame21.ui.components.CampoTexto
import br.edu.positivo.cardgame21.ui.theme.*

@Composable
fun CriarUsuarioScreen(
    onVoltar: () -> Unit,
    onSalvarUsuario: (nome: String, senha: String, isAdmin: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    mensagemErro: String? = null,
    carregando: Boolean = false
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }

    var erroLocal by rememberSaveable { mutableStateOf<String?>(null) }

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
            titulo = "Criar Conta",
            descricaoVoltar = "Voltar",
            onVoltar = onVoltar
        ) {}

        Text(
            "Cadastre seu perfil para começar a jogar",
            color = CremeTexto,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Formulário
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VerdePainel)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoTexto(
                rotulo = "Nome de Jogador",
                valor = nome,
                onValorChange = { nome = it },
                placeholder = "ex.: jogador21"
            )

            CampoTexto(
                rotulo = "Senha",
                valor = senha,
                onValorChange = { senha = it },
                placeholder = "••••••",
                senha = true
            )


        }

        val erro = erroLocal ?: mensagemErro
        if (erro != null) CaixaErro(erro)

        Spacer(modifier = Modifier.weight(1f))

        BotaoPrincipal(
            texto = if (carregando) "SALVANDO..." else "SALVAR PERFIL",
            onClick = {
                if (carregando) return@BotaoPrincipal
                if (nome.isBlank() || senha.isBlank()) {
                    erroLocal = "Preencha o nome e a senha"
                } else {
                    erroLocal = null
                    onSalvarUsuario(nome.trim(), senha, false)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CriarUsuarioScreenPreview() {
    Jogo21Theme {
        CriarUsuarioScreen(onVoltar = {}, onSalvarUsuario = { _, _, _ -> })
    }
}
