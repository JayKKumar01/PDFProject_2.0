package pdfproject.utils;

import pdfproject.models.WordInfo;

import java.util.Comparator;

public class SortingUtil implements Comparator<WordInfo> {

    private int compare(int a, int b) {
        return Integer.compare(a,b);
    }

    @Override
    public int compare(WordInfo w1, WordInfo w2) {
        return compare(w1.getPosition(),w2.getPosition());
    }
}