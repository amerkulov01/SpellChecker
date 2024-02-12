package spellchecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

// this file will create our dictionary to reference for correct spelling, implemented as a hashmap
public class dictionary{
    String name;
    HashMap<String,word> dict;
    
    public dictionary(){
        this.name = "emptyDict";
        this.dict = new HashMap<String,word>();

    }
    public dictionary(String path){
        loadFromFile(path);
    }
    // method to return a word object with given string key
    public word get(String key){
        return this.dict.get(key);
    }
    // method to add into dictionary
    public void add(String key){
        this.dict.put(key, new word(key));
    }
    // method to populate the dictionary with values from a given filePath
    public void loadFromFile(String path){
        if(this.dict == null){
            this.dict = new HashMap<String,word>();
        }
        Scanner s;
        try {
            s = new Scanner(new File(path));
            while (s.hasNextLine()){
                String cur = s.nextLine();
                this.dict.put(cur, new word(cur));
            }
            s.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void printKeys(){
        System.out.println(this.dict.keySet());
    }
    
    
}