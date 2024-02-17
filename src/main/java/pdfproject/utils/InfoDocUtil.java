package pdfproject.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.util.Matrix;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for generating PDF documents with extracted information.
 */
public class InfoDocUtil {

    /**
     * Creates and saves a PDF document with extracted information.
     *
     * @param list List of WordInfo objects containing extracted information.
     * @param path Path to save the generated PDF document.
     * @return
     */
    public static List<List<Info>> setDoc(List<WordInfo> list, String path) {
        try {
            List<List<Info>> masterList = toMasterList(list);
            addText(masterList, path);
            return masterList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a list of WordInfo objects into a master list of Info objects, grouped by page number.
     *
     * @param list List of WordInfo objects.
     * @return List of lists containing Info objects, grouped by page number.
     */
    private static List<List<Info>> toMasterList(List<WordInfo> list) {
        // Sorting the WordInfo list based on custom criteria
        list.sort(new SortingUtil());

        // Create a map to store Info lists for each page number
        Map<Integer, List<Info>> pageToInfoListMap = new HashMap<>();

        // Iterate through WordInfo objects and populate the map
        for (WordInfo wordInfo : list) {
            int pageNumber = wordInfo.getFinalPageNumber();
            Info info = new Info(wordInfo.getWord(), wordInfo.getInfo(), wordInfo.getPDFont(), Base.getColorFromOperations(wordInfo.getTypeList()));
            info.setPositionY(wordInfo.getPosition());
            info.setLine(wordInfo.getLine());
            info.setPdColor(wordInfo.getColor());
            // If the page number is not already in the map, create a new list
            // Otherwise, add the info to the existing list for that page
            pageToInfoListMap.computeIfAbsent(pageNumber, k -> new ArrayList<>()).add(info);
        }

        // Combine adjacent Info objects with the same info and positionY
        for (List<Info> li : pageToInfoListMap.values()) {
            Iterator<Info> iterator = li.iterator();
            Info a = iterator.hasNext() ? iterator.next() : null;

            while (iterator.hasNext()) {
                Info b = iterator.next();
                if (a != null
                        && a.getInfo().equals(b.getInfo())
                        && a.getLine() == b.getLine()
//                        && a.getPositionY() == b.getPositionY()
                        && a.getPdColor() == b.getPdColor()) {
                    a.setSentence(a.getSentence() + " " + b.getSentence());
                    // Remove the second element (b) after setting the sentence
                    iterator.remove();
                } else {
                    a = b;
                }
            }
        }

        // Convert the map entries to a list of lists, sorted by page number
        return pageToInfoListMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Adds text to a PDF document based on the provided list of Info objects.
     *
     * @param masterList List of lists containing Info objects, grouped by page number.
     * @param path       Path to save the generated PDF document.
     * @throws IOException If an I/O error occurs.
     */
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

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                    contentStream.beginText();
                    contentStream.setTextMatrix(Matrix.getTranslateInstance(20, yLimit));
                    PDColor pdColor = in.getPdColor();
                    if (pdColor == null) {
                        contentStream.setNonStrokingColor(Color.BLACK);
                    }else {
                        contentStream.setNonStrokingColor(pdColor);
                    }
                    contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                    contentStream.showText(in.getSentence());

                    contentStream.setNonStrokingColor(in.getColor());
                    contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);

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

    /**
     * Inner class representing information to be included in the PDF document.
     */
    public static class Info {
        private String sentence;
        private String info;
        private PDFont font;
        private Color color;
        private PDColor pdColor;
        private int positionY;
        private int line;

        public Info(String sentence, String info, PDFont font, Color color) {
            this.sentence = sentence;
            this.info = info;
            this.font = font;
            this.color = color;
        }

        public PDColor getPdColor() {
            return pdColor;
        }

        public void setPdColor(PDColor pdColor) {
            this.pdColor = pdColor;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getPositionY() {
            return positionY;
        }

        public void setPositionY(int positionY) {
            this.positionY = positionY;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
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
