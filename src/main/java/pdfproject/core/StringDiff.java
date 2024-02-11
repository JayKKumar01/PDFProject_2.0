package pdfproject.core;

import pdfproject.enums.Info.Operation;
import pdfproject.models.WordInfo;

import java.awt.datatransfer.FlavorListener;
import java.util.ArrayList;
import java.util.List;

public class StringDiff {
    public static List<WordInfo> List(List<WordInfo> words1, List<WordInfo> words2){
        if (words1 == null || words2 == null){
            return null;
        }
        List<WordInfo> list = new ArrayList<>();
        List<WordInfo> listDel = new ArrayList<>();
        List<WordInfo> listAdd = new ArrayList<>();


        int a = 0;
        int b = 0;

        int m,n;

        int batchSize = 328;

        while (a < words1.size() || b < words2.size()){
            // add only words after last equal (must needed)
            m = Math.min(batchSize,words1.size()-a);
            n = Math.min(batchSize,words2.size()-b);

            List<WordInfo> list1 = new ArrayList<>();
            for (WordInfo wordInfo: listDel){
                if (wordInfo.isShouldCheck()){
                    list1.add(wordInfo);
                }else {
                    list.add(wordInfo);
                }
            }
            list1.addAll(words1.subList(a,a+m));

            List<WordInfo> list2 = new ArrayList<>();
            for (WordInfo wordInfo: listAdd){
                if (wordInfo.isShouldCheck()){
                    list2.add(wordInfo);
                }else {
                    list.add(wordInfo);
                }
            }
            list2.addAll(words2.subList(b,b+n));
            CustomList customList = getList(list1,list2);

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
            String w1 = words1.get(m - i).getWord();
            String w2 = words2.get(n - j).getWord();
            if (w1.equals(w2)) {
                if (!gotEqual) {
                    gotEqual = true;
                    updateDelAddList(resultDel);
                    updateDelAddList(resultAdd);
//                    resultDel.add(new WordInfo(Operation.DELETED, "DoNotAddFromHere"));
                }
                resultEql.add(new WordInfo(Operation.EQUAL, w1));
                i--;
                j--;
            } else if (LCSuffix[i - 1][j] > LCSuffix[i][j - 1]) {
                gotEqual = false;
                resultDel.add(new WordInfo(Operation.DELETED, w1));
                i--;
            } else {
                gotEqual = false;
                resultAdd.add(new WordInfo(Operation.ADDED, w2));
                j--;
            }
        }

        while (i > 0) {
            resultDel.add(new WordInfo(Operation.DELETED, words1.get(m - i).getWord()));
            i--;
        }

        while (j > 0) {
            resultAdd.add(new WordInfo(Operation.ADDED, words2.get(n - j).getWord()));
            j--;
        }

        return new CustomList(resultEql,resultDel,resultAdd);
    }

    private static void updateDelAddList(List<WordInfo> result) {
        for (WordInfo wordInfo: result){
            wordInfo.setShouldCheck(false);
        }
    }

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
