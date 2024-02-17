package pdfproject.core;

import pdfproject.enums.Constants.Operation;
import pdfproject.models.WordInfo;
import pdfproject.utils.Base;

import java.util.ArrayList;
import java.util.List;

import static pdfproject.Config.WORD_BATCH_SIZE_FOR_COMPARISON;

/**
 * Class for comparing two lists of WordInfo objects and identifying differences.
 */
public class StringDiff {

    /**
     * Compares two lists of WordInfo objects and identifies differences.
     *
     * @param words1 List of WordInfo objects from the first source.
     * @param words2 List of WordInfo objects from the second source.
     * @return A list of WordInfo objects representing the differences between the two lists.
     */
    public static List<WordInfo> List(List<WordInfo> words1, List<WordInfo> words2) {
        if (words1 == null || words2 == null) {
            return null;
        }

        List<WordInfo> list = new ArrayList<>();
        List<WordInfo> listDel = new ArrayList<>();
        List<WordInfo> listAdd = new ArrayList<>();

        int a = 0;
        int b = 0;

        int m, n;

        int batchSize = WORD_BATCH_SIZE_FOR_COMPARISON;

        while (a < words1.size() || b < words2.size()) {
            m = Math.min(batchSize, words1.size() - a);
            n = Math.min(batchSize, words2.size() - b);

            List<WordInfo> list1 = new ArrayList<>();
            for (WordInfo wordInfo : listDel) {
                if (wordInfo.isShouldCheck()) {
                    list1.add(wordInfo);
                } else {
                    list.add(wordInfo);
                }
            }
            list1.addAll(words1.subList(a, a + m));

            List<WordInfo> list2 = new ArrayList<>();
            for (WordInfo wordInfo : listAdd) {
                if (wordInfo.isShouldCheck()) {
                    list2.add(wordInfo);
                } else {
                    list.add(wordInfo);
                }
            }
            list2.addAll(words2.subList(b, b + n));
            CustomList customList = getList(list1, list2);

            list.addAll(customList.getResultEql());
            listDel = customList.getResultDel();
            listAdd = customList.getResultAdd();

            a += m;
            b += n;
        }

        list.addAll(listDel);
        list.addAll(listAdd);

        return list;
    }

    /**
     * Gets a custom list containing information about equal, deleted, and added WordInfo objects.
     *
     * @param words1 List of WordInfo objects from the first source.
     * @param words2 List of WordInfo objects from the second source.
     * @return CustomList containing equal, deleted, and added WordInfo objects.
     */
    public static CustomList getList(List<WordInfo> words1, List<WordInfo> words2) {
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

        List<WordInfo> resultEql = new ArrayList<>();
        List<WordInfo> resultDel = new ArrayList<>();
        List<WordInfo> resultAdd = new ArrayList<>();
        boolean gotEqual = false;

        int i = m;
        int j = n;
        while (i > 0 && j > 0) {
            WordInfo wordInfo1 = words1.get(m - i);
            WordInfo wordInfo2 = words2.get(n - j);
            String w1 = wordInfo1.getWord();
            String w2 = wordInfo2.getWord();
            if (w1.equals(w2)) {
                if (!gotEqual) {
                    gotEqual = true;
                    updateDelAddList(resultDel);
                    updateDelAddList(resultAdd);
                }
                if (Base.isFontInfoSame(wordInfo1, wordInfo2)) {
                    wordInfo1.addType(Operation.EQUAL);
                    resultEql.add(wordInfo1);
                } else {
                    Base.updateFontInfo(wordInfo1, wordInfo2);
                    resultEql.add(wordInfo2);
                }
                i--;
                j--;
            } else if (LCSuffix[i - 1][j] > LCSuffix[i][j - 1]) {
                if (confirmDel(words1,m-i,words2,n-j)){
                    gotEqual = false;
                    wordInfo1.addType(Operation.DELETED);
                    String info = Base.getInfo(Operation.DELETED, wordInfo1);
                    wordInfo1.setInfo(info);
                    resultDel.add(wordInfo1);
                }

                i--;
            } else {
                if (confirmAdd(words1,m-i,words2,n-j)) {
                    gotEqual = false;
                    wordInfo2.addType(Operation.ADDED);
                    String info = Base.getInfo(Operation.ADDED, wordInfo2);
                    wordInfo2.setInfo(info);
                    resultAdd.add(wordInfo2);
                }
                j--;
            }
        }

        while (i > 0) {
            WordInfo wordInfo1 = words1.get(m - i);
            wordInfo1.addType(Operation.DELETED);
            String info = Base.getInfo(Operation.DELETED, wordInfo1);
            wordInfo1.setInfo(info);
            resultDel.add(wordInfo1);
            i--;
        }

        while (j > 0) {
            WordInfo wordInfo2 = words2.get(n - j);
            wordInfo2.addType(Operation.ADDED);
            String info = Base.getInfo(Operation.ADDED, wordInfo2);
            wordInfo2.setInfo(info);
            resultAdd.add(wordInfo2);
            j--;
        }

        return new CustomList(resultEql, resultDel, resultAdd);
    }



    private static boolean confirmAdd(List<WordInfo> words1, int i, List<WordInfo> words2, int j) {
        int s1 = words1.size();
        if (s1 == 0 || i < 0 || i+2 > s1){
            return true;
        }
        String secondWord = words2.get(j).getWord();


        String curWord = words1.get(i).getWord();
        String nextWord = words1.get(i+1).getWord();
        return !(curWord + nextWord).equals(secondWord);
    }

    private static boolean confirmDel(List<WordInfo> words1, int i, List<WordInfo> words2, int j) {
        int s2 = words2.size();
        if (s2 == 0 || j == 0 || j > s2){
            return true;
        }
        String secondWord = words2.get(j-1).getWord();
        int s1 = words1.size();
        String curWord = words1.get(i).getWord();
        // when curWord is first part
        if (i + 1 < s1){
            String nextWord = words1.get(i+1).getWord(); // combine all and at the end re run the script
            if((curWord + nextWord).equals(secondWord)){
                return false;
            }
        }

        // when curWord is second part
        if (i != 0){
            String lastWord = words1.get(i-1).getWord();
            return !(lastWord + curWord).equals(secondWord);
        }

        // need to check if this word is deleted when 2nd page has parts of this word

        return true;
    }

    /**
     * Updates the 'shouldCheck' attribute for WordInfo objects in the provided list.
     *
     * @param result List of WordInfo objects to be updated.
     */
    private static void updateDelAddList(List<WordInfo> result) {
        for (WordInfo wordInfo : result) {
            wordInfo.setShouldCheck(false);
        }
    }

    /**
     * CustomList class to encapsulate lists of equal, deleted, and added WordInfo objects.
     */
    public static class CustomList {
        private List<WordInfo> resultEql;
        private List<WordInfo> resultDel;
        private List<WordInfo> resultAdd;

        public CustomList(List<WordInfo> resultEql, List<WordInfo> resultDel, List<WordInfo> resultAdd) {
            this.resultEql = resultEql;
            this.resultDel = resultDel;
            this.resultAdd = resultAdd;
        }

        public List<WordInfo> getResultEql() {
            return resultEql;
        }

        public void setResultEql(List<WordInfo> resultEql) {
            this.resultEql = resultEql;
        }

        public List<WordInfo> getResultDel() {
            return resultDel;
        }

        public void setResultDel(List<WordInfo> resultDel) {
            this.resultDel = resultDel;
        }

        public List<WordInfo> getResultAdd() {
            return resultAdd;
        }

        public void setResultAdd(List<WordInfo> resultAdd) {
            this.resultAdd = resultAdd;
        }
    }
}
