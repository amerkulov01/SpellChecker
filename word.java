package spellchecker;

import java.util.ArrayList;
import java.util.Scanner;

public class word extends dictionary {
    boolean isCorrect;
    boolean isWord;
    boolean isDoubleWord;
    boolean isMiscapitalized;
    boolean isMisspelled;
    int charCount;
    String theWord;
    ArrayList<word> recommendations;

    
    //PLEASE DO NOT DELETE. KEEP THESE
    final static public word NEWLINE = new word("\n");
    final static public word WHITESPAECE = new word(" ");
    
    public word(String inputString){
        this.theWord = inputString;
        this.charCount = inputString.length();
        this.isCorrect = true;
        this.isWord = true;
        this.isDoubleWord = false;
        this.isMiscapitalized = false;
        this.isMisspelled = false;
        this.recommendations = new ArrayList<>();
    }

    public void isWord(word inputWord){
        int i;
        for(i=0;i<inputWord.charCount; i++){
            if (inputWord.theWord.charAt(i) > 64 && inputWord.theWord.charAt(i) < 123){
                //if there are any letters in inputWord then we consider it a word
                //this.isWord = true;
                return;
            }
        }
        this.isWord = false;
    }

    // method to compare input word against the dictionary to check for correct spelling
    public boolean isMisspelled(word inputWord, dictionary ourDict){

        String strLower = inputWord.theWord.toLowerCase();
        strLower = strLower.replaceAll("\\p{Punct}", "");
        word testWord = new word(strLower);
        if(!ourDict.dict.containsKey(testWord.theWord)) {
            inputWord.isCorrect = false;
            inputWord.isMisspelled = true;
            return true;
        }
        else{
            return false;
        }
    }

    // method will generate recommendations for a flagged word by substituting letters at each position of the input word
    // and comparing against the dictionary
    public void correctSpellingSubstitution(word inputWord, dictionary ourDict){
        String strInpWord = inputWord.theWord;
        char[] charArray = strInpWord.toCharArray();

        String alphabet= "abcdefghijklmnopqrstuvwxyz";
        int i, j;
        for(i=0;i<charArray.length; i++){
            for(j=0;j<alphabet.length();j++){
                charArray[i] = alphabet.charAt(j);
                inputWord.theWord = String.valueOf(charArray);
                if(ourDict.dict.containsKey(inputWord.theWord)){
                    System.out.println(inputWord.theWord);
                    if(!recommendations.contains(inputWord)){
                        recommendations.add(inputWord); // recommendations are added to the ArrayList attribute of the input word if they exist in dictionary
                    }
                }
            }
            charArray = strInpWord.toCharArray();
        }
    }

    // method gives user recommendations for a flagged word by deleting letters at each position and checking against the dictionary
    public void correctSpellingOmission(word inputWord, dictionary ourDict) {
        String strInpWord = inputWord.theWord;
        char[] tempWord = strInpWord.toCharArray();
        char[] newWord;
        String strTempWord;

        int i;
        for(i=0;i<inputWord.charCount;i++){
            newWord = tempWord.clone();
            tempWord[i] = ' ';
            strTempWord = String.valueOf(tempWord);
            strTempWord = strTempWord.replaceAll(" ","");

            if(ourDict.dict.containsKey(strTempWord)){
                inputWord.theWord = strTempWord;
                System.out.println(inputWord.theWord);
                if(!recommendations.contains(inputWord)){ //recommendations are added to ArrayList if they exist in the dictionary
                    recommendations.add(inputWord);
                }
                tempWord = newWord.clone();
            }
            else{
                tempWord = newWord.clone();
            }

        }
    }

    // method gives user recommendation for flagged word by reversing it and comparing against the dictionary
    public void correctSpellingReversal(word inputWord, dictionary ourDict) {
    	word reversedWord = new word(new StringBuilder(inputWord.theWord).reverse().toString());
    	if(ourDict.dict.containsKey(reversedWord.theWord) && !recommendations.contains(reversedWord))
            recommendations.add(reversedWord);

    }

    public static String addChar(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }

    // gives user recomendations for a flagged word by inserting letters at each index in the word and comparing against the dictionary
    public void correctSpellingInsertion(word inputWord, dictionary ourDict){
        String strInpWord = inputWord.theWord;
        String newWord = strInpWord, alphabet= "abcdefghijklmnopqrstuvwxyz";
        char[] alphArray = alphabet.toCharArray();

        int i, j;
        for(i = 0;i<strInpWord.length()+1;i++){
            for(j=0;j<alphabet.length();j++){
                newWord = addChar(strInpWord, alphArray[j], i);
                if(ourDict.dict.containsKey(newWord)){
                    System.out.println(newWord);
                    inputWord.theWord = newWord;
                    if(!recommendations.contains(inputWord)){ // if a recommendation exists in the dictionary it is added to ArrayList
                        recommendations.add(inputWord);
                    }
                }
                else{
                    newWord = strInpWord;
                }
            }
        }

    }

    public void correctSpellingManually(word inputWord, dictionary ourDict){
        if(!ourDict.dict.containsKey(inputWord.theWord)){
            try (Scanner userImp = new Scanner(System.in)) {
				String newWord = userImp.next();
				if(ourDict.dict.containsKey(newWord)){
				    inputWord.theWord = newWord;
				    if(!recommendations.contains(inputWord)){
				        recommendations.add(inputWord);
				    }
				}
			}
        }
    }


    //method to detect if a word is double word by comparing it to the next word index
    public void isDoubleWord(word inputWord, ArrayList<word> arrayList){
        int ind = arrayList.indexOf(inputWord);

        if((ind-2)>=0 &&(arrayList.get(ind - 2).theWord).equalsIgnoreCase(arrayList.get(ind).theWord)){
            inputWord.isDoubleWord = true;
            inputWord.isCorrect = false;
        }
    }

    // method to check if a word is miscapitalized by checking for punctuation before it
    public void isMiscapitalized(word inputWord, ArrayList<word> arrayList){
    	//First word check
        int ind = arrayList.indexOf(inputWord);
        if(ind == 0 && Character.isLowerCase(arrayList.get(ind).theWord.charAt(0))){
            inputWord.isMiscapitalized = true;
            inputWord.isCorrect = false;
        }
        
        //Check for missed capitalization (lowercase leading char when should be uppercase)
        if (ind - 2 >= 0) {
        	String word1 = arrayList.get(ind-2).theWord; //get word1 as string
        	String word2 = arrayList.get(ind).theWord; //get word1 as string
        	//Check if previous word ended in ?!. and next one is starting with lowercase
            if (!word1.isEmpty() && !word2.isEmpty() && Character.isLowerCase(word2.charAt(0)) && "?!.".contains(String.valueOf(word1.charAt(word1.length() - 1)))) { 
                inputWord.isMiscapitalized = true;
                inputWord.isCorrect = false;
            }
        }
        
        //Check for wrong capitalization (capital leading char when should be lowercase)
        if((ind - 2) >= 0){
        	String word1 = arrayList.get(ind-2).theWord; //get word1 as string
        	String word2 = arrayList.get(ind).theWord; //get word1 as string
            if(!word1.isEmpty() && !word2.isEmpty() && Character.isUpperCase(word2.charAt(0)) && !"?!.".contains(String.valueOf(word1.charAt(word1.length() - 1)))){
                inputWord.isMiscapitalized = true;
                inputWord.isCorrect = false;
            }
        }
        
        //If there are any uppercase letters in the middle of the word
        if (!inputWord.theWord.isEmpty() && inputWord.theWord.substring(1).chars().anyMatch(Character::isUpperCase)) {
        	inputWord.isMiscapitalized = true;
            inputWord.isCorrect = false;
        }
    }
}
