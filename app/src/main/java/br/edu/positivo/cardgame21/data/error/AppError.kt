package br.edu.positivo.cardgame21.data.error

sealed class AppError(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NomeDuplicado(
        cause: Throwable? = null
    ) : AppError(
        "Já existe um jogador com esse nome.",
        cause
    )

    class BancoDeDados(
        cause: Throwable? = null
    ) : AppError(
        "Não foi possível acessar o banco de dados.",
        cause
    )

    class Conexao(
        cause: Throwable? = null
    ) : AppError(
        "Não foi possível estabelecer conexão.",
        cause
    )

    class RespostaInvalida(
        cause: Throwable? = null
    ) : AppError(
        "O serviço retornou uma resposta inválida.",
        cause
    )

    class Desconhecido(
        cause: Throwable? = null
    ) : AppError(
        "Ocorreu um erro inesperado.",
        cause
    )
}
