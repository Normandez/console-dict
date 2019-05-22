package com.company;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;

public class Main {

    static TreeMap<String, String> dict = new TreeMap<String, String>();
    static String dictFileName = "";
    static Scanner scan = new Scanner(System.in, "Cp866");
    static final String inputTag = "# ";

    enum SearchType {
        SEARCH_TYPE_WORD,
        SEARCH_TYPE_DESCRIPTION
    }
    enum PrintType {
        PRINT_TYPE_WORD,
        PRINT_TYPE_DESCRIPTION
    }

    public static void main( String[] args ) {
        System.out.print("Enter dictionary file full name: ");
        dictFileName = scan.nextLine();

        // Dictionary loading
        boolean dictIsLoaded = false;
        while(!dictIsLoaded) {
            try {
                loadDictionary(dictFileName);
                dictIsLoaded = true;
            } catch (IOException e) {
                System.out.print("Dictionary loading failed: " + e.getMessage() + "\nEnter correct dictionary file full name: ");
                dictFileName = scan.nextLine();
            }
        }
        System.out.println("Dictionary loaded successfully: '" + dictFileName + "'\n");

        // Main event loop
        String menuStr = "MAIN MENU:\n\t0 - Exit\n\t1 - Print all dictionary\n\t2 - Search by word\n\t3 - Search by description\n\t4 - Add new value\n";
        byte menuChoice;
        while(true){
            System.out.println(menuStr);
            System.out.print(inputTag);
            menuChoice = scan.nextByte();
            scan.nextLine();
            switch(menuChoice){
                case 0: return;
                case 1:
                    printAll();
                    break;
                case 2:
                    System.out.print("Enter word or part of word: ");
                    search(scan.nextLine(), SearchType.SEARCH_TYPE_WORD);
                    break;
                case 3:
                    System.out.print("Enter description or part of description: ");
                    search(scan.nextLine(), SearchType.SEARCH_TYPE_DESCRIPTION);
                    break;
                case 4:
                    addNewWord();
                    break;
                default:
                    System.out.println("Invalid choice! Try again!");
            }
        }
    }

    // Print all dictionary
    public static void printAll(){
        System.out.println("===========================================================<WHOLE DICTIONARY CONTENTS>===========================================================");

        int count = 1;
        for(Map.Entry<String, String> entry : dict.entrySet() ){
            System.out.println(count + ". " + entry.getKey() + " - " + entry.getValue());
            count++;
        }
        System.out.println("======================================================================================================================\n");
    }

    // Search dictionary value by word or description
    public static void search(String searchPattern, SearchType type){
        TreeMap<String, String> searchRes = new TreeMap<String, String>();

        int count = 1;
        for( Map.Entry<String, String> entry : dict.entrySet() ){
            if(type == SearchType.SEARCH_TYPE_WORD) {
                if(entry.getKey().toLowerCase().contains(searchPattern)){
                    searchRes.put( count + ". " + entry.getKey(), entry.getValue());
                    count++;
                }
            }
            else if(type == SearchType.SEARCH_TYPE_DESCRIPTION) {
                if(entry.getValue().toLowerCase().contains(searchPattern)){
                    searchRes.put( count + ". " + entry.getKey(), entry.getValue());
                    count++;
                }
            }
            else {
                System.out.println("INVALID SEARCH TYPE\n");
                return;
            }
        }

        if(!searchRes.isEmpty()){
            System.out.println("===========================================================<BY WORD SEARCH>===========================================================");
            for( Map.Entry<String, String> entry : searchRes.entrySet() ){
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
            System.out.println("======================================================================================================================\n");
        }
        else{
            System.out.println("NOTHING WAS FOUND\n");
            return;
        }

        String subMenuStr = "SEARCH MENU:\n\t0 - Nothing\n\t1 - Print word\n\t" +
                            "2 - Print description\n\t3 - Edit word\n\t4 - Edit description\n\t5 - Remove dictionary value\n";
        System.out.println(subMenuStr);
        int subMenuChoice;
        boolean isSelected = false;
        while(!isSelected){
            System.out.print(inputTag);
            subMenuChoice = scan.nextByte();
            scan.nextLine();
            switch(subMenuChoice){
                case 0: return;
                case 1:
                    isSelected = true;
                    System.out.print("Enter search number: ");
                    print(searchRes, scan.nextByte(), PrintType.PRINT_TYPE_WORD);
                    break;
                case 2:
                    isSelected = true;
                    System.out.print("Enter search number: ");
                    print(searchRes, scan.nextByte(), PrintType.PRINT_TYPE_DESCRIPTION);
                    break;
                case 3:
                    isSelected = true;
                    System.out.print("Enter search number: ");
                    editWord(searchRes, scan.nextByte());
                    break;
                case 4:
                    isSelected = true;
                    System.out.print("Enter search number: ");
                    editDescription(searchRes, scan.nextByte());
                    break;
                case 5:
                    isSelected = true;
                    System.out.print("Enter search number: ");
                    removeDictValue(searchRes, scan.nextByte());
                    break;
                default:
                    System.out.println("Invalid choice! Try again!");
            }
        }

    }

    // Print dictionary value's word or description
    public static void print(TreeMap<String, String> searchRes, int index, PrintType type){
        while(true){
            for( Map.Entry<String, String> entry : searchRes.entrySet() ){
                if(entry.getKey().contains(Integer.toString(index))){
                    if(type == PrintType.PRINT_TYPE_WORD) {
                        System.out.println("WORD: '" + entry.getKey().replaceFirst("\\d{1,}\\.", "").trim() + "'\n");
                    }
                    else if(type == PrintType.PRINT_TYPE_DESCRIPTION) {
                        System.out.println("DESCRIPTION: '" + entry.getValue().trim() + "'\n");
                    }
                    else {
                        System.out.println("INVALID PRINT TYPE\n");
                    }
                    return;
                }
            }
            System.out.print("Invalid search number. Enter correct number: ");
            index = scan.nextByte();
            scan.nextLine();
        }
    }

    // Edit dictionary value's word
    public static void editWord(TreeMap<String, String> searchRes, int index){
        scan.nextLine();
        while(true){
            for( Map.Entry<String, String> entry : searchRes.entrySet() ){
                if(entry.getKey().contains(Integer.toString(index))){
                    String oldKey = entry.getKey().replaceFirst("\\d{1,}\\.", "").trim();
                    String description = entry.getValue();
                    System.out.print("OLD WORD: '" + oldKey + "'\nEnter new value: ");
                    String newKey = scan.nextLine();

                    dict.remove(oldKey);
                    dict.put(newKey.trim(), description);
                    boolean dictIsSaved = false;
                    while(!dictIsSaved){
                        try{
                            saveDictionary(dictFileName);
                            dictIsSaved = true;
                        }
                        catch(IOException e){
                            System.out.print("Dictionary saving failed: " + e.getMessage() + "\nEnter correct dictionary file full name: ");
                            dictFileName = scan.nextLine();
                        }
                    }
                    System.out.println("WORD was changed successfully. Dictionary saved.\n");

                    return;
                }
            }
            System.out.print("Invalid search number. Enter correct number: ");
            index = scan.nextByte();
            scan.nextLine();
        }
    }

    // Edit dictionary value's description
    public static void editDescription(TreeMap<String, String> searchRes, int index){
        scan.nextLine();
        while(true){
            for( Map.Entry<String, String> entry : searchRes.entrySet() ){
                if(entry.getKey().contains(Integer.toString(index))){
                    String key = entry.getKey().replaceFirst("\\d{1,}\\.", "").trim();
                    System.out.print("OLD DESCRIPTION: '" + entry.getValue().trim() + "'\nEnter new description: ");
                    String newDescription = scan.nextLine();

                    dict.remove(key);
                    dict.put(key, newDescription.trim());
                    boolean dictIsSaved = false;
                    while(!dictIsSaved){
                        try{
                            saveDictionary(dictFileName);
                            dictIsSaved = true;
                        }
                        catch(IOException e){
                            System.out.print("Dictionary saving failed: " + e.getMessage() + "\nEnter correct dictionary file full name: ");
                            dictFileName = scan.nextLine();
                        }
                    }
                    System.out.println("DESCRIPTION was changed successfully. Dictionary saved.\n");

                    return;
                }
            }
            System.out.print("Invalid search number. Enter correct number: ");
            index = scan.nextByte();
            scan.nextLine();
        }
    }

    // Add new value into dictionary
    public static void addNewWord(){
        System.out.print("ENTER WORD: ");
        String newWord = scan.nextLine();
        System.out.print("ENTER DESCRIPTION: ");
        String newDescription = scan.nextLine();

        dict.put(newWord.trim(), newDescription.trim());
        boolean dictIsSaved = false;
        while(!dictIsSaved){
            try{
                saveDictionary(dictFileName);
                dictIsSaved = true;
            }
            catch(IOException e){
                System.out.print("Dictionary saving failed: " + e.getMessage() + "\nEnter correct dictionary file full name: ");
                dictFileName = scan.nextLine();
            }
        }
        System.out.println("New VALUE was added successfully. Dictionary saved.\n");
    }

    // Remove value from dictionary
    public static void removeDictValue(TreeMap<String, String> searchRes, int index){
        while(true){
            for( Map.Entry<String, String> entry : searchRes.entrySet() ){
                if(entry.getKey().contains(Integer.toString(index))){
                    String key = entry.getKey().replaceFirst("\\d{1,}\\.", "").trim();

                    dict.remove(key);
                    boolean dictIsSaved = false;
                    while(!dictIsSaved){
                        try{
                            saveDictionary(dictFileName);
                            dictIsSaved = true;
                        }
                        catch(IOException e){
                            System.out.print("Dictionary saving failed: " + e.getMessage() + "\nEnter correct dictionary file full name: ");
                            dictFileName = scan.nextLine();
                        }
                    }
                    System.out.println("VALUE was removed successfully. Dictionary saved.\n");

                    return;
                }
            }
            System.out.print("Invalid search number. Enter correct number: ");
            index = scan.nextByte();
            scan.nextLine();
        }
    }

    // Load dictionary from file
    public static void loadDictionary(String fileName) throws IOException{
        FileReader fr = new FileReader(fileName);
        Scanner fr_scan = new Scanner(fr);

        String dict_line = "";
        String dict_key = "";
        String dict_value = "";
        while(fr_scan.hasNextLine()){
            dict_line = fr_scan.nextLine();
            if(dict_line.length() != 0){
                String[] dict_parts = dict_line.split("—");
                dict_key = dict_parts[0].trim().replaceFirst("\\d{1,}\\.", "").trim();
                dict_value = dict_parts[1].trim();
                dict.put(dict_key, dict_value);
            }
        }

        fr.close();
    }

    // Save dictionary into file
    public static void saveDictionary(String fileName) throws IOException{
        FileWriter fw = new FileWriter(fileName, false);

        int count = 1;
        for(Map.Entry<String, String> entry : dict.entrySet()){
            fw.write("\n" + count + ". " + entry.getKey() + " — " + entry.getValue() + "\n");
            count++;
        }

        fw.close();
    }

}
