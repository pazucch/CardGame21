package br.edu.positivo.cardgame21.logic

import java.security.MessageDigest

object Seguranca {
    // Nunca guardar a senha em texto puro no banco.
    fun hashSenha(senha: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(senha.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
