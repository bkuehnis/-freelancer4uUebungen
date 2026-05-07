# 🧪 Übung 13 - MCP, Skills, Hooks und Feature-Automation

**Themen:** Model Context Protocol (MCP), Skills, Hooks, GitHub MCP Server, Skill-Design, agentische Feature-Umsetzung

Diese Übung besteht aus vier Teilen:
1. **Grundlagen verstehen:** MCP, Skills und Hooks anhand offizieller Dokumentation nachlesen.
2. **GitHub MCP nutzen:** GitHub MCP Server einrichten und ein erstes Issue erstellen.
3. **Skills bauen und testen:** Einen einfachen Skill (`/grill-me`).
4. **Feature-Workflow automatisieren:** Einen Skill bauen, der aus einer Feature-Beschreibung ein GitHub-Issue erstellt und die Umsetzung bis Commit/Push inklusive Issue-Verknüpfung vorbereitet.

---

## 🛠️ Teil 1: MCP, Skills und Hooks verstehen

Lies die Konzepte **MCP**, **Skills** und **Hooks** auf folgender Webseite:

- https://www.exploreclaudecode.com/

### 1.1 Aufgabenstellung

Dokumentiere in deiner `uebung13.md` kurz:

- Was ist MCP und welchen Zweck hat es?
- Wofür werden Skills verwendet?
- Wofür werden Hooks verwendet?
- Überlege dir pro Konzept mindestens **ein praktisches Beispiel** in welchem du diese für dein Projekt einsetzen könntest.

---

## 🔌 Teil 2: GitHub MCP installieren und erstes Issue erstellen

Installiere und konfiguriere den GitHub MCP Server (Remote):

- https://github.com/github/github-mcp-server#remote-github-mcp-server

### 2.1 Aufgabenstellung

1. Richte den GitHub MCP Server in deinem Entwicklungssetup ein.
2. Verifiziere, dass du auf ein Test-Repository zugreifen kannst.
3. Erstelle über MCP ein neues Issue im Repository, z. B.:
	- Titel: `Setup: Validate GitHub MCP Integration`
	- Inhalt: kurze Beschreibung, dass der MCP-Zugriff erfolgreich getestet wurde.

---

## ✨ Teil 3: Skills erstellen und testen

### 3.1 Einfachen Skill `/grill-me` erstellen

Nutze diesen Skill als Vorlage:

- https://github.com/mattpocock/skills/tree/main/skills/productivity/grill-me

Erstelle einen eigenen Skill `/grill-me`, der bei unklaren oder zu vagen Anforderungen aktiv kritische Rückfragen stellt.


### 3.2 Aufgabenstellung

1. Implementiere beide Skills in deinem Projekt.
2. Teste `/grill-me` mit einer absichtlich unklaren Aufgabenbeschreibung.

---

## 🚀 Teil 4: Skill für Feature-Umsetzung mit GitHub-Issue-Workflow

Erstelle einen weiteren Skill, der deine **nächste Feature-Idee** für die Applikation verarbeitet.

### Ziel des Skills

Der Skill soll:

1. Eine Feature-Beschreibung entgegennehmen.
2. Zuerst **verpflichtend** den Skill `/grill-me` aufrufen, um Unklarheiten zu identifizieren und zu klären.
3. Nach der Klärung automatisch ein GitHub-Issue für das Feature erstellen.
4. Eine Umsetzungsstrategie mit konkreten Schritten erzeugen.
5. Bei Implementierung am Ende:
	- den Code committen,
	- pushen,
	- den Commit mit dem Issue verlinken (z. B. `Closes #123`),
	- das Issue schliessen.

### 4.1 Aufgabenstellung

1. Definiere den Skill so, dass `/grill-me` immer zuerst zur Anforderungspräzisierung eingesetzt wird.
2. Führe den vollständigen Ablauf einmal mit einem realistischen Feature durch.
3. Verifiziere, dass:
	- ein Issue erstellt wurde,
	- die Umsetzung committed und gepusht wurde,
	- das Issue korrekt referenziert und geschlossen wurde.