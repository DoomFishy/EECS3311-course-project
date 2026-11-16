# StudyBuddy

StudyBuddy is a Java desktop application for course planning, assessment tracking, and AI-assisted learning. It supports a Swing GUI and a terminal CLI, and can use either a local Ollama-based model or a cloud OpenRouter model.

## Prerequisites

- Java 21 or newer
- Maven 3.9+
- Optional: Ollama installed locally for offline AI mode
- Optional: OpenRouter API key for cloud AI mode

Check your Java and Maven installation:

```bash
java -version
mvn -v
```

## Dependency Installation

From the project root, install and compile dependencies:

```bash
mvn clean install
```

If you only want to compile without installing to the local Maven repository:

```bash
mvn clean compile
```

The project uses Maven and includes the PDFBox dependency for PDF reading in [pom.xml](pom.xml).

## Local Configuration

The application reads AI settings from environment variables at runtime.

### Option 1: Local mode (Ollama)

If Ollama is running locally:

```bash
export STUDYBUDDY_AI_MODE=local
export OLLAMA_HOST=http://localhost:11434
export OLLAMA_MODEL=llama3.2
```

### Option 2: Cloud mode (OpenRouter)

Set a valid API key and model:

```bash
export STUDYBUDDY_AI_MODE=cloud
export OPENROUTER_API_KEY=your_api_key_here
export OPENROUTER_MODEL=openrouter/free
```

A replace placeholder key in launch.json


## Running the Application

### GUI mode

From the project root:

```bash
mvn exec:java -Dexec.args="--gui"
```

This launches the Swing-based course dashboard.

### CLI mode

Start the interactive CLI:

```bash
mvn exec:java -Dexec.args="--cli"
```

Show the help text:

```bash
mvn exec:java -Dexec.args="--help"
```

### Build a runnable jar

```bash
mvn clean package
```

Then run the app via Maven or the jar as needed. Note that the project is set up to run from `App`, so the recommended workflow is to use `mvn exec:java` for development.

## CLI Commands

Once the interactive CLI starts:

```text
> help
> list
> course "EECS 3311"
> add-course "Math 101"
> add-assessment "Midterm" 03/15 25 EXAM
> ask "What should I study first?"
> deadlines
> week
> quit
```

You can also pass commands directly:

```bash
mvn exec:java -Dexec.args="--cli help"
mvn exec:java -Dexec.args="--cli list"
mvn exec:java -Dexec.args="--cli week"
mvn exec:java -Dexec.args="--cli deadlines"
```

## Example Usage

### Create a course

```bash
mvn exec:java -Dexec.args="--cli course \"EECS 3311\""
```

### Add an assessment

```bash
mvn exec:java -Dexec.args="--cli add-assessment \"Midterm\" \"03/15\" \"25\" \"EXAM\""
```

### Ask a question

```bash
mvn exec:java -Dexec.args="--cli ask \"What are the most important topics for my next exam?\""
```

### View this week's tasks

```bash
mvn exec:java -Dexec.args="--cli week"
```

### View deadlines

```bash
mvn exec:java -Dexec.args="--cli deadlines"
```

## GUI Workflow

In the GUI:

- Add a course from the left panel
- Upload materials such as PDFs, text files, or slides
- Select which materials should be used as context for AI questions
- Add assessment deadlines and weights
- Ask AI questions about the selected course materials
- Generate notes, reports, quizzes, and study plans
- Toggle local/cloud mode from the AI mode button in the top-right of the dashboard

## Project Structure

Key project files:

- [src/main/java/App.java](src/main/java/App.java) — app entry point
- [src/main/java/MainMenuGUI.java](src/main/java/MainMenuGUI.java) — GUI
- [src/main/java/ai/controller/AppFacade.java](src/main/java/ai/controller/AppFacade.java) — central application logic
- [src/main/java/cli/CommandRegistry.java](src/main/java/cli/CommandRegistry.java) — CLI command routing
- [src/main/java/course/Course.java](src/main/java/course/Course.java) — course model
- [pom.xml](pom.xml) — Maven configuration

## Troubleshooting

- If the app fails to start, confirm Java 21 is installed and `mvn -v` works.
- If AI mode is local but Ollama is not running, switch to cloud mode or start Ollama.
- If an API call fails, verify the `OPENROUTER_API_KEY` environment variable is set correctly.
- If a PDF cannot be read, ensure the file exists and is a valid PDF.

## Notes

This project is intended for local course planning and small AI-assisted workflows. The AI integrations are designed to be configuration-based and can run in either local or cloud mode depending on the environment.
