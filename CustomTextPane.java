package spellchecker;

//imports
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This class provides a very nice modification/extention to the JTextPane class where it uses the ArrayList of words from the Ourdocument class
 * as the basis of the underlying document. The startIndices hashmaps the words from arraylist of words to the starting index in the document, and
 * updates everything accordingly for all modifications. There is also a mouselister which spawns a JMenu with possible suggestions and provides a way
 * to apply them.
 */
public class CustomTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;
	
	//text information
	int charCount;
	int wordCount;
	int lineCount;
	
	//List of words, and a map to their respective index inside the inner document
	private ArrayList<word> words;
    private HashMap<word, Integer> startIndices; //necessary since I have to keep track on indecies to find which word is hovered over
	
    /**
     * Constructor, take a refference to a list of words
     * @param words Basically, represents the whole document here
     */
    public CustomTextPane(ArrayList<word> words) {
        this.words = (words != null) ? words : new ArrayList<>();
        startIndices = new HashMap<>();
        
        //Set up custom listeners to extend functionality
        this.setupListeners();
        this.setupDocumentListener();
        
        //init values to 0
        charCount = 0;
        wordCount = 0;
        lineCount = 0;
    }

    /**
     * Rebuild the text, update hashmap indices, and updates colors of incorrect words.
     */
    public void updateText() {
    	if (words == null) return; //when document is empty, do nothing
    	
    	//Reset attributes
        StyledDocument doc = getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(), SimpleAttributeSet.EMPTY, true);

        try {
            // Clear the document and start rebuilding
            doc.remove(0, doc.getLength());

            //Go word by word for the list representing the document
            for (word aword : words) {
                //get the start index for the word
                int startIndex = doc.getLength();

                //add word to the document
                doc.insertString(startIndex, aword.theWord, null);

                //set correct hashmap index
                startIndices.put(aword, startIndex);

                //Check the preprocessed isCorrect flag to see if we should color the word red 
                if (!aword.isCorrect) {
                    SimpleAttributeSet att = new SimpleAttributeSet();
                    StyleConstants.setForeground(att, Color.red);
                    doc.setCharacterAttributes(startIndex, aword.theWord.length(), att, false);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        //Update the labels inside GUI by getting the counts locally
        GUI.updateCountsLabel(getCounts());
    }
    
    /**
     * set a new doc. resets the current doc
     * @param newWords
     */
    public void setDoc(ArrayList<word> newWords) {
    	// Reset the current doc and clear the existing word indices
        getStyledDocument().setCharacterAttributes(0, getStyledDocument().getLength(), SimpleAttributeSet.EMPTY, true);
        startIndices.clear();

        // Set the new words and update the word indices
        this.words = newWords;
        updateText(); //run update
    }

    /**
     * Mouse listened, checks when clicked word is red, then displays a pop up menu
     */
    private void setupListeners() {
    	addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            	Point p = e.getPoint();
                int offset = viewToModel2D(p);
                word clickedWord = getWordAt(offset);
                
                //if incorrect, display menu
                if (clickedWord != null && !clickedWord.isCorrect) {
                    displayPopUpMenu(clickedWord, e.getX(), e.getY());
                }
            }
        });
    }
    
    /**
     * This method takes a word, its x and y position on the 2d textpane
     * @param whichword The word to suggest corrections for
     * @param x coords
     * @param y coords
     */
    public void displayPopUpMenu(word whichword, int x, int y) {
    	//edge case check
    	if (whichword == null || whichword.theWord.isEmpty())
    		return;
    	
    	//init pupup menu
		JPopupMenu popupMenu = new JPopupMenu();
		
		//Firstly, check if word is a double word, then just suggest to delete it and return
		if (whichword.isDoubleWord) {
			JMenuItem menuItem = new JMenuItem("Delete duplicate word");
			popupMenu.add(menuItem);
	        menuItem.addActionListener(e -> deleteDoubleWord(whichword)); //add event handler 
			popupMenu.show(this, x, y);
			return;
		}
		
		//Secondly, check if the target word is mis capitalized, then just return the suggestion to capitalize it
		if (shouldBeCapitalized(whichword)) {
			String wordStr = whichword.theWord; //str representation
			String capitalized = wordStr.substring(0, 1).toUpperCase() + wordStr.substring(1); //first letter capitalized
			JMenuItem menuItem = new JMenuItem(capitalized); //make a new menu item
	        menuItem.addActionListener(e -> applySuggestion(whichword, capitalized)); //add event handler
			popupMenu.add(menuItem); //add to menu
			popupMenu.show(this, x, y); //show menu
			return;
		}
		
		//Thirdly, check if the target word is mis capitalized, then just return the suggestion to capitalize it
		if (shouldBeLowerCased(whichword)) {
			String wordStr = whichword.theWord; //str representation
			String lowecase = wordStr.substring(0, 1).toLowerCase() + wordStr.substring(1); //first letter capitalized
			JMenuItem menuItem = new JMenuItem(lowecase); //make a new menu item
	        menuItem.addActionListener(e -> applySuggestion(whichword, lowecase)); //add event handler
			popupMenu.add(menuItem); //add to menu
			popupMenu.show(this, x, y); //show menu
			return;
		}
		
		//Forthly, check if the target word has capital letters in other positions, and return
		if (whichword.theWord.chars().anyMatch(Character::isUpperCase)) {
			String fix = whichword.theWord.toLowerCase();
			JMenuItem menuItem = new JMenuItem(fix); //make a new menu item
	        menuItem.addActionListener(e -> applySuggestion(whichword, fix)); //add event handler
			popupMenu.add(menuItem); //add to menu
			popupMenu.show(this, x, y); //show menu
			return;
		}
		
		/*Lastly, generate suggestions based on
		 * 1) adding a random letter 
		 * 2) omitting a random letter 
		 * 3) transposition errors
		 */
		List<String> suggestions = genSuggestions(whichword, 5); // <-- change to a higher or lower number for max suggestions
		
		//exit if there are no suggestions
		if (suggestions == null  || suggestions.isEmpty())
			return;

		//add items to JMenu
		for (String sugma : suggestions) {
			JMenuItem menuItem = new JMenuItem(sugma);
	        menuItem.addActionListener(e -> applySuggestion(whichword, sugma));
			popupMenu.add(menuItem);
		}
		
		suggestions.clear(); //clear suggestions so the arraylist doesn't get populated over and over again with the same information when you click on the same word multiuple times in the program
		
        popupMenu.show(this, x, y); //display menu finally
	}
    
    //PRIVATE HELPER METHODS
    
    /**
     * Deleted the first occurance of a double word
     * @param whichword The word
     */
    private void deleteDoubleWord(word whichword) {
    	//edge cases stuffs
    	if (whichword == null || words == null || words.isEmpty()) 
            return;
    	
    	// Get the previous word
    	int prevDeleteIndex = words.indexOf(whichword) - 1;
    	
    	//Remove the word itself, then remove the WHITESPACE
    	words.remove(prevDeleteIndex);
    	words.remove(prevDeleteIndex);
    	
    	this.updateText(); //Make sure to update text
    }
    
    /**
     * Returns true if a word should be capitalized based on the last word and punctuation
     */
    private boolean shouldBeCapitalized(word whichword) {
    	//edge cases stuffs
    	if (whichword == null || words == null || words.isEmpty())
            return false;
    	
    	//edge case for "I"
    	if (whichword.theWord.equals("i"))
    		return true;
    	
    	//Check if this is the first word, then it should be capitalized
    	if (startIndices.containsKey(whichword) && startIndices.get(whichword) == 0)
    		return true;
    	
    	// Get the previous word
        word previousWord = words.get(words.indexOf(whichword) - 2);
        
        //check if previous word ends with one of "?!."
        String lastCharOfPrevWord = String.valueOf(previousWord.theWord.charAt(previousWord.theWord.length() - 1));
        return "?!. ".contains(lastCharOfPrevWord);
    }
    
    /**
     * Returns true if a word should be NOT be capitalized and it is, again based on the last word and punctuation
     */
    private boolean shouldBeLowerCased(word whichword) {
    	//edge cases stuffs
    	if (whichword == null || words == null || words.isEmpty())
            return false;
    	
    	//edge case for "I"
    	if (whichword.theWord.equalsIgnoreCase("i"))
    		return false;
    	
    	// Get the previous word
        word previousWord = words.get(words.indexOf(whichword) - 2);
        
        //check if previous does not end with one of "?!."
        char lastCharOfPrevWord = previousWord.theWord.charAt(previousWord.theWord.length() - 1);
      
        //is the last character of the previous word lowercase AND is the first character of the current word uppercase? 
        return Character.isLowerCase(lastCharOfPrevWord) && Character.isUpperCase(whichword.theWord.charAt(0)); 
    }
    
    /**
     * Sets the content of the word to be the accepted suggestions
     */
    private void applySuggestion(word originalWord, String acceptedSuggestion) {        
        originalWord.theWord = acceptedSuggestion;
        originalWord.isCorrect = true;
        
        updateText(); //again, update text
    }
    
    /**
     * This uses some algorithms to generate possible suggestions by brute forcing
     * adding a single letter
     * omitting a single letter
     * swapping two adjacent letters
     */
    private List<String> genSuggestions(word whichword, int maxSugCount) {
    	ArrayList<String> suggestions = new ArrayList<>();

    	//store the original word as string
    	String originalWord = whichword.theWord.toLowerCase();

        // 1) Adding a letter test
        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i <= originalWord.length(); i++) {
                String newWord = originalWord.substring(0, i) + c + originalWord.substring(i);
                if (Ourdocument.ourDict.dict.containsKey(newWord))
                    suggestions.add(newWord);
            }
        }

        // 2) Omitting a letter test
        for (int i = 0; i < originalWord.length(); i++) {
        	String newWord = originalWord.substring(0, i) + originalWord.substring(i + 1);
            if (Ourdocument.ourDict.dict.containsKey(newWord))
                suggestions.add(newWord);
        }
        
        // 3) transposition letters test
        for (int i = 0; i < originalWord.length() - 2; i++) {
            char[] charArray = originalWord.toCharArray();
            //swap adjacent letters to check transposition errors
            char temp = charArray[i];
            charArray[i] = charArray[i + 1];
            charArray[i + 1] = temp;

            String sug = new String(charArray);
            if (Ourdocument.ourDict.dict.containsKey(sug))
                suggestions.add(sug);
        }
        
        //remove duplicate items from reccomendations in case there are any, and limit to maxSugCount
        ArrayList<String> unique = new ArrayList<>(new HashSet<>(suggestions));
        maxSugCount = (unique.size() < maxSugCount) ? unique.size() : maxSugCount; //use whichever one is smaller
        return unique.subList(0, maxSugCount); //just return the first x number of suggestions
    }
    
    /**
     * need this to sync our hashmap, also sends a signal to the GUI to update the counts
     */
    private void setupDocumentListener() {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateWordStartIndices(e.getOffset(), e.getLength());
                GUI.updateCountsLabel(getCounts());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateWordStartIndices(e.getOffset(), -e.getLength());
                GUI.updateCountsLabel(getCounts());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }
    
    /**
     * Use the offset given by the document listened to smartly adjust the indecies 
     */
    private void updateWordStartIndices(int offset, int changeLength) {
        for (word aword : startIndices.keySet()) {
            int startIndex = startIndices.get(aword);
            if (startIndex >= offset) {
                startIndices.put(aword, startIndex + changeLength);
            }
        }
    }

    /**
     * Given offset, return the corresponding word
     * required because there are non unique string keys representing different words
     */
    private word getWordAt(int offset) {
        for (word aword : startIndices.keySet()) {
            int startIndex = startIndices.get(aword);
            int endIndex = startIndex + aword.theWord.length();

            if (startIndex <= offset && endIndex >= offset) {
                return aword;
            }
        }
        return null;
    }
    
    /**
     * @return the number of characters in this document
     */
    private int charCount() {
    	return getText().replaceAll("\\s", "").length();
    }
    
    /**
     * @return the number of words in this document
     */
    private int wordCount() {
    	String[] wordsArray = getText().split("\\s+");
        return wordsArray.length;
    }
    
    /**
     * @return the number of lines in this document
     */
    private int lineCount() {
    	String[] linesArray = getText().split("\n");
        return linesArray.length;
    }
    
    /**
     * Calls above three count methods and sends an array with them to the caller
     * @return Necessarily an ordered int array with 3 elements corresponding to char count, word count, line count
     */
    public int[] getCounts() {
    	int[] counts = new int[3];
    	counts[0] = this.charCount();
    	counts[1] = this.wordCount();
    	counts[2] = this.lineCount();
    	return counts;
    }
}
