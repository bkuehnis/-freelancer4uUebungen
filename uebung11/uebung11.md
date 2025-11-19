# 🧪 Übung 11 – KI-gestützte Titelgenerierung mit Spring AI


In dieser Übung wird das Framework [Spring AI](https://spring.io/projects/spring-ai) in das Projekt *Freelancer4u* integriert. Die Aufgabe besteht aus drei Teilen:

1. **Konfiguration von Spring AI**  
   → Einrichtung und Anbindung eines Chat-Providers (z. B. OpenAI)

2. **LLM-gestützte Job-Erstellung**  
   → Beim Erstellen eines Jobs wird der Titel anhand der Jobbeschreibung durch ein LLM automatisch verbessert.

3. **Chatfenster mit Tool-Unterstützung**  
   → Ein interaktives Chatfenster wird implementiert  
   → Es ermöglicht:
   - Abfragen von Daten aus der Datenbank  
   - Erstellen von Entitäten mittels Spring AI Tools (Company, Jobs)

---

### 📎 Abgabe

**Folgendes Artefakt ist über Moodle einzureichen:**

- Die **URL** eines privaten GitHub-Repositories  
- Das Repository muss für die Dozierenden freigegeben sein  
- Es enthält die implementierten **Java-Klassen**


## 🛠️ Teil 1. Vorbereitung: Installation und Konfiguration

### 1.1 `pom.xml` anpassen

Füge folgende **Dependencies** innerhalb des `<dependencies>`-Blocks hinzu: 

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```


### 1.2 OpenAI API Key einrichten

Füge in `src/main/resources/application.properties` folgende Zeilen hinzu:

```properties
spring.ai.openai.api-key=${OPEN_AI_KEY}
#For debugging (optional):
logging.level.org.springframework.ai.chat.client.advisor=DEBUG
```

In der Datei `.vscode/launch.json` fügt bei `env` das Propertie `"OPEN_AI_KEY": "..."` hinzu, wobei ihr den Wert `...` mit dem Open AI Key von Moodle ersetzten müsst.

Sollte wie folgt aussehen:

```json
"env": {
                "MONGODB_URI": "...",
                "AUTH0_DOMAIN": "...",
                "OPEN_AI_KEY": "..."
            }
```

## ✨ Teil 2: Titelgenerierung mit Spring AI

Im Teil 2 wird anhand von [Chat Models](https://docs.spring.io/spring-ai/reference/api/chatmodel.html) den Titel, des Jobs anhand der Beschreibung bei der Erstellung angepasst.

### 2.1 AI-Model in `JobController` einbinden
Öffne `src/main/java/ch/zhaw/freelancer4u/controller/JobController.java` und füge das AI-Model per Dependency Injection ein:

```java
@Autowired
OpenAiChatModel chatModel;
```

### 2.2 Titel automatisch generieren lassen

Ersetze bei `@PostMapping("/job")` folgenden Code:

```java
Job job = new Job(cDTO.getTitle(), cDTO.getDescription(), cDTO.getJobType(), cDTO.getEarnings(), cDTO.getCompanyId());
```

mit diesem:
```java
var generatedTitle = chatModel.call(new Prompt(
    "Der Titel lautet bisher: '" + cDTO.getTitle() + "'. Falls nötig, verbessere den Titel anhand der folgenden Beschreibung: " + cDTO.getDescription() + ". Gib nur den neuen Titel zurück."
));
var title = generatedTitle.getResult().getOutput().getText();
Job job = new Job(title, cDTO.getDescription(), cDTO.getJobType(), cDTO.getEarnings(), cDTO.getCompanyId());
```

### 2.3 Unit Test anpassen

Öffne `src/test/java/ch/zhaw/freelancer4u/controller/JobControllerTest.java`.

Füge folgendes Mock-Objekt hinzu:

```java
@MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
private OpenAiChatModel chatModel;
```
Ergänze ausserdem folgende Setup-Methode:

```java
@BeforeEach
void setupMockAiResponse() {
    when(chatModel.call(any(Prompt.class))
        .getResult()
        .getOutput()
        .getText()).thenReturn(TEST_TITLE);
}
```

### 2.4 Testen
Erstelle einen neuen Job. Überprüffe, ob der Titel nach der Erstellung angepasst wurde.

## ✨ Teil 3: Chatbot
Ziel dieses Teil ist es einen Chatbot zu erstellen, mit welchem die einen geigneten Job finden kann. Zusätzlich soll es möglich sein über den Chatbot einen Job und die ensprechende Company zu erstellen.

Für die Umsetzung wird die [Chat Client API](https://docs.spring.io/spring-ai/reference/api/chatclient.html) verwendet. 
### 3.1 CompanyService updaten

`src/main/java/ch/zhaw/freelancer4u/service/CompanyService.java`

```java
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public void createCompany(String name, String email) {
        Company company = new Company(name, email);
        companyRepository.save(company);
    }
```

### 3.2 Neue Dateien erstellen

Kopiere die folgenden Dateien in die entsprechenden Ordner:

| Datei | Pfad | Beschreibung |
|-------|------|--------------|
| `OpenAiConfig.java` | `src/main/java/ch/zhaw/freelancer4u/config/` | Diese Klasse enthält die Konfiguration für das OpenAI-Modell. Hier wird z. B. das `OpenAiChatModel` als Bean definiert, sodass es in anderen Klassen per `@Autowired` genutzt werden kann. |
| `FreelancerTools.java` | `src/main/java/ch/zhaw/freelancer4u/tools/` | Eine Hilfsklasse (Utility), die Tools (Methoden) für den Chat bereitstellt. Der Chatbot hat Zugriff auf diese Methoden und kann sie während des Chattens aufrufen. |
| `ChatController.java` | `src/main/java/ch/zhaw/freelancer4u/controller/` | Ein REST-Controller für Chat-Funktionalitäten. Er stellt Endpunkte bereit, über die im Frontend eine Konversation mit dem OpenAI-Modell geführt werden kann. |
| `CreateRandomJobsController.java` | `src/main/java/ch/zhaw/freelancer4u/controller/` | Ein REST-Controller, der zufällige Jobs generieren und speichern kann – nützlich für Tests, Demos oder initiale Datenbefüllung. |
| `CreateRandomJobsControllerTest.java` | `src/test/java/ch/zhaw/freelancer4u/controller/` | Ein JUnit-Test für den `CreateRandomJobsController`. Stellt sicher, dass das Generieren und Speichern von zufälligen Jobs korrekt funktioniert. |
| `+page.svelte` | `frontend/src/routes/chat/` | Die Svelte-Komponente für die Chat-Seite. Hier wird die Benutzeroberfläche für den KI-Chat dargestellt, z. B. ein Eingabefeld für Nachrichten und ein Anzeigebereich für Antworten. |
| `+page.server.js` | `frontend/src/routes/chat/` | Die Svelte-Komponente für die serverseitige Chat-Seite. Hier wird das Spring Boot Backend aufgerufen. |



### 3.3 Menüeintrag im Frontend hinzufügen

Öffne `frontend/src/routes/+layout.svelte` und füge einen Menüpunkt zum Chat ein, z. B.:

```html
<a class="nav-link" href="/chat">💬 Chat</a>
```

### 3.4 Erweiterung von `FreelancerTools.java`
Bisher kann der Chatbot nur Informationen über Jobs und Unternehmen aus der Datenbank lesen. Ziel ist es, die Klasse `FreelancerTools.java` so anzupassen, dass auch Jobs erstellt werden können. Dabei ist zu beachten, dass ein Unternehmen ebenfalls erstellt werden muss, falls es noch nicht existiert.

Bei der Erstellung eines Jobs soll es möglich sein, den Namen des Unternehmens anzugeben. Es werden also zwei Methoden benötigt: eine zum Erstellen eines Unternehmens und eine zum Erstellen eines Jobs.

Für die Erstellung eines Jobs wird die `companyId` benötigt, die anhand des Unternehmensnamens in der Datenbank ermittelt werden kann.
