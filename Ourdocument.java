package spellchecker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;

public class Ourdocument {

    private String documentAsString;
    private int wordCount;
    private int issuesCount;
    private int misspelledWordCount;
    private int doubleWordCount;
    private int miscapitalizationCount;
    private int wordDeletionCount;

    private ArrayList<word> wordList;
    static public dictionary ourDict = new dictionary("words_alpha.txt");;

    public Ourdocument(String pathToTextFile) {
        loadFromFile(pathToTextFile);
        scanForIssues();
    }

    public Ourdocument() {
        // Default constructor for an empty document

    }
    // method to load from an input file and populate Ourdocument with word objects of each word in the input file
    public void loadFromFile(String pathToTextFile) {
        StringBuilder content = new StringBuilder();
        wordList = new ArrayList<word>();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToTextFile))) {
            String words[];
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
                words = line.split(" ");

                for (int i=0; i<words.length; i++) {
                    word nextword = new word(words[i]);
                    wordList.add(nextword);// add each word to the ArrayList
                    wordCount++;
                    if (i < words.length - 1)
                    	wordList.add(word.WHITESPAECE);
                }
                wordList.add(word.NEWLINE);
            }
            wordList.remove(wordList.size() - 1); //remove last redundant newline
            this.documentAsString = content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public word searchWord(String wordKey) {
        Pattern pattern = Pattern.compile("\\b" + wordKey + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(documentAsString);

        while (matcher.find()) {
            String foundWord = matcher.group();
            // Create a word object or use an existing one based on your implementation
            word word = new word(foundWord);
            return word;
        }

        return null;
    }

    // this method will scan each word in the wordList and flag all issues on each word
    public void scanForIssues() {

        for (word w : wordList) {
            if (!w.isWord) {
                wordDeletionCount++;
                setIssuesCount(getIssuesCount() + 1);
            } else {
                if (w.isMisspelled(w, ourDict)) {
                    if (!w.isCorrect) {
                        misspelledWordCount++;
                        setIssuesCount(getIssuesCount() + 1);
                    }
                }

                w.isDoubleWord(w, wordList);
                if (w.isDoubleWord) {
                    doubleWordCount++;
                    setIssuesCount(getIssuesCount() + 1);
                }

                w.isMiscapitalized(w, wordList);
                if (w.isMiscapitalized) {
                    miscapitalizationCount++;
                    setIssuesCount(getIssuesCount() + 1);
                }
            }
        }
    }

    // getter functions for class attributes
    public int getDoubleWordCount() {
    	
    	return doubleWordCount;
    }
    
    public int getMiscapitalizationCount() {
    	
    	return miscapitalizationCount;
    }
    
    public int getNumMisspelledWords() {
    	
    	return misspelledWordCount;
    }
    
    public int getNumWords() {
    	
    	return wordCount;
    }
    
    public int getNumDeletions() {
    	
    	return wordDeletionCount;
    }

    /**
     * Return document as a generic list of words. newline represented as word.NEWLINE constant
     */
    public ArrayList<word> getDocument() {
        //Init scanner and words list

        return wordList;
    }

    public String getDocAsString() {
        return documentAsString;
    }

	public int getIssuesCount() {
		return issuesCount;
	}

	public void setIssuesCount(int issuesCount) {
		this.issuesCount = issuesCount;
	}
}
