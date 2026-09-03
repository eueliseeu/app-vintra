# Vintra

Aplicativo Android de gestão financeira pessoal, desenvolvido com Kotlin e Jetpack Compose. O projeto tem como objetivo ajudar o usuário a acompanhar receitas, despesas e o saldo financeiro em um só lugar, com os dados armazenados e sincronizados pelo Firebase.

> Projeto em desenvolvimento.

> A base atual já está configurada com Kotlin e Jetpack Compose. A integração com Firebase ainda precisa ser adicionada ao projeto.

## Funcionalidades previstas

- Cadastro e acompanhamento de receitas e despesas
- Organização das movimentações por categoria
- Visualização do saldo e do resumo financeiro
- Histórico de lançamentos
- Sincronização dos dados com o Firebase
- Autenticação de usuários

## Tecnologias

- Kotlin 2.2.10
- Android SDK 37
- Jetpack Compose e Material 3
- AndroidX Core, Activity e Lifecycle
- Firebase Authentication (planejado)
- Cloud Firestore (planejado)
- Gradle com Kotlin DSL e Version Catalog

## Requisitos

- Android Studio atualizado
- JDK 11 ou compatível com a configuração do projeto
- Android SDK 37
- Um dispositivo físico ou emulador com Android 7.0 (API 24) ou superior
- Projeto criado no [Firebase Console](https://console.firebase.google.com/)

## Configuração

1. Clone o repositório e abra a pasta no Android Studio:

	```bash
	git clone https://github.com/eueliseeu/app-vintra.git
	cd app-vintra
	```

2. Crie ou selecione um projeto no Firebase Console.

3. Adicione um aplicativo Android com o identificador:

	```text
	com.vintra.app
	```

4. Baixe o arquivo `google-services.json` e coloque-o em `app/google-services.json`.

5. No Firebase, habilite os serviços necessários, como Authentication e Cloud Firestore.

6. Sincronize o projeto no Android Studio e aguarde o Gradle concluir a configuração.

> O arquivo `google-services.json` contém configurações específicas do seu projeto Firebase e não deve ser versionado quando houver dados sensíveis ou regras internas associadas.

## Executando o projeto

Pelo Android Studio, selecione o módulo `app`, escolha um emulador ou dispositivo conectado e pressione **Run**.

Também é possível executar pelo terminal:

```bash
./gradlew assembleDebug
```

O APK de debug será gerado em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Testes

Execute os testes unitários com:

```bash
./gradlew test
```

Execute os testes instrumentados em um dispositivo ou emulador com:

```bash
./gradlew connectedAndroidTest
```

## Estrutura principal

```text
app/
├── src/main/java/com/vintra/app/       # Código Kotlin e telas Compose
├── src/main/res/                       # Recursos Android
├── src/test/                           # Testes unitários
├── src/androidTest/                    # Testes instrumentados
└── build.gradle.kts                    # Configuração do módulo Android
```

## Identidade do aplicativo

- Nome: Vintra
- Application ID: `com.vintra.app`
- Versão atual: `1.0` (versionCode `1`)

## Licença

Ainda não definida.
