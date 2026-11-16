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
java -cp target\classes App --gui
```

This launches the Swing-based course dashboard.

### CLI mode

Start the interactive CLI:

```bash
java -cp target\classes App --cli
```

Show the help text:

```bash
java -cp target\classes App --help
```

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

## GUI Workflow

In the GUI:

- Add a course from the left panel
- Upload materials such as PDFs, text files, or slides
- Select which materials should be used as context for AI questions
- Add assessment deadlines and weights
- Ask AI questions about the selected course materials
- Generate notes, reports, quizzes, and study plans
- Toggle local/cloud mode from the AI mode button in the top-right of the dashboard

