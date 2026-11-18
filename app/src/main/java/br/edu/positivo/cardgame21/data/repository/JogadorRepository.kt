package br.edu.positivo.cardgame21.data.repository

import br.edu.positivo.cardgame21.data.local.dao.JogadorDao
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import br.edu.positivo.cardgame21.data.error.ErrorHandler
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
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

        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }

    fun listarJogadores(): Flow<Result<List<JogadorEntity>>> =
        jogadorDao.listarTodos()
            .map { lista -> Result.success(lista) }
            .catch { erro ->
                if (erro is CancellationException) throw erro

                emit(Result.failure(ErrorHandler.tratar(erro)))
            }

    suspend fun buscarPorNome(nome: String): Result<JogadorEntity?> {
        return try {
            Result.success(jogadorDao.buscaPorNome(nome))
        } catch (erro: CancellationException) {
            throw erro
        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
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
                if (erro is CancellationException) throw erro

                emit(Result.failure(ErrorHandler.tratar(erro)))
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
        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }

    suspend fun deletarJogador(jogador: JogadorEntity): Result<Unit> {
        return try {
            jogadorDao.deletar(jogador)
            Result.success(Unit)
        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }

    suspend fun buscarSaldo(id: Long): Result<Int> {
        return try {
            val saldo = jogadorDao.buscarSaldo(id)
                ?: return Result.failure(
                    IllegalStateException("Jogador não encontrado.")
                )

            Result.success(saldo)
        } catch (erro: CancellationException) {
            throw erro
        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }

    suspend fun alterarSaldo(
        id: Long,
        variacao: Int
    ): Result<Int> {
        return try {
            val linhasAfetadas = jogadorDao.alterarSaldo(id, variacao)

            if (linhasAfetadas == 0) {
                return Result.failure(
                    IllegalStateException("Não foi possível atualizar o saldo.")
                )
            }

            val saldoAtualizado = jogadorDao.buscarSaldo(id)
                ?: return Result.failure(
                    IllegalStateException("Jogador não encontrado.")
                )

            Result.success(saldoAtualizado)
        } catch (erro: CancellationException) {
            throw erro
        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }

    suspend fun concluirRodada(
        partida: PartidaEntity,
        variacao: Int
    ): Result<Int> {
        return try {
            Result.success(jogadorDao.concluirRodada(partida, variacao))
        } catch (erro: CancellationException) {
            throw erro
        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }
}
