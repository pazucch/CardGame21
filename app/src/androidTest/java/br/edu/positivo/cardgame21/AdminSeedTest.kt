
package br.edu.positivo.cardgame21

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.edu.positivo.cardgame21.data.local.database.AppDatabase
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminSeedTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: JogadorRepository

    @Before
    fun configurar() {
        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        repository = JogadorRepository(database.jogadorDao())
    }

    @After
    fun finalizar() {
        database.close()
    }

    @Test
    fun deveCriarAdministradorInicial() = runBlocking {
        val resultado = repository.criarAdminInicial(
            nome = "AdminTeste",
            senhaHash = "hash-ficticio-exclusivo-do-teste"
        )

        assertTrue(resultado.isSuccess)
        assertEquals(true, resultado.getOrNull())

        val admin = database.jogadorDao()
            .buscaPorNome("AdminTeste")

        assertNotNull(admin)
        assertTrue(admin!!.isAdmin)
        assertEquals(1, database.jogadorDao().contarAdministradores())
    }

    @Test
    fun naoDeveCriarAdministradorDuplicado() = runBlocking {
        val primeiro = repository.criarAdminInicial(
            nome = "AdminTeste",
            senhaHash = "hash-ficticio-exclusivo-do-teste"
        )

        val segundo = repository.criarAdminInicial(
            nome = "OutroAdmin",
            senhaHash = "outro-hash-ficticio"
        )

        assertEquals(true, primeiro.getOrNull())
        assertEquals(false, segundo.getOrNull())

        assertEquals(
            1,
            database.jogadorDao().contarAdministradores()
        )

        assertNull(
            database.jogadorDao().buscaPorNome("OutroAdmin")
        )
    }

    @Test
    fun naoDeveAceitarDadosVazios() = runBlocking {
        val resultado = repository.criarAdminInicial(
            nome = "",
            senhaHash = ""
        )

        assertTrue(resultado.isFailure)

        assertEquals(
            0,
            database.jogadorDao().contarAdministradores()
        )
    }
}