# CardGame21

[Português](#português) | [English](#english)

## Português

Jogo de Blackjack 21 para Android, desenvolvido como projeto acadêmico na Universidade Positivo.

Crie uma mesa local, escolha a aposta e jogue contra a banca. É possível criar uma conta ou jogar como convidado. As vitórias dos jogadores cadastrados ficam no ranking, que tem busca por nome e filtro por período. O perfil de administrador permite editar e excluir jogadores e partidas.

O app usa Kotlin, Jetpack Compose e Room. As cartas vêm da Deck of Cards API via Retrofit, com um baralho local disponível quando a API falha.

Para rodar, abra o projeto no Android Studio, sincronize o Gradle e execute o módulo `app` em um emulador ou dispositivo Android.

## English

An Android Blackjack 21 game built as a class project at Universidade Positivo.

Create a local table, choose a bet, and play against the dealer. You can create an account or play as a guest. Wins from registered players appear on the leaderboard, which you can search by name and filter by date range. Admins can edit and delete players and game records.

The app uses Kotlin, Jetpack Compose, and Room. It gets cards from the Deck of Cards API through Retrofit and uses a local deck if the API request fails.

To run it, open the project in Android Studio, sync Gradle, and run the `app` module on an Android emulator or device.

## Licença / License

[MIT](LICENSE.md)
