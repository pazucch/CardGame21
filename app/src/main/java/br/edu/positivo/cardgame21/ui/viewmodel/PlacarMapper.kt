package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity

const val NOME_JOGADOR_REMOVIDO = "Jogador removido"

fun montarRanking(
    partidas: List<PartidaEntity>,
    jogadores: List<JogadorEntity>,
    ehVitoria: (PartidaEntity) -> Boolean = { true }
): List<PlacarItem> {
    val nomesPorId = jogadores.associate { it.id to it.nome }

    return partidas
        .filter(ehVitoria)
        .groupBy { it.jogadorId }
        .mapNotNull { (jogadorId, partidasDoJogador) ->
            val ultimaPartida = partidasDoJogador.maxByOrNull { it.dataPartida } ?: return@mapNotNull null
            val totalVitorias = partidasDoJogador.size

            PlacarItem(
                posicao = 0,
                partidaId = ultimaPartida.id,
                jogadorId = jogadorId,
                nome = nomesPorId[jogadorId] ?: NOME_JOGADOR_REMOVIDO,
                pontos = totalVitorias,
                dataPartida = ultimaPartida.dataPartida
            )
        }
        .sortedBy { it.dataPartida }
        .sortedByDescending { it.pontos }
        .mapIndexed { indice, item -> item.copy(posicao = indice + 1) }
}