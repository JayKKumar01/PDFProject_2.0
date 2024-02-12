package pdfproject.core;
import org.apache.poi.sl.draw.geom.GuideIf;
import pdfproject.enums.Info.Operation;
import pdfproject.models.WordInfo;
import pdfproject.utils.Base;

import java.awt.datatransfer.FlavorListener;
import java.util.ArrayList;
import java.util.List;

import static pdfproject.Config.WORD_BATCH_SIZE_FOR_COMPARISON;

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

        int batchSize = WORD_BATCH_SIZE_FOR_COMPARISON;

        //long currentTime = System.currentTimeMillis();

        while (a < words1.size() || b < words2.size()){
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

        //System.out.println(System.currentTimeMillis()-currentTime+" millis");

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
                if (Base.isFontInfoSame(wordInfo1,wordInfo2)) {
                    wordInfo1.addType(Operation.EQUAL);
                    resultEql.add(wordInfo1);
                }else{
                    Base.updateFontInfo(wordInfo1,wordInfo2);
                    resultEql.add(wordInfo2);
                }
                i--;
                j--;
            } else if (LCSuffix[i - 1][j] > LCSuffix[i][j - 1]) {
                gotEqual = false;
                wordInfo1.addType(Operation.DELETED);
                String info = Base.getInfo(Operation.DELETED, wordInfo1);
                wordInfo1.setInfo(info);
                resultDel.add(wordInfo1);
                i--;
            } else {
                gotEqual = false;
                wordInfo2.addType(Operation.ADDED);
                String info = Base.getInfo(Operation.ADDED,wordInfo2);//"ADDED [Font: "+wordInfo2.getFont()+", Size: "+wordInfo2.getFontSize()+", Style: "+wordInfo2.getFontStyle()+"]";
                wordInfo2.setInfo(info);
                resultAdd.add(wordInfo2);
                j--;
            }
        }

        while (i > 0) {
            WordInfo wordInfo1 = words1.get(m-i);
            wordInfo1.addType(Operation.DELETED);
            String info = Base.getInfo(Operation.DELETED,wordInfo1);//"DELETED [Font: "+wordInfo1.getFont()+", Size: "+wordInfo1.getFontSize()+", Style: "+wordInfo1.getFontStyle()+"]";
            wordInfo1.setInfo(info);
            resultDel.add(wordInfo1);
            i--;
        }

        while (j > 0) {
            WordInfo wordInfo2 = words2.get(n-j);
            wordInfo2.addType(Operation.ADDED);
            String info = Base.getInfo(Operation.ADDED,wordInfo2);//"ADDED [Font: "+wordInfo2.getFont()+", Size: "+wordInfo2.getFontSize()+", Style: "+wordInfo2.getFontStyle()+"]";
            wordInfo2.setInfo(info);
            resultAdd.add(wordInfo2);
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
