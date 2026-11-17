
package br.edu.positivo.cardgame21.data.error

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import java.io.IOException
import retrofit2.HttpException

object ErrorHandler {

    fun tratar(erro: Throwable): AppError {
        return when (erro) {

            is SQLiteConstraintException ->
                AppError.NomeDuplicado(erro)

            is SQLiteException ->
                AppError.BancoDeDados(erro)

            is IOException ->
                AppError.Conexao(erro)

            is HttpException ->
                AppError.RespostaInvalida(erro)

            else ->
                AppError.Desconhecido(erro)
        }
    }
}