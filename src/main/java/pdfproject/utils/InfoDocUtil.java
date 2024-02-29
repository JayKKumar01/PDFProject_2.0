package pdfproject.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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

    private static final int MARGIN = 60;

    /**
     * Creates and saves a PDF document with extracted information.
     *
     * @param list List of WordInfo objects containing extracted information.
     * @param path Path to save the generated PDF document.
     * @return List of lists containing Info objects, grouped by page number.
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
        list.sort(new SortingUtil());
        Map<Integer, List<Info>> pageToInfoListMap = new HashMap<>();

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (WordInfo wordInfo : list) {
            int pageNumber = wordInfo.getFinalPageNumber();
            min = Math.min(pageNumber, min);
            max = Math.max(pageNumber, max);
            Info info = new Info(wordInfo.getWord(), wordInfo.getInfo(), wordInfo.getPDFont(), Base.getColorFromOperations(wordInfo.getTypeList()));
            info.setPositionY(wordInfo.getPosition());
            info.setLine(wordInfo.getLine());
            pageToInfoListMap.computeIfAbsent(pageNumber, k -> new ArrayList<>()).add(info);
        }

        for (int i = min + 1; i < max; i++) {
            if (!pageToInfoListMap.containsKey(i)) {
                pageToInfoListMap.put(i, new ArrayList<>());
            }
        }

        for (List<Info> li : pageToInfoListMap.values()) {
            Iterator<Info> iterator = li.iterator();
            Info a = iterator.hasNext() ? iterator.next() : null;

            while (iterator.hasNext()) {
                Info b = iterator.next();
                if (a != null && a.getInfo().equals(b.getInfo()) && a.getLine() == b.getLine()) {
                    a.setSentence(a.getSentence() + " " + b.getSentence());
                    iterator.remove();
                } else {
                    a = b;
                }
            }
        }

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
        PDDocument document = new PDDocument();

        for (List<Info> infos : masterList) {
            int pageHeight = Math.max(MARGIN * 2 + infos.size() * (2 * 20), 792);

            document.addPage(new PDPage());
            if (infos.isEmpty()) {
                continue;
            }
            PDPage page = document.getPage(document.getNumberOfPages() - 1);
            PDRectangle mediaBox = page.getMediaBox();
            mediaBox.setUpperRightY(pageHeight);
            page.setMediaBox(mediaBox);

            float wordHeight = 0f;

            for (Info in : infos) {
                float yLimit = page.getMediaBox().getHeight() - wordHeight - MARGIN;
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    addTextToContentStream(contentStream, in, yLimit);
                    wordHeight += 2 * 20;
                }
            }
        }

        document.save(path);
        document.close();
    }

    private static void addTextToContentStream(PDPageContentStream contentStream, Info info, float yLimit) throws IOException {
        contentStream.beginText();
        contentStream.setTextMatrix(Matrix.getTranslateInstance(20, yLimit));
        contentStream.setNonStrokingColor(Color.BLACK);

        try {
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.showText(info.getSentence());
        } catch (Exception e) {
            handleFontUnavailableError(contentStream);
        }

        contentStream.setNonStrokingColor(info.getColor());
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);

        contentStream.setTextMatrix(Matrix.getTranslateInstance(20, yLimit - 20));
        contentStream.showText(info.getInfo());
        contentStream.endText();
    }

    private static void handleFontUnavailableError(PDPageContentStream contentStream) throws IOException {
        System.out.println("Error: Font unavailable!");
        contentStream.setNonStrokingColor(Color.GRAY);
        contentStream.showText("-->FONT UNAVAILABLE<--");
        contentStream.setNonStrokingColor(Color.BLACK);
    }

    /**
     * Inner class representing information to be included in the PDF document.
     */
    public static class Info {
        private String sentence;
        private String info;
        private PDFont font;
        private Color color;
        private int positionY;
        private int line;

        public Info(String sentence, String info, PDFont font, Color color) {
            this.sentence = sentence;
            this.info = info;
            this.font = font;
            this.color = color;
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
