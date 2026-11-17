package br.edu.positivo.cardgame21.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.Factory {

    private val creators =
        mutableMapOf<Class<out ViewModel>, () -> ViewModel>()

    fun <T : ViewModel> register(
        modelClass: Class<T>,
        creator: () -> T
    ) {
        creators[modelClass] = creator
    }

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        val creator = creators[modelClass]
            ?: throw IllegalArgumentException(
                "ViewModel não registrado: ${modelClass.simpleName}"
            )

        return modelClass.cast(creator())
            ?: throw IllegalStateException(
                "Falha ao criar ${modelClass.simpleName}"
            )
    }
}