package pdfproject;

import pdfproject.models.WordInfo;
import pdfproject.core.StringDiff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Random random = new Random();
    private static List<String> commonWords;

    public static void main(String[] args) {
        commonWords = Arrays.asList(
                "apple", "orange", "banana", "grape", "kiwi",
                "dog", "cat", "bird", "fish", "rabbit",
                "car", "bike", "bus", "train", "plane",
                "house", "tree", "flower", "sun", "moon",
                "water", "fire", "earth", "air", "rock",
                "book", "pen", "pencil", "desk", "chair",
                "shoe", "hat", "shirt", "pants", "jacket",
                "phone", "computer", "keyboard", "mouse", "screen",
                "music", "movie", "game", "pizza", "burger",
                "coffee", "tea", "milk", "juice", "cake",
                "school", "teacher", "student", "class", "homework",
                "garden", "park", "beach", "river", "mountain",
                "watch", "clock", "glasses", "ring", "bracelet",
                "train", "carriage", "ticket", "platform", "station",
                "doctor", "hospital", "patient", "medicine", "nurse",
                "police", "crime", "thief", "law", "judge",
                "rain", "snow", "wind", "cloud", "storm"
        );

//        StringBuilder stringBuilder1 = new StringBuilder();
//        StringBuilder stringBuilder2 = new StringBuilder();
//
//        generateSentences(stringBuilder1,stringBuilder2,10);
//        String sentence1 = stringBuilder1.toString();
//        String sentence2 = stringBuilder2.toString();

        String sentence1 = "station snow car teacher shirt teacher keyboard police pants";
        String sentence2 = "station snow watch car shirt keyboard police pants";

        System.out.println(sentence1 +"\n"+sentence2);

        String[] words1 = sentence1.split(" ");
        String[] words2 = sentence2.split(" ");
        List<WordInfo> list1 = new ArrayList<>();
        List<WordInfo> list2 = new ArrayList<>();
        for (String word: words1){
            list1.add(new WordInfo(word));
        }
        for (String word: words2){
            list2.add(new WordInfo(word));
        }
//        List<WordInfo> list = StringDiff.getList(list1, list2);
//        for (WordInfo wordInfo: list){
//            System.out.println(wordInfo);
//        }
    }

    private static void generateSentences(StringBuilder stringBuilder1, StringBuilder stringBuilder2, int length) {
        for (int i =0; i<length; i++){
            String str = getRandomWord();
            int x = random.nextInt(4);
            if (x < 2){
                stringBuilder1.append(str).append(" ");
                stringBuilder2.append(str).append(" ");
            }else if (x == 2){
                stringBuilder1.append(str).append(" ");
            }else{
                stringBuilder2.append(str).append(" ");
            }
        }

    }

    private static String getRandomWord() {
        int randomIndex = random.nextInt(commonWords.size());
        return commonWords.get(randomIndex);
    }
}