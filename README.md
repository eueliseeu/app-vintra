# Vintra

Aplicativo Android social voltado a desenvolvedores e profissionais de tecnologia. Projeto acadêmico da disciplina **A3 — Usabilidade, Desenvolvimento Web, Mobile e Jogos**.

## Sobre o projeto

Vintra é uma rede social mobile em que o usuário pode publicar posts, comentar, curtir e consultar vagas de trabalho. Contas verificadas podem criar anúncios de emprego com tags (CLT, PJ, estágio, entre outras). O app inclui autenticação, perfil, feed global, módulo de jobs e uma tela de FAQ.

Por se tratar de um projeto acadêmico com escopo de entrega definido e prazo limitado, **não foram implementados testes automatizados** (unitários ou de UI). A prioridade foi a construção completa das telas, fluxos de uso e integração com o backend.

## Arquitetura

Foi adotado o padrão **MVVM (Model-View-ViewModel)** com separação em camadas:

- **UI (View):** Jetpack Compose — screens e componentes
- **ViewModel:** estado da tela (`StateFlow`) e orquestração de casos de uso
- **Domain:** models e use cases
- **Data:** repositórios Firebase (Auth e Firestore)

Injeção de dependências com **Hilt**. Operações assíncronas com **Coroutines** e **Flow**.

## Tecnologias

| Item | Uso |
|------|-----|
| Kotlin | Linguagem principal |
| Jetpack Compose | Interface |
| Material 3 | Design system |
| MVVM + Hilt | Arquitetura e DI |
| Firebase Authentication | Login Google e GitHub |
| Cloud Firestore | Persistência em tempo real |
| Navigation Compose | Navegação entre telas |

## Funcionalidades principais

- Login com Google e GitHub
- Cadastro de perfil (nome, username, foto)
- Feed Global com abas For you e My Post
- Criar, editar e apagar posts (somente o autor)
- Curtir posts e comentar
- Editar comentário (autor) e apagar (autor ou dono do post)
- Módulo Jobs: listar, buscar, criar e editar (somente usuário verificado)
- Detalhe de post e de vaga
- FAQ na aba Ranking do menu inferior
- Selo de verificação e toasts de feedback

## Estrutura do Código

```text
## Estrutura do Código (MVVM + Clean Architecture)

```text
com.vintra.app/
│
├── ui/                     # Camada de Apresentação (MVVM)
│   ├── components/         # Componentes de UI reutilizáveis (Buttons, Cards, Dialogs)
│   ├── theme/              # Configurações de tema (Color.kt, Type.kt, Theme.kt)
│   └── feature/            # Features da aplicação (Telas e ViewModels)
│       ├── auth/           # Login / Registro
│       │   ├── LoginScreen.kt
│       │   ├── LoginViewModel.kt
│       │   └── LoginUiState.kt
│       └── home/           # Tela Principal
│           ├── HomeScreen.kt
│           ├── HomeViewModel.kt
│           └── HomeUiState.kt
│
├── domain/                 # Regras de Negócio
│   ├── model/              # Objetos de Domínio imutáveis
│   ├── repository/         # Interfaces dos Repositórios (Contratos)
│   └── usecase/            # Casos de Uso (Regras de negócio isoladas)
│
├── data/                   # Camada de Dados
│   ├── remote/             # Integração com Firestore / APIs
│   │   ├── dto/            # Data Transfer Objects (Modelos do Firestore)
│   │   └── source/         # DataSources (FirestoreService.kt)
│   ├── mapper/             # Mapeadores (DTO <-> Domain Model)
│   └── repository/         # Implementação das interfaces do Domain
│
└── core/                   # Infraestrutura e utilitários
    ├── di/                 # Injeção de Dependências com Hilt
    │   ├── AppModule.kt
    │   ├── RepositoryModule.kt
    │   └── FirebaseModule.kt
    ├── session/            # Gerenciador de Sessão / Auth State
    └── util/               # Extensions, Result wrappers e Helpers
```
## Como executar

1. Abrir o projeto no Android Studio
2. Configurar o arquivo `google-services.json` do Firebase
3. Cadastrar o SHA-1 do keystore no console Firebase
4. Habilitar Authentication (Google e GitHub) e Firestore
5. Sincronizar o Gradle e executar no emulador ou dispositivo físico

---

## Observações acadêmicas

- Disciplina: Usabilidade, Desenvolvimento Web, Mobile e Jogos (A3)
- Desenvolvimento do Grupo
- Foco em usabilidade mobile, fluxos de navegação e integração Firebase
- Testes automatizados não fazem parte do escopo desta entrega

---

## Autores

Ana Júlia Brum  124222016
Bruno Resende Ribeiro - 124220140
Gustavo Morais - 1242022304
Fabricio Rocha - 125111399975
Maycon Soares - 125111404445
Eliseu Silva - 124220479
Gustavo Silva Lourenço - 125111410319
Lucca Lommez - 1261948642
