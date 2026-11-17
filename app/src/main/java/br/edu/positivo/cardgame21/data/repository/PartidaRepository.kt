package br.edu.positivo.cardgame21.data.repository

import br.edu.positivo.cardgame21.data.local.dao.PartidaDao
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PartidaRepository(
    private val partidaDao: PartidaDao
) {
    fun listarPlacar(): Flow<Result<List<PartidaEntity>>> =
        partidaDao.listarPlacar()
            .map { lista -> Result.success(lista) }
            .catch { e -> emit(Result.failure(e)) }

    fun listarPartidasPorJogador(
        jogadorId: Long
    ): Flow<Result<List<PartidaEntity>>> =
        partidaDao.listarPorJogador(jogadorId)
            .map { partidas ->
                Result.success(partidas)
            }
            .catch { erro ->
                emit(Result.failure(erro))
            }

    suspend fun buscarPartidaPorId(
        id: Long
    ): Result<PartidaEntity?> {
        return try {
            Result.success(
                partidaDao.buscarPorId(id)
            )
        } catch (erro: Exception) {
            Result.failure(erro)
        }
    }

    suspend fun inserirPartida(partida: PartidaEntity): Result<Long> {
        return try {
            val id = partidaDao.inserir(partida)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun atualizarPartida(partida: PartidaEntity): Result<Unit> {
        return try {
            partidaDao.atualizar(partida)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deletarPartida(partida: PartidaEntity): Result<Unit> {
        return try {
            partidaDao.deletar(partida)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}