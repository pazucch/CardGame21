package br.edu.positivo.cardgame21

import android.app.Application
import br.edu.positivo.cardgame21.di.AppContainer

class CardGameApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = AppContainer(applicationContext)
    }
}