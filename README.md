# StudyBuddy

DISCLAIMER! This Project was developed with Co-pilot which developed most of the project with my editing coming in later.

StudyBuddy is a Java desktop application for course planning, assessment tracking, and AI-assisted learning. It supports a Swing GUI and a terminal CLI, and can use either a local Ollama-based model or a cloud OpenRouter model.

## Prerequisites

- Java 21 or newer
- Maven 3.9+
- Optional: Ollama installed locally for offline AI mode
- Optional: OpenRouter API key for cloud AI mode

Check your Java and Maven installation:

java -version
mvn -v

## Dependency Installation & Building

From the project root, build the project and package the standalone Fat JAR (which automatically includes dependencies like PDFBox):

mvn clean package

This will create a runnable studybuddy-1.0-SNAPSHOT.jar inside the target/ folder.

## Local Configuration

The application reads AI settings from environment variables or the -LLMAPI command-line flag. 
You can also create a .env file in the project root (next to pom.xml) to store these automatically:

OPENROUTER_API_KEY=your_api_key_here
OPENROUTER_MODEL=openrouter/free

### Option 1: Local mode (Ollama)
If Ollama is running locally:

export STUDYBUDDY_AI_MODE=local
export OLLAMA_HOST=http://localhost:11434
export OLLAMA_MODEL=llama3.2

### Option 2: Cloud mode (OpenRouter)
Set a valid API key and model:

export STUDYBUDDY_AI_MODE=cloud
export OPENROUTER_API_KEY=your_api_key_here
export OPENROUTER_MODEL=openrouter/free

## Running the Application

### GUI Mode
To launch the Swing-based course dashboard:

java -jar target/studybuddy-1.0-SNAPSHOT.jar --gui

### Interactive CLI Mode
To start the interactive terminal shell:

java -jar target/studybuddy-1.0-SNAPSHOT.jar --cli

## Standalone Agentic Testing (Headless Mode)

You can run specific AI agent functions directly from your terminal without launching the interactive GUI or CLI shell. This is useful for automated testing, grading, and running scripts.

### Usage

java -jar target/studybuddy-1.0-SNAPSHOT.jar [COMMAND] -input <filepath> [-LLMAPI <api_key>]

### Supported Commands
- -ask    : Reads the text file and uses it as a prompt/question for the AI.
- -notes  : Generates study notes for the course.
- -plan   : Generates a study plan.
- -quiz   : Generates a practice quiz.
- -report : Generates a course report.

### Working Example
1. Create a file named task.txt:
Explain the difference between TCP and UDP.

2. Run the agent (passing the API key directly via flag, or relying on your .env file):
java -jar target/studybuddy-1.0-SNAPSHOT.jar -ask -input task.txt -LLMAPI sk-or-v1-123456789

3. Expected Output:
Running -ask...

--- AI RESPONSE ---
TCP is connection-oriented and reliable, ensuring packets arrive in order. UDP is connectionless and faster...
-------------------

(Note: For -notes, -plan, -quiz, and -report, the contents of the -input file are ignored, but the file must still exist to satisfy the CLI structure.)

## Interactive CLI Commands

Once the interactive CLI starts (> ), the application uses unified commands grouped by flags. Type help at any time to see this menu.

### Menu Commands (Before entering a course)
> help
> course -list                   List all courses
> course -create <name>          Create a new course
> course -delete <name>          Delete an existing course
> course -enter <name>           Enter a course to access its tools
> quit

### Course Commands (After entering a course)
> help
> week                           Show this week's tasks
> deadlines                      Show upcoming deadlines

Assessment Tools:
> assessment -add <name> <date> <weight> <type>    Add an assessment
> assessment -complete <name> <score>              Grade an assessment
> list-assessments                                 List all assessments

Material Tools:
> material -list                                   List uploaded materials
> material -upload <filepath>                      Upload a new file
> material -set <id> ...                           Select materials for AI context
> material -set -d <id> ...                        Deselect materials

Grade Tools:
> grade -set <percentage>                          Set a target grade goal
> grade -view                                      View current average vs goal

AI Agent Tools:
> agent -ask <question>                            Ask the AI a question
> agent -report                                    Generate a course report
> agent -plan                                      Generate a study plan
> agent -notes                                     Generate study notes
> agent -quiz                                      Generate a practice quiz

General:
> back                                             Return to the main menu
> quit

## GUI Workflow

In the GUI:
- Add a course from the left panel
- Upload materials such as PDFs, text files, or slides
- Select which materials should be used as context for AI questions
- Add assessment deadlines and weights
- Ask AI questions about the selected course materials
- Generate notes, reports, quizzes, and study plans
- Toggle local/cloud mode from the AI mode button in the top-right of the dashboard