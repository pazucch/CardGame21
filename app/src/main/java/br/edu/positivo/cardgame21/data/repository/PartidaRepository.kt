package br.edu.positivo.cardgame21.data.repository

import br.edu.positivo.cardgame21.data.local.dao.PartidaDao
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import br.edu.positivo.cardgame21.data.error.ErrorHandler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PartidaRepository(
    private val partidaDao: PartidaDao
) {
    fun listarPlacar(): Flow<Result<List<PartidaEntity>>> =
        partidaDao.listarPlacar()
            .map { lista -> Result.success(lista) }
            .catch { erro ->
                if (erro is CancellationException) throw erro

                emit(Result.failure(ErrorHandler.tratar(erro)))
            }
    fun listarPartidasPorJogador(
        jogadorId: Long
    ): Flow<Result<List<PartidaEntity>>> =
        partidaDao.listarPorJogador(jogadorId)
            .map { partidas ->
                Result.success(partidas)
            }
            .catch { erro ->
                if (erro is CancellationException) throw erro

                emit(Result.failure(ErrorHandler.tratar(erro)))
            }

    suspend fun buscarPartidaPorId(
        id: Long
    ): Result<PartidaEntity?> {
        return try {
            Result.success(
                partidaDao.buscarPorId(id)
            )
        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }

    suspend fun inserirPartida(partida: PartidaEntity): Result<Long> {
        return try {
            val id = partidaDao.inserir(partida)
            Result.success(id)
        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }
    suspend fun atualizarPartida(partida: PartidaEntity): Result<Unit> {
        return try {
            partidaDao.atualizar(partida)
            Result.success(Unit)
        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }
    suspend fun deletarPartida(partida: PartidaEntity): Result<Unit> {
        return try {
            partidaDao.deletar(partida)
            Result.success(Unit)
        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            Result.failure(ErrorHandler.tratar(erro))
        }
    }
}