package br.edu.positivo.cardgame21.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.positivo.cardgame21.data.error.AppError
import br.edu.positivo.cardgame21.data.error.ErrorHandler
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlacarViewModel(
    private val partidaRepository: PartidaRepository,
    private val jogadorRepository: JogadorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlacarUiState())
    val uiState: StateFlow<PlacarUiState> = _uiState.asStateFlow()

    private val _busca = MutableStateFlow("")
    private val _filtro = MutableStateFlow(FiltroPlacar.TODAS)

    init {
        observarPlacar()
    }

    fun aoMudarBusca(texto: String) {
        _busca.value = texto
    }

    fun aoSelecionarFiltro(filtro: FiltroPlacar) {
        _filtro.value = filtro
    }

    fun aoSelecionarPartida(item: PlacarItem) {
        _uiState.update { it.copy(partidaSelecionada = item) }
    }

    fun aoFecharDetalhe() {
        _uiState.update { it.copy(partidaSelecionada = null) }
    }

    private fun observarPlacar() {
        viewModelScope.launch {
            combine(
                partidaRepository.listarPlacar(),
                jogadorRepository.listarJogadores(),
                _busca,
                _filtro
            ) { partidas, jogadores, busca, filtro ->
                val detalheAtual = _uiState.value.partidaSelecionada
                val falha = partidas.exceptionOrNull() ?: jogadores.exceptionOrNull()
                if (falha != null) {
                    PlacarUiState(
                        carregando = false,
                        busca = busca,
                        filtro = filtro,
                        partidaSelecionada = detalheAtual,
                        erro = mensagemDe(falha)
                    )
                } else {
                    val ranking = montarRanking(
                        partidas = partidas.getOrThrow(),
                        jogadores = jogadores.getOrThrow()
                    )
                    val visiveis = ranking.filtrarPor(busca, filtro)
                    PlacarUiState(
                        carregando = false,
                        itens = visiveis,
                        metricas = calcularMetricas(visiveis),
                        busca = busca,
                        filtro = filtro,
                        partidaSelecionada = detalheAtual
                    )
                }
            }
                .catch { falha ->
                    emit(
                        PlacarUiState(
                            carregando = false,
                            partidaSelecionada = _uiState.value.partidaSelecionada,
                            erro = mensagemDe(falha)
                        )
                    )
                }
                .collect { novo -> _uiState.update { novo } }
        }
    }

    private fun mensagemDe(falha: Throwable): String =
        (falha as? AppError ?: ErrorHandler.tratar(falha)).message
            ?: "Ocorreu um erro inesperado."
}

private const val UM_DIA_MS = 24L * 60 * 60 * 1000

private fun List<PlacarItem>.filtrarPor(
    busca: String,
    filtro: FiltroPlacar
): List<PlacarItem> {
    val agora = System.currentTimeMillis()
    val desde = when (filtro) {
        FiltroPlacar.TODAS -> Long.MIN_VALUE
        FiltroPlacar.ULTIMAS_24H -> agora - UM_DIA_MS
        FiltroPlacar.ULTIMOS_7_DIAS -> agora - 7 * UM_DIA_MS
    }
    val termo = busca.trim()
    return this
        .filter { it.dataPartida >= desde }
        .filter { termo.isEmpty() || it.nome.contains(termo, ignoreCase = true) }
        .sortedBy { it.posicao }
}

private fun calcularMetricas(itens: List<PlacarItem>): PlacarMetricas =
    if (itens.isEmpty()) {
        PlacarMetricas()
    } else {
        PlacarMetricas(
            totalPartidas = itens.size,
            jogadoresDistintos = itens.map { it.jogadorId }.distinct().size,
            melhorPontuacao = itens.maxOf { it.pontos },
            mediaPontos = itens.map { it.pontos }.average()
        )
    }