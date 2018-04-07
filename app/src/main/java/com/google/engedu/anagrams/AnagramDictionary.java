/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;



public class AnagramDictionary {


    // ==============================================
    // member variables
    // Note: can instantiate objects in constructor instead
    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;

    private int wordLength = DEFAULT_WORD_LENGTH;
    // holds dictionary
    private ArrayList<String> wordList = new ArrayList<String>();
    // used to verify if word is valid
    private HashSet<String> wordSet = new HashSet<String>();
    // group anagrams together
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<String, ArrayList<String>>();
    // maps word length to an array list of all words of that length
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<Integer, ArrayList<String>>();

    private Random random = new Random();

    // ==============================================

    // constructor
    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            // push back word to wordList
            wordList.add(word);
            wordSet.add(word);

            String mKey = sortLetters(word);

            // START SIZE TO WORD
            if(sizeToWords.containsKey(word.length())) {
                // add current word to arraylist at that key
                sizeToWords.get(word.length()).add(word);
            } else {
                // else, create a new ArrayList, add the word to it and store in
                // the HashMap with the corresponding key.
                ArrayList<String> allWords = new ArrayList<>();
                allWords.add(word);
                sizeToWords.put(word.length(), allWords);
            }
            // END SIZE TO WORD

            // START LETTERS TO WORD
            // check if letters to word already contains an entry from that key
            if (lettersToWord.containsKey(mKey)) {
                // add current word to arraylist at that key
                lettersToWord.get(mKey).add(word);
//                ArrayList<String> allWords
//                // get original array list, then add to it
//                ArrayList<String> allWords = lettersToWord.get(mKey);
//                allWords.add(word); // add the word
//                lettersToWord.put(mKey, allWords);
            } else {
                // else, create a new ArrayList, add the word to it and store in
                // the HashMap with the corresponding key.
                ArrayList<String> allWords = new ArrayList<>();
                allWords.add(word);
                lettersToWord.put(mKey, allWords);
            }
            // END LETTERS TO WORD
        }
    }

    public boolean isGoodWord(String word, String base) {

        // whether a word is a valid dictionary word: does wordset contain the word.
        if (wordSet.contains(word)) {
            //check if word does not contain the base word as a substring
            if (!word.contains(base)) {
                return true;
            }
        }
        return false;
    }


    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();

        String sortedTargetWord = sortLetters(targetWord);
        // check if same length and if anagrams
        for (int i = 0; i < wordList.size(); i++) {

            if (wordList.get(i).length() != targetWord.length()) {
                // skip
            } else {
                // same length so check if same anagrams
                if(sortLetters(wordList.get(i)).equals(sortedTargetWord)) {
                    result.add(wordList.get(i));
                }
            }
        }

        return result;
    }

    //  takes a String and returns another String with the same letters in alphabetical order
    public String sortLetters(String word) {


        char[] charactersOfWord = word.toCharArray();
        Arrays.sort(charactersOfWord);
        String sortedWord = new String(charactersOfWord);

        return sortedWord;
    }

    // takes a string and finds all anagrams that can be formed by adding one
    // letter to that word.
    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();

        for(char alphabet = 'a'; alphabet <= 'z';alphabet++) {
            if (lettersToWord.containsKey(sortLetters(word + alphabet))) {
                // get list of angrams
                ArrayList<String> listAnagrams = lettersToWord.get(sortLetters(word + alphabet));
                // add each anagram
                for (int i = 0; i < listAnagrams.size(); i++) {
                    if (isGoodWord(word, listAnagrams.get(i))) {
                        result.add(listAnagrams.get(i));
                    }
                }

            }
        }

        return result;
    }

    public String pickGoodStarterWord() {

        // Pick a random starting point in the wordList array and check each
        // word in the array until you find one that has at least MIN_NUM_ANAGRAMS anagrams.

        int randomNumber;
        int numAnagrams = 0;

        // restrict your search to the words of length wordLength
        ArrayList<String> listWordsMaxLength = sizeToWords.get(wordLength);
        int arraySize = listWordsMaxLength.size();

        // while less than, keep picking new random word
        while (numAnagrams < MIN_NUM_ANAGRAMS) {

            // get random number of size of array, then use it
            // to choose a (random) word from the fixed length array of words
            randomNumber = random.nextInt(arraySize);
            String randomWord = listWordsMaxLength.get(randomNumber);

            // get size of array with all anagrams of randomly selected word
            // call getAnagramsWithOneMoreLetter since word.length() 3 on average contain 1 anagram max
            // creating infinite loop
            numAnagrams = getAnagramsWithOneMoreLetter(randomWord).size();
            if (numAnagrams >= MIN_NUM_ANAGRAMS) {
                if(wordLength < MAX_WORD_LENGTH) {
                    wordLength++;
                }
                return randomWord;
            }
        }
        return "stop";
    }
}