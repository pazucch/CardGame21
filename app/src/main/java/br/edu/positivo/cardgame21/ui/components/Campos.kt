package br.edu.positivo.cardgame21.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.positivo.cardgame21.ui.theme.CinzaBotaoCampo
import br.edu.positivo.cardgame21.ui.theme.CinzaPlaceholder
import br.edu.positivo.cardgame21.ui.theme.Creme
import br.edu.positivo.cardgame21.ui.theme.CremeTexto
import br.edu.positivo.cardgame21.ui.theme.Dourado
import br.edu.positivo.cardgame21.ui.theme.ErroBorda
import br.edu.positivo.cardgame21.ui.theme.ErroFundo
import br.edu.positivo.cardgame21.ui.theme.ErroTexto
import br.edu.positivo.cardgame21.ui.theme.Preto
import br.edu.positivo.cardgame21.ui.theme.VerdeBorda

// Campo de texto claro, sem rótulo. Com [senha] = true, ganha o botão "mostrar/ocultar".
@Composable
fun CampoEntrada(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    senha: Boolean = false
) {
    var mostrar by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier,
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (senha) KeyboardType.Password else KeyboardType.Text
        ),
        visualTransformation = if (senha && !mostrar) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (senha) {
            {
                TextButton(onClick = { mostrar = !mostrar }) {
                    Text(
                        text = if (mostrar) "ocultar" else "mostrar",
                        color = CinzaBotaoCampo,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Preto,
            unfocusedTextColor = Preto,
            focusedContainerColor = Creme,
            unfocusedContainerColor = Creme,
            focusedBorderColor = Dourado,
            unfocusedBorderColor = VerdeBorda,
            cursorColor = Preto,
            focusedPlaceholderColor = CinzaPlaceholder,
            unfocusedPlaceholderColor = CinzaPlaceholder
        )
    )
}

// Rótulo em cima + CampoEntrada embaixo
@Composable
fun CampoTexto(
    rotulo: String,
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    senha: Boolean = false
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(rotulo, color = CremeTexto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        CampoEntrada(
            valor = valor,
            onValorChange = onValorChange,
            placeholder = placeholder,
            modifier = Modifier.fillMaxWidth(),
            senha = senha
        )
    }
}

// Caixa avermelhada para mensagens de erro.
@Composable
fun CaixaErro(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ErroFundo)
            .border(1.5.dp, ErroBorda, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(texto, color = ErroTexto, fontSize = 13.sp)
    }
}
