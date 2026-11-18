package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity

const val NOME_JOGADOR_REMOVIDO = "Jogador removido"

fun montarRanking(
    partidas: List<PartidaEntity>,
    jogadores: List<JogadorEntity>
): List<PlacarItem> {
    val nomesPorId = jogadores.associate { it.id to it.nome }

    return partidas
        .map { partida ->
            PlacarItem(
                posicao = 0,
                partidaId = partida.id,
                jogadorId = partida.jogadorId,
                nome = nomesPorId[partida.jogadorId] ?: NOME_JOGADOR_REMOVIDO,
                pontos = partida.pontos,
                dataPartida = partida.dataPartida
            )
        }
        .sortedBy { it.dataPartida }
        .sortedByDescending { it.pontos }
        .mapIndexed { indice, item -> item.copy(posicao = indice + 1) }
}