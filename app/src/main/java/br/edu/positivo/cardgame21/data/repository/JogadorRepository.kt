package br.edu.positivo.cardgame21.data.repository

import br.edu.positivo.cardgame21.data.local.dao.JogadorDao
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import br.edu.positivo.cardgame21.data.error.AppError
import br.edu.positivo.cardgame21.data.error.ErrorHandler
import kotlinx.coroutines.CancellationException

class JogadorRepository(
    private val jogadorDao: JogadorDao
) {
    suspend fun criarAdminInicial(
        nome: String,
        senhaHash: String
    ): Result<Boolean> {
        return try {
            val criado = jogadorDao.inserirAdminInicial(
                nome = nome,
                senhaHash = senhaHash
            )

            Result.success(criado)

        } catch (erro: kotlinx.coroutines.CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(erro)
        }
    }

    fun listarJogadores(): Flow<Result<List<JogadorEntity>>> =
        jogadorDao.listarTodos()
            .map { lista -> Result.success(lista) }
            .catch { e -> emit(Result.failure(e)) }

    suspend fun buscarPorNome(nome: String): Result<JogadorEntity?> {
        return try {
            Result.success(jogadorDao.buscaPorNome(nome))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun pesquisarJogadores(
        nome: String
    ): Flow<Result<List<JogadorEntity>>> =
        jogadorDao.pesquisarPorNome(nome)
            .map { lista ->
                Result.success(lista)
            }
            .catch { erro ->
                emit(Result.failure(erro))
            }


    suspend fun inserirJogador(jogador: JogadorEntity): Result<Long> {
        return try {
            Result.success(jogadorDao.inserir(jogador))

        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }


    suspend fun atualizarJogador(jogador: JogadorEntity): Result<Unit> {
        return try {
            jogadorDao.atualizar(jogador)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarJogador(jogador: JogadorEntity): Result<Unit> {
        return try {
            jogadorDao.deletar(jogador)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
