package br.edu.positivo.cardgame21.ui.viewmodel

// Uma linha do ranking: uma partida já unida ao nome do jogador que a jogou.
data class PlacarItem(
    val posicao: Int,
    val partidaId: Long,
    val jogadorId: Long,
    val nome: String,
    val pontos: Int,
    val dataPartida: Long
)

// Estado único e reativo da tela de Ranking.
data class PlacarUiState(
    val carregando: Boolean = true,
    val itens: List<PlacarItem> = emptyList(),
    val erro: String? = null
)