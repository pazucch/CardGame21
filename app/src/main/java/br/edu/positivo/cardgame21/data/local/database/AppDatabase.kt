package br.edu.positivo.cardgame21.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.edu.positivo.cardgame21.data.local.dao.JogadorDao
import br.edu.positivo.cardgame21.data.local.dao.PartidaDao
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity

@Database(
    entities = [JogadorEntity::class, PartidaEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jogadorDao(): JogadorDao
    abstract fun partidaDao(): PartidaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE jogadores ADD COLUMN saldo INTEGER NOT NULL DEFAULT 1000"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE partidas ADD COLUMN resultado TEXT NOT NULL DEFAULT 'DESCONHECIDO'")
                db.execSQL("ALTER TABLE partidas ADD COLUMN aposta INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE partidas ADD COLUMN variacaoSaldo INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cardgame_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
