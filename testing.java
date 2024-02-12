package spellchecker;

public class testing {

    public testing(){

    }

    static dictionary ourDict = new dictionary("words_alpha.txt");

    public static void main(String[]args){
        String testSub = "platc";
        word testSubWord = new word(testSub);
        testSubWord.correctSpellingSubstitution(testSubWord, ourDict);

        System.out.println("Sub errors:");
        for (word w : testSubWord.recommendations) {
            System.out.println(w.theWord);
        }


        String testOmit = "eyez";
        word testOmitWord = new word(testOmit);
        testOmitWord.correctSpellingOmission(testOmitWord, ourDict);

        System.out.println("Omission errors:");
        for (word w : testOmitWord.recommendations) {
            System.out.println(w.theWord);
        }



        String testRev = "olleh";
        word testRevWord = new word(testRev);
        testRevWord.correctSpellingReversal(testRevWord, ourDict); 

        System.out.println("Reversal errors:");
        for (word w : testRevWord.recommendations) {
            System.out.println(w.theWord);
        }

    }

}