package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity

data class PlacarItem(
    val posicao: Int,
    val partidaId: Long,
    val jogadorId: Long,
    val nome: String,
    val pontos: Int,
    val dataPartida: Long,
    val partida: PartidaEntity? = null
)

enum class FiltroPlacar(val rotulo: String) {
    TODAS("Todas"),
    ULTIMAS_24H("24 horas"),
    ULTIMOS_7_DIAS("7 dias")
}

data class PlacarMetricas(
    val totalPartidas: Int = 0,
    val jogadoresDistintos: Int = 0,
    val melhorPontuacao: Int = 0,
    val mediaPontos: Double = 0.0
)

data class PlacarUiState(
    val carregando: Boolean = true,
    val itens: List<PlacarItem> = emptyList(),
    val metricas: PlacarMetricas = PlacarMetricas(),
    val busca: String = "",
    val filtro: FiltroPlacar = FiltroPlacar.TODAS,
    val partidaSelecionada: PlacarItem? = null,
    val erro: String? = null
)