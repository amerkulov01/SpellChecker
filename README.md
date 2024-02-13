# Spell Checker Application
This Java project implements a spell checker with a user-friendly graphical interface, allowing users to write, check, and correct spelling in their text. It uses a custom dictionary for spell checking and provides functionalities such as text editing, file saving, and spell correction suggestions.

## Features
- Spell Checking: Identifies misspelled words based on a predefined dictionary.
- GUI: A graphical user interface for easy interaction with the application.
- Text Editing: Supports editing text directly within the application.
- File Operations: Open, save, and save text files with spell-checked content.
## Files and Directories
- GUI.java: Main class for the application's GUI.
- CustomTextPane.java: Custom JTextPane for text editing and display.
- Ourdocument.java: Handles document events and operations.
- dictionary.java: Manages the spell checker dictionary.
- word.java: Defines the structure for words used in the spell checking process.
- testing.java: Contains testing routines for the application (if applicable).
- upload.png, saveas.png, download3.png, dowlnoad3.png: Icons used in the GUI.
## Getting Started
### Prerequisites
- Java Development Kit (JDK) installed on your system.
### Compilation
- Compile the Java files using the following command:
```
javac *.java
```
### Running the Application
- Run the application with the following command:
```
java GUI
```
## How to Use
- Open a File: Click on the upload icon to open and load a text file for spell checking.
- Save a File: Use the save icon to save the current text to a file.
- Spell Check: The application automatically highlights misspelled words. Right-click on a highlighted word to see suggested corrections.
