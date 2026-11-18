package br.edu.positivo.cardgame21

import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.edu.positivo.cardgame21.data.local.database.AppDatabase
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import br.edu.positivo.cardgame21.data.session.SessaoUsuario
import br.edu.positivo.cardgame21.logic.Seguranca
import br.edu.positivo.cardgame21.ui.viewmodel.AuthViewModel
import br.edu.positivo.cardgame21.ui.viewmodel.AdminViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthAdminTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private lateinit var banco: AppDatabase
    private lateinit var jogadores: JogadorRepository
    private lateinit var partidas: PartidaRepository
    private lateinit var sessao: SessaoUsuario
    private lateinit var auth: AuthViewModel
    private lateinit var admin: AdminViewModel
    private val store = ViewModelStore()

    private fun naTela(acao: () -> Unit) = instrumentation.runOnMainSync { acao() }
    private fun aguardar(condicao: () -> Boolean) = runBlocking {
        withTimeout(10000) { while (!condicao()) delay(20) }
    }

    @Before fun preparar() {
        banco = Room.inMemoryDatabaseBuilder(instrumentation.targetContext, AppDatabase::class.java).build()
        jogadores = JogadorRepository(banco.jogadorDao())
        partidas = PartidaRepository(banco.partidaDao())
        sessao = SessaoUsuario()
        naTela {
            auth = AuthViewModel(jogadores, sessao)
            admin = AdminViewModel(jogadores, partidas, sessao)
            store.put("auth", auth)
            store.put("admin", admin)
        }
        aguardar { !auth.uiState.value.carregando }
    }

    @After fun finalizar() { naTela { store.clear() }; banco.close() }

    @Test fun cadastroValidaPersisteHashEEntraComoJogador() {
        naTela { auth.cadastrar("ab", "123") }
        assertNotNull(auth.uiState.value.mensagemErro)
        naTela { auth.cadastrar("  jogador21  ", "senha123") }
        aguardar { !auth.uiState.value.carregando }
        val jogador = runBlocking { jogadores.buscarPorNome("jogador21").getOrThrow()!! }
        assertEquals(Seguranca.hashSenha("senha123"), jogador.senhaHash)
        assertFalse(jogador.isAdmin)
        assertEquals(jogador.id, sessao.perfil.value.jogador?.id)
        naTela { auth.sair() }
        assertFalse(sessao.perfil.value.autenticado)
    }

    @Test fun senhaIncorretaENomeDuplicadoNaoAutenticam() {
        naTela { auth.cadastrar("jogador21", "senha123") }
        aguardar { !auth.uiState.value.carregando }
        naTela { auth.sair(); auth.entrar("jogador21", "errada") }
        aguardar { !auth.uiState.value.carregando }
        assertFalse(sessao.perfil.value.autenticado)
        assertEquals("Usuário ou senha incorretos.", auth.uiState.value.mensagemErro)
        naTela { auth.cadastrar("jogador21", "senha456") }
        aguardar { !auth.uiState.value.carregando }
        assertFalse(sessao.perfil.value.autenticado)
        assertTrue(auth.uiState.value.mensagemErro!!.contains("Já existe"))
        naTela { auth.entrar("jogador21", "senha123") }
        aguardar { !auth.uiState.value.carregando }
        assertTrue(sessao.perfil.value.autenticado)
    }

    @Test fun jogadorEConvidadoNaoExecutamOperacoesAdmin() {
        naTela { auth.cadastrar("jogador21", "senha123") }
        aguardar { !auth.uiState.value.carregando }
        val id = sessao.perfil.value.jogador!!.id
        naTela { admin.excluirJogador(id) }
        aguardar { !admin.uiState.value.carregando }
        assertNotNull(runBlocking { jogadores.buscarPorNome("jogador21").getOrThrow() })
        assertFalse(admin.uiState.value.autorizado)
        naTela { auth.sair(); auth.entrarComoConvidado(); admin.excluirJogador(id) }
        aguardar { !admin.uiState.value.carregando }
        assertFalse(admin.uiState.value.autorizado)
        assertNotNull(runBlocking { jogadores.buscarPorNome("jogador21").getOrThrow() })
    }

    @Test fun adminAtualizaEExcluiJogadoresEPartidas() {
        naTela { auth.cadastrar("jogador21", "senha123") }
        aguardar { !auth.uiState.value.carregando }
        val jogadorId = sessao.perfil.value.jogador!!.id
        val partidaId = runBlocking { partidas.inserirPartida(PartidaEntity(jogadorId = jogadorId, pontos = 15)).getOrThrow() }
        naTela { auth.sair(); auth.entrar("admin", "admin123") }
        aguardar { !auth.uiState.value.carregando && admin.uiState.value.partidas.isNotEmpty() }
        assertTrue(admin.uiState.value.autorizado)
        naTela { admin.atualizarJogador(jogadorId, "renomeado", "nova123", false) }
        aguardar { !admin.uiState.value.carregando }
        assertEquals(Seguranca.hashSenha("nova123"), runBlocking { jogadores.buscarPorNome("renomeado").getOrThrow()!!.senhaHash })
        naTela { admin.atualizarPartida(partidaId, "21") }
        aguardar { !admin.uiState.value.carregando }
        assertEquals(21, runBlocking { partidas.buscarPartidaPorId(partidaId).getOrThrow()!!.pontos })
        naTela { admin.excluirPartida(partidaId) }
        aguardar { !admin.uiState.value.carregando }
        assertNull(runBlocking { partidas.buscarPartidaPorId(partidaId).getOrThrow() })
        val segunda = runBlocking { partidas.inserirPartida(PartidaEntity(jogadorId = jogadorId, pontos = 10)).getOrThrow() }
        naTela { admin.excluirJogador(jogadorId) }
        aguardar { !admin.uiState.value.carregando }
        assertNull(runBlocking { jogadores.buscarPorNome("renomeado").getOrThrow() })
        assertNull(runBlocking { partidas.buscarPartidaPorId(segunda).getOrThrow() })
    }

    @Test fun adminNaoPodeExcluirOuRemoverSeuProprioAcesso() {
        naTela { auth.entrar("admin", "admin123") }
        aguardar { !auth.uiState.value.carregando && admin.uiState.value.jogadores.isNotEmpty() }
        val id = sessao.perfil.value.jogador!!.id
        naTela { admin.atualizarJogador(id, "admin", "", false) }
        aguardar { !admin.uiState.value.carregando }
        assertNotNull(admin.uiState.value.mensagemErro)
        naTela { admin.excluirJogador(id) }
        aguardar { !admin.uiState.value.carregando }
        assertTrue(runBlocking { jogadores.buscarPorNome("admin").getOrThrow()!!.isAdmin })
        naTela { auth.sair() }
        aguardar { !admin.uiState.value.autorizado }
        assertTrue(admin.uiState.value.jogadores.isEmpty())
    }
}
