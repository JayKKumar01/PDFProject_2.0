package pdfproject.utils;

import pdfproject.core.PDFWordExtractor;
import pdfproject.models.WordInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PDFUtil {
    public static List<WordInfo> WordList(File file,List<Integer> pages){
        List<WordInfo> list = new ArrayList<>();
        try {
            PDFWordExtractor extractor = new PDFWordExtractor(file,pages);
            return extractor.getWordList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
