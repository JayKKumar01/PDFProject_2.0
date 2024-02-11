package org.example;

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
        String sentence1 = "station snow car teacher shirt teacher keyboard police pants";
        String sentence2 = "station snow watch car shirt keyboard police pants";
        //station snow car teacher shirt teacher keyboard police pants
        //station snow watch car shirt keyboard police pants

//        StringBuilder stringBuilder1 = new StringBuilder();
//        StringBuilder stringBuilder2 = new StringBuilder();
//
//        generateSentences(stringBuilder1,stringBuilder2,10);
//        String sentence1 = stringBuilder1.toString();
//        String sentence2 = stringBuilder2.toString();

        System.out.println(sentence1 +"\n"+sentence2);

        String[] words1 = sentence1.split(" ");
        String[] words2 = sentence2.split(" ");


        int m = words1.length;
        int n = words2.length;

        int[][] LCSuffix = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (words1[m - i].equals(words2[n - j])) {
                    LCSuffix[i][j] = LCSuffix[i - 1][j - 1] + 1;
                } else {
                    LCSuffix[i][j] = Math.max(LCSuffix[i - 1][j], LCSuffix[i][j - 1]);
                }
            }
        }

        //print(LCSuffix);

        int i = words1.length;
        int j = words2.length;
        while (i > 0 && j > 0) {
            if (words1[m-i].equals(words2[n-j])) {
                System.out.println("Equal: " + words1[m-i]);
                i--;
                j--;
            } else if (LCSuffix[i - 1][j] > LCSuffix[i][j - 1]) {
                System.out.println("Deleted: " + words1[m-i]);
                i--;
            } else {
                System.out.println("Added: " + words2[n-j]);
                j--;
            }
        }

        while (i > 0) {
            System.out.println("Deleted: " + words1[m-i]);
            i--;
        }

        while (j > 0) {
            System.out.println("Added: " + words2[n-j]);
            j--;
        }
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

    private static void print(int[][] lcSuffix) {
        for (int i = 0; i < lcSuffix.length; i++) {
            for (int j = 0; j < lcSuffix[i].length; j++) {
                System.out.print(lcSuffix[i][j] + "  ");
            }
            System.out.println();
        }
    }
}