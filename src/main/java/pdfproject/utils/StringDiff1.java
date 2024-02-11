package pdfproject.utils;

import pdfproject.models.WordInfo;

import java.util.List;

public class StringDiff1 {
    public static List<WordInfo> getList(List<WordInfo> words1, List<WordInfo> words2){
        int m = words1.size();
        int n = words2.size();

        int[][] LCSuffix = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (words1.get(m - i).getWord().equals(words2.get(n - j).getWord())) {
                    LCSuffix[i][j] = LCSuffix[i - 1][j - 1] + 1;
                } else {
                    LCSuffix[i][j] = Math.max(LCSuffix[i - 1][j], LCSuffix[i][j - 1]);
                }
            }
        }

        int i = words1.size();
        int j = words2.size();
        while (i > 0 && j > 0) {
            String w1 = words1.get(m-i).getWord();
            String w2 = words2.get(n-j).getWord();
            if (w1.equals(w2)) {
                System.out.println("Equal: " + w1);
                i--;
                j--;
            } else if (LCSuffix[i - 1][j] > LCSuffix[i][j - 1]) {
                System.out.println("Deleted: " + w1);
                i--;
            } else {
                System.out.println("Added: " + w2);
                j--;
            }
        }

        while (i > 0) {
            System.out.println("Deleted: " + words1.get(m-i).getWord());
            i--;
        }

        while (j > 0) {
            System.out.println("Added: " + words2.get(n-j).getWord());
            j--;
        }

        return null;
    }
}
