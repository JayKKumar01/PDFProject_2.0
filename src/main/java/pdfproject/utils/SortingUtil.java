package pdfproject.utils;

import pdfproject.models.WordInfo;

import java.util.Comparator;

/**
 * Comparator for sorting WordInfo objects based on their positions.
 */
public class SortingUtil implements Comparator<WordInfo> {

    /**
     * Compares two integer values.
     *
     * @param a The first integer.
     * @param b The second integer.
     * @return The result of the comparison.
     */
    private int compare(int a, int b) {
        return Integer.compare(a, b);
    }

    /**
     * Compares two WordInfo objects based on their positions.
     *
     * @param w1 The first WordInfo object.
     * @param w2 The second WordInfo object.
     * @return The result of the comparison.
     */
    @Override
    public int compare(WordInfo w1, WordInfo w2) {
        return compare(w1.getLine(),w2.getLine());
//        return compare(w1.getPosition(), w2.getPosition());
    }
}
