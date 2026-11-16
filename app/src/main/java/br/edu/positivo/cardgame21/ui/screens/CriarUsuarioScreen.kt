package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.components.BarraTopo
import br.edu.positivo.cardgame21.ui.components.BotaoPrincipal
import br.edu.positivo.cardgame21.ui.components.CampoTexto
import br.edu.positivo.cardgame21.ui.theme.*

@Composable
fun CriarUsuarioScreen(
    onVoltar: () -> Unit,
    onSalvarUsuario: (nome: String, isAdmin: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var isAdmin by rememberSaveable { mutableStateOf(false) }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Criar como Administrador?", color = CremeTexto, fontSize = 14.sp)
                Switch(
                    checked = isAdmin,
                    onCheckedChange = { isAdmin = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Dourado,
                        checkedTrackColor = VerdeMesa
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BotaoPrincipal(
            texto = "SALVAR PERFIL",
            onClick = { if (nome.isNotBlank()) onSalvarUsuario(nome, isAdmin) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CriarUsuarioScreenPreview() {
    Jogo21Theme {
        CriarUsuarioScreen(onVoltar = {}, onSalvarUsuario = { _, _ -> })
    }
}