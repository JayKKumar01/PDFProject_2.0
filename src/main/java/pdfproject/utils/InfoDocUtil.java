package pdfproject.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import pdfproject.Config.Colors;
import pdfproject.enums.Info;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InfoDocUtil {

    public static void setDoc(List<WordInfo> list,String path) {
        try {
            List<List<Info>> masterList = toMasterList(list);
            addText(masterList,path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<List<Info>> toMasterList(List<WordInfo> list) {
        list.sort(new SortingUtil());
        // Create a map to store Info lists for each page number
        Map<Integer, List<Info>> pageToInfoListMap = new HashMap<>();

        // Iterate through WordInfo objects and populate the map
        for (WordInfo wordInfo : list) {
            int pageNumber = wordInfo.getPageNumber();
            Info info = new Info(wordInfo.getWord(),wordInfo.getInfo(),wordInfo.getPDFont(),Base.getColorFromOperations(wordInfo.getTypeList()));
            info.setWordColor(wordInfo.getColor());

            // If the page number is not already in the map, create a new list
            // Otherwise, add the info to the existing list for that page
            pageToInfoListMap.computeIfAbsent(pageNumber, k -> new ArrayList<>()).add(info);
        }

        // Convert the map entries to a list of lists, sorted by page number

        return pageToInfoListMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
    public static void addText(List<List<Info>> masterList, String path) throws IOException {

        int margin = 60;

        PDDocument document = new PDDocument();
        for (List<Info> infos : masterList) {
            int pageHeight = Math.max(margin * 2 + infos.size() * (2 * 20), 792);
            document.addPage(new PDPage());
            PDPage page = document.getPage(document.getNumberOfPages() - 1);
            PDRectangle mediaBox = page.getMediaBox();
            mediaBox.setUpperRightY(pageHeight);
            page.setMediaBox(mediaBox);

            float wordHeight = 0f;
            for (Info in : infos) {
                float yLimit = page.getMediaBox().getHeight() - wordHeight - margin;

                try(PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);) {

                    contentStream.beginText();

                    contentStream.setTextMatrix(Matrix.getTranslateInstance(20, yLimit));
//
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.setFont(PDType1Font.TIMES_ROMAN,12);
                    contentStream.showText(in.getSentence());

//
                    contentStream.setNonStrokingColor(in.getColor());
                    contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
//
                    contentStream.setTextMatrix(Matrix.getTranslateInstance(20, yLimit - 20));
                    contentStream.showText(in.getInfo());
                    contentStream.endText();
                    wordHeight += 2 * 20;
                }
            }
        }

        document.save(path);
        document.close();
    }



    private static class Info{
        String sentence;
        String info;
        PDFont font;
        Color color;
        PDColor wordColor;

        public Info(String sentence, String info, PDFont font, Color color) {
            this.sentence = sentence;
            this.info = info;
            this.font = font;
            this.color = color;
        }

        public PDColor getWordColor() {
            return wordColor;
        }

        public void setWordColor(PDColor wordColor) {
            this.wordColor = wordColor;
        }

        public String getSentence() {
            return sentence;
        }

        public String getInfo() {
            return info;
        }

        public PDFont getFont() {
            return font;
        }

        public Color getColor() {
            return color;
        }
    }
}
