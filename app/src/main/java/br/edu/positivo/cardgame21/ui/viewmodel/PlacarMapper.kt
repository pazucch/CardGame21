package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity

const val NOME_JOGADOR_REMOVIDO = "Jogador removido"

// Junta partidas + jogadores e devolve o ranking já ordenado e numerado.
// Função pura (sem Android, sem coroutines): fácil de testar e de reaproveitar.
fun montarRanking(
    partidas: List<PartidaEntity>,
    jogadores: List<JogadorEntity>
): List<PlacarItem> {
    // id -> nome, para o "join" em memória (O(1) por partida)
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
        // Ordenação estável em dois passos: o desempate (quem fez primeiro fica na frente)
        // vem antes da ordenação principal (maior pontuação primeiro).
        .sortedBy { it.dataPartida }
        .sortedByDescending { it.pontos }
        .mapIndexed { indice, item -> item.copy(posicao = indice + 1) }
}