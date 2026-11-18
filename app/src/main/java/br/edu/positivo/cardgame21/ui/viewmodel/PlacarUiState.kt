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

// Chips de filtro da tela de Ranking.
enum class FiltroPlacar(val rotulo: String) {
    TODAS("Todas"),
    ULTIMAS_24H("24 horas"),
    ULTIMOS_7_DIAS("7 dias")
}

// Números agregados sobre as partidas que estão visíveis na tela.
data class PlacarMetricas(
    val totalPartidas: Int = 0,
    val jogadoresDistintos: Int = 0,
    val melhorPontuacao: Int = 0,
    val mediaPontos: Double = 0.0
)

// Estado único e reativo da tela de Ranking.
data class PlacarUiState(
    val carregando: Boolean = true,
    val itens: List<PlacarItem> = emptyList(),
    val metricas: PlacarMetricas = PlacarMetricas(),
    val busca: String = "",
    val filtro: FiltroPlacar = FiltroPlacar.TODAS,
    val erro: String? = null
)