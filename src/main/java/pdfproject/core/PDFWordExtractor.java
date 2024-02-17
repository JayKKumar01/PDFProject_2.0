package pdfproject.core;

import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.models.WordInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Extracts words and related information from a PDF document.
 */
public class PDFWordExtractor extends PDFTextStripper {
    private final List<WordInfo> wordList = new ArrayList<>();
    private int minPageNum = Integer.MAX_VALUE;
    private int maxPageNum = Integer.MIN_VALUE;
    private boolean modifyPageNum;

    private PDDocument document;
    private int line = 0;
    private int previousPageNum = 0;
    private WordInfo prevWordInfo;

    private static final String TEST_WORD = "brought";
    private static boolean Tested = false;
    /**
     * Constructs a PDFWordExtractor for the specified file and pages.
     *
     * @param file  The PDF file to extract words from.
     * @param pages The list of page numbers to extract words from.
     * @throws IOException If an I/O error occurs while loading the PDF file.
     */
    public PDFWordExtractor(File file, List<Integer> pages) throws IOException {
        this.document = PDDocument.load(file, MemoryUsageSetting.setupTempFileOnly());

        // Adding color-related operators for text extraction
        addColorOperators();

        for (int pageNum : pages) {
            maxPageNum = Math.max(maxPageNum, pageNum);
            minPageNum = Math.min(minPageNum, pageNum);
        }
        this.setSortByPosition(true);

        if (pages.isEmpty()) {
            this.getText(document);
        } else {
            modifyPageNum = true;
            for (int page : pages) {
                this.setStartPage(page);
                this.setEndPage(page);
                this.getText(document);
            }
        }
    }

    /**
     * Gets the list of WordInfo extracted from the PDF.
     *
     * @return List of WordInfo objects representing words and related information.
     */
    public List<WordInfo> getWordList() {
        try {
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }

    // Uncomment the following method if you want to process each TextPosition individually
    // and store corresponding colors in colorList.
    //    @Override
    //    protected void processTextPosition(TextPosition text) {
    //        super.processTextPosition(text);
    //        PDColor color = getGraphicsState().getNonStrokingColor();
    //        colorList.add(color);
    //    }

    /**
     * Overrides the writeString method to process each word and related information.
     *
     * @param string         The string to be processed.
     * @param textPositions  The list of TextPosition objects representing positions of the string in the PDF.
     */
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) {
        int curPageNum = this.getCurrentPageNo();
        if (previousPageNum != curPageNum){
            line = 1;
            prevWordInfo = null;
        }


        String[] words = string.split(getWordSeparator());
        int i = 0;

        for (String word : words) {
            if (!word.isEmpty() && textPositions.get(i).getFontSize() > 1) {

                List<TextPosition> positions = new ArrayList<>();
                int len = i + word.length();
                for (int j = i; j < len; j++) {
                    positions.add(textPositions.get(j));
                }
                WordInfo wordInfo = new WordInfo(word, positions);

                if (prevWordInfo != null) {
                    if (prevWordInfo.getPosition() < wordInfo.getPosition()) {
                        line++;
                    }
                }


                wordInfo.setLine(line);
                wordInfo.setPageNumber(curPageNum);
                int pageNum = modifyPageNum ? curPageNum - minPageNum + 1 : curPageNum;
                wordInfo.setFinalPageNumber(pageNum);
                wordList.add(wordInfo);


                prevWordInfo = wordInfo;
            }
            i += word.length() + 1;
        }
        previousPageNum = curPageNum;
    }

    /**
     * Adds color-related operators for text extraction.
     */
    private void addColorOperators() {
        addOperator(new SetStrokingColorSpace());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
        addOperator(new SetStrokingDeviceRGBColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetStrokingDeviceGrayColor());
        addOperator(new SetStrokingColor());
        addOperator(new SetStrokingColorN());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetNonStrokingColorN());
    }
}