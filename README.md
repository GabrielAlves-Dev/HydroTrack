# HydroTrack 💧

HydroTrack é um aplicativo Android intuitivo e completo, desenvolvido para ajudar os usuários a monitorar e manter sua meta diária de consumo de água. O projeto foi criado para a disciplina de Desenvolvimento para Dispositivos Móveis da UFC, no semestre 2025.1.

## ✨ Funcionalidades

* **Acompanhamento de Consumo:** Registre facilmente sua ingestão de água ao longo do dia.
* **Metas Diárias Personalizáveis:** Defina sua meta de hidratação e acompanhe seu progresso com indicadores visuais.
* **Histórico Detalhado:** Visualize todo o seu histórico de consumo, incluindo adições e remoções, para identificar padrões e manter-se no caminho certo.
* **Lembretes Inteligentes:** Receba notificações periódicas para não esquecer de beber água, com base na sua meta e consumo atual.
* **Sugestões de Hidratação com Base no Clima:** O aplicativo se integra a uma API de clima para oferecer sugestões de hidratação adaptadas às condições climáticas da sua localização (temperatura e umidade). Esta funcionalidade pode ser ativada/desativada nas configurações.
* **Unidades de Medida Flexíveis:** Escolha entre diferentes unidades de medida (ml, litros, copos, garrafas) para registrar sua água.
* **Gerenciamento de Perfil:** Edite suas informações de usuário como nome, e-mail e telefone.
* **Autenticação Segura:** Faça login ou registre-se usando e-mail e senha, ou de forma conveniente com sua conta Google.
* **Tema Claro/Escuro:** Personalize a interface do aplicativo para se adequar às suas preferências visuais.
* **Exclusão de Conta:** Capacidade de excluir a conta e todos os dados associados.

## 🛠️ Tecnologias Utilizadas

O HydroTrack foi construído utilizando as seguintes tecnologias:

* **Kotlin:** Linguagem de programação principal.
* **Jetpack Compose:** Toolkit moderno para construção de UI nativa Android.
* **Room Persistence Library:** Para banco de dados local (registros de consumo de água).
* **DataStore Preferences:** Para armazenar preferências do usuário de forma assíncrona.
* **Firebase Authentication:** Para gerenciamento de usuários (login, registro, login com Google).
* **Firebase Realtime Database:** Para sincronização de dados do usuário (meta diária, consumo).
* **Retrofit & GSON:** Para consumo da API OpenWeatherMap para dados climáticos.
* **WorkManager:** Para agendamento de lembretes de hidratação em segundo plano.
* **Navigation Compose:** Para gerenciamento da navegação entre telas.
* **Material 3:** Sistema de design do Google.

## ⚙️ Como Configurar e Rodar o Projeto

1.  **Clone o Repositório:**
    ```bash
    git clone [https://github.com/gabrielalves-dev/hydrotrack.git](https://github.com/gabrielalves-dev/hydrotrack.git)
    cd hydrotrack
    ```

2.  **Configuração do Firebase:**
    * Crie um projeto no Firebase Console.
    * Adicione um aplicativo Android ao seu projeto Firebase.
    * Baixe o arquivo `google-services.json` e coloque-o no diretório `app/` do seu projeto.

3.  **API Key do OpenWeatherMap:**
    * Uma chave de API para o OpenWeatherMap está incorporada no `WeatherRepository.kt`. Para uso em produção, considere métodos mais seguros para gerenciar chaves de API.

4.  **Permissões:**
    * Certifique-se de que as permissões de `INTERNET`, `ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`, `POST_NOTIFICATIONS` e `RECEIVE_BOOT_COMPLETED` estão declaradas no `AndroidManifest.xml`.

5.  **Abrir no Android Studio:**
    * Abra o projeto no Android Studio e espere o Gradle sincronizar as dependências.

6.  **Executar o Aplicativo:**
    * Conecte um dispositivo Android ou use um emulador.
    * Clique no botão "Run" (seta verde) no Android Studio.

## 👥 Equipe

Este projeto foi desenvolvido por:

* **Gabriel Alves** (eu)
* **Robson José**

Para a disciplina de Desenvolvimento para Dispositivos Móveis da Universidade Federal do Ceará (UFC), semestre 2025.1, sob a orientação do **Professor Francisco Victor**.

## 📄 Licença

Este projeto está licenciado sob a licença MIT. Consulte o arquivo `LICENSE` para mais detalhes.
