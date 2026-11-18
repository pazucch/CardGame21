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

    init {
        observarPlacar()
    }

    // Combina as duas fontes do Room: qualquer partida salva (ou jogador alterado/removido)
    // reemite os dois Flows e o ranking é recalculado sozinho, sem precisar recarregar.
    private fun observarPlacar() {
        viewModelScope.launch {
            combine(
                partidaRepository.listarPlacar(),
                jogadorRepository.listarJogadores()
            ) { partidas, jogadores ->
                val falha = partidas.exceptionOrNull() ?: jogadores.exceptionOrNull()
                if (falha != null) {
                    PlacarUiState(carregando = false, erro = mensagemDe(falha))
                } else {
                    PlacarUiState(
                        carregando = false,
                        itens = montarRanking(
                            partidas = partidas.getOrThrow(),
                            jogadores = jogadores.getOrThrow()
                        )
                    )
                }
            }
                .catch { falha ->
                    // Rede de segurança: nada que falhe aqui pode derrubar o app.
                    emit(PlacarUiState(carregando = false, erro = mensagemDe(falha)))
                }
                .collect { novo -> _uiState.update { novo } }
        }
    }

    private fun mensagemDe(falha: Throwable): String =
        (falha as? AppError ?: ErrorHandler.tratar(falha)).message
            ?: "Ocorreu um erro inesperado."
}