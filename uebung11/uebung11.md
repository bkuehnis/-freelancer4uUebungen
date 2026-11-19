# 3SE2 – Übung 11

**Themen:** Spring AI, KI-generierte Jobtitel, Tool Calling, MCP und ein Chatfenster mit SvelteKit

In dieser Übung erweiterst du Freelancer4u um KI-generierte Jobtitel und einen Chat. Zuerst bindest du ein Modell an. Danach ergänzt du zwei Java-Methoden, mit denen der Chat Jobs suchen und erstellen kann. Am Schluss rufst du dieselben Methoden über MCP aus VS Code auf.

Folgendes wird erwartet.

- Beim Erstellen eines Jobs über das bestehende Formular wird der Titel anhand der Beschreibung angepasst.
- Im Frontend können angemeldete Benutzerinnen und Benutzer das Chatfenster öffnen und damit Jobs suchen und erstellen.
- In VS Code kannst du dieselben Tools über MCP aufrufen. Ein so erstellter Job erscheint anschliessend auch in Freelancer4u.

## Ausgangslage

Starte mit deinen bisherigen Lösungen. Wenn du mit der Musterlösung arbeitest, verwende die folgenden Stände **vor Übung 11** und lege in jedem Repository einen neuen Übungsbranch an.

| Repository | Ausgangsstand |
| --- | --- |
| [Backend](https://github.com/zhaw-iwi/freelancer4u-hs26) | `1562c30` (nach Übung 10) |
| [Frontend](https://github.com/zhaw-iwi/freelancer4u-hs26-frontend) | `43f20ad` (nach Übung 9) |

Verwende Java 25 und Node.js ab 22.17. Übernimm wie in den früheren Übungen deine lokalen Einstellungen für MongoDB und Auth0. Im Backend müssen `MONGODB_URI`, `MONGODB_DATABASE` und `AUTH0_DOMAIN` für den Java-Prozess gesetzt sein. Verwende für die Übung eine lokale oder separate Demo-Datenbank. Kopiere im Frontend bei Bedarf `.env.example` nach `.env`, ergänze deine Auth0-Einstellungen und setze `API_BASE_URL=http://127.0.0.1:8080`. Führe im Frontend `npm install` aus. Starte beide Ausgangsprojekte wie in den früheren Übungen und prüfe, ob du dich anmelden und die Jobs-Seite öffnen kannst.

## Teil 1 – Spring AI einrichten

**a)** Ergänze in der Backend-`pom.xml` unter `<properties>` die Version `<spring-ai.version>2.0.2</spring-ai.version>`. Füge direkt vor dem bestehenden `<dependencies>`-Block den folgenden Block ein.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>${spring-ai.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Füge die folgenden beiden Abhängigkeiten in den bestehenden `<dependencies>`-Block ein. Die BOM legt die Versionen fest.

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
</dependency>
```


**b)** Ergänze in `src/main/resources/application.properties` die folgenden Einstellungen. Behalte die vorhandenen Einträge für MongoDB und Auth0 bei.

```properties
spring.ai.model.chat=openai
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.model=${OPENAI_MODEL:gpt-4o-mini}
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.protocol=STREAMABLE
spring.ai.model.embedding=none
spring.ai.model.image=none
spring.ai.model.audio.speech=none
spring.ai.model.audio.transcription=none
spring.ai.model.moderation=none
```


**c)** Setze `OPENAI_API_KEY` zusammen mit den vorhandenen Variablen in der Umgebung des Backend-Prozesses. Einen anderen Modellnamen kannst du über `OPENAI_MODEL` setzen. Speichere den Schlüssel nur in deiner lokalen Umgebung.

Setze für diese Übung zusätzlich `SERVER_ADDRESS=127.0.0.1` in derselben Umgebung. So bleibt der MCP-Endpunkt lokal erreichbar. Unter macOS und Linux startest du das Backend wie folgt, nachdem du die übrigen Variablen gesetzt hast.

```sh
chmod +x mvnw
SERVER_ADDRESS=127.0.0.1 ./mvnw spring-boot:run
```

Unter Windows setzt du in PowerShell `$env:SERVER_ADDRESS = "127.0.0.1"` und startest mit `.\mvnw.cmd spring-boot:run`. Starte das Backend nach Änderungen an der Konfiguration erneut.

## Teil 2 – Jobtitel generieren

**a)** Lege [JobTitleGenerator.java](JobTitleGenerator.java) im Java-Paket `ch.zhaw.freelancer4u.controller` ab. Die Klasse verwendet den von Spring AI bereitgestellten `ChatClient.Builder`.

**b)** Ergänze in `JobController` das Feld `private final JobTitleGenerator jobTitleGenerator;`. Rufe nach dem Erstellen des `Job`-Objekts und vor dem Speichern Folgendes auf.

```java
jDAO.setTitle(jobTitleGenerator.generate(jDAO.getTitle(), jDAO.getDescription()));
```

**c)** Melde dich mit einem Admin-Konto an und erstelle einen Job über das bestehende Formular. Prüfe den gespeicherten Titel. Bei einem Modellfehler behält der Generator den eingegebenen Titel bei.

## Teil 3 – Zwei Tools für den Chat bereitstellen

**a)** Lege [FreelancerTools.java](FreelancerTools.java) und [FreelancerToolConfig.java](FreelancerToolConfig.java) im Paket `ch.zhaw.freelancer4u.tools` ab. `@Tool` beschreibt die Methoden für Spring AI. Der `ToolCallbackProvider` registriert beide Methoden.

**b)** Ergänze im `CompanyRepository` die Methode `Optional<Company> findFirstByNameIgnoreCase(String name);` und importiere `java.util.Optional`. Ergänze im `JobRepository` die folgende Methode und importiere `ch.zhaw.freelancer4u.model.JobState`.

```java
List<Job> findByJobStateAndTitleContainingIgnoreCaseOrJobStateAndDescriptionContainingIgnoreCase(
        JobState titleState, String title, JobState descriptionState, String description, Pageable pageable);
```

**c)** Lege [ChatController.java](ChatController.java) im Paket `ch.zhaw.freelancer4u.controller` ab. Der Controller stellt `POST /api/chat` bereit und übergibt die Tools an den `ChatClient`. Das Modell kann einen Tool-Aufruf anfordern. Spring AI führt die Java-Methode aus und gibt das Ergebnis an das Modell zurück.

## Teil 4 – Chatfenster im Frontend einbauen

| Vorlage | Ziel im Frontend-Repository |
| --- | --- |
| [JobChat.svelte](JobChat.svelte) | `src/lib/JobChat.svelte` |
| [+server.js](+server.js) | `src/routes/api/chat/+server.js` |

Importiere in `src/routes/+layout.svelte` die Komponente mit `import JobChat from "#lib/JobChat.svelte";`. Ergänze unter dem Seiteninhalt:

```svelte
{#if isAuthenticated}
  <JobChat />
{/if}
```

Starte das Backend und das Frontend (`npm run dev`). Melde dich an und öffne den Chat unten rechts. Frage zum Beispiel «Welche offenen Java-Jobs gibt es?». Der SvelteKit-Endpunkt reicht das vorhandene Zugriffstoken an das Spring-Backend weiter. Das Modell fordert bei Bedarf einen Aufruf von `search_jobs` an.

## Teil 5 – Dieselben Tools in VS Code verwenden

Der WebMVC-Starter veröffentlicht die registrierten Tool-Callbacks am MCP-Endpunkt. Ergänze im Backend in `SecurityConfig` die folgende Filterkette und importiere `org.springframework.core.annotation.Order`. Der Endpunkt dient der lokalen Übung und kann Jobs schreiben. Starte das Backend dafür mit `SERVER_ADDRESS=127.0.0.1` wie in Teil 1.

```java
@Bean
@Order(1)
public SecurityFilterChain localMcpFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/mcp")
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .build();
}
```

Kopiere [mcp.json](mcp.json) nach `.vscode/mcp.json` im Backend-Workspace. Starte den Server-Eintrag in VS Code und prüfe im Copilot-Chat, ob `search_jobs` und `create_job` verfügbar sind. Rufe `create_job` mit einem Demo-Auftrag auf, zum Beispiel «Erstelle den Job Java-API implementieren bei Demo AG für 500 mit Jobtyp IMPLEMENT und der Beschreibung REST-Endpunkt entwickeln». Falls die Firma fehlt, legt das Tool sie an. Suche den Job danach im Chatfenster von Freelancer4u und prüfe nach dem Neuladen auch die Jobs-Seite.

Die beiden Tools definieren, **was** Freelancer4u ausführen kann. MCP definiert, **wie** VS Code diese Tools entdeckt und aufruft.

## Abgabe

Pushe deine Änderungen in deine privaten GitHub-Repositories für Frontend und Backend. Gib beide Repositories für die Dozierenden frei und reiche die beiden URLs auf Moodle ein. Zugangsdaten bleiben in deiner lokalen Umgebung.

[Spring AI OpenAI Chat](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html) · [Spring AI MCP über WebMVC](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html#_webmvc)
