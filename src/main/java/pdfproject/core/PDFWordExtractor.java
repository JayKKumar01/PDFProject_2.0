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
import java.util.List;

/**
 * Extracts words and related information from a PDF document.
 */
public class PDFWordExtractor extends PDFTextStripper {
    private final List<WordInfo> wordList = new ArrayList<>();
    private final List<WordInfo> wordList1 = new ArrayList<>();
    private int minPageNum = Integer.MAX_VALUE;
    private int maxPageNum = Integer.MIN_VALUE;
    private boolean modifyPageNum;

    private PDDocument document;
    private int line = 1;
    private StringBuilder wordBuilder = new StringBuilder();
    List<TextPosition> textPositions = new ArrayList<>();
    private int prevPageNum;
    private TextPosition prevText;

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
            String text = this.getText(document);
        } else {
            modifyPageNum = true;
            for (int page : pages) {
                this.setStartPage(page);
                this.setEndPage(page);
                this.getText(document);
            }
        }
    }


    @Override
    protected void writeString(String string, List<TextPosition> texts) {
        int curPageNum = this.getCurrentPageNo();
        if (prevPageNum != curPageNum){
            line = 1;
        }

        for (int i=0; i<texts.size();i++){
            TextPosition curText = texts.get(i);

            String ch = curText.getUnicode();
            if (prevText != null){
                float yGap = curText.getY() - prevText.getY();
                if (yGap > 0){
                    line++;
                    System.out.println();
                }
            }



            if (ch.trim().isEmpty()){
                prevText = curText;
                continue;
            }



            if (prevText != null){
                float xGap = curText.getX() - (prevText.getX() + prevText.getWidth());
                float yGap = curText.getY() - prevText.getY();
                float space = (float) (0.8 * curText.getWidthOfSpace());

                if (yGap > 0 || xGap > space){
                    if (xGap > space) {
                        System.out.print(" ");
                    }

                    WordInfo wordInfo = new WordInfo(wordBuilder.toString(),textPositions);
                    wordInfo.setPageNumber(curPageNum);
                    int pageNum = modifyPageNum ? curPageNum - minPageNum + 1 : curPageNum;
                    wordInfo.setFinalPageNumber(pageNum);
                    wordInfo.setLine(line);

                    if (wordInfo.getFontSize() > 1) {
                        wordList.add(wordInfo);
                    }

                    wordBuilder = new StringBuilder();
                    textPositions = new ArrayList<>();
                }
            }



            System.out.print(ch);
            wordBuilder.append(ch);
            textPositions.add(curText);


            prevText = curText;

        }
        prevPageNum = curPageNum;
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
        WordInfo wordInfo = new WordInfo(wordBuilder.toString(),textPositions);
        wordInfo.setPageNumber(prevPageNum);
        int pageNum = modifyPageNum ? prevPageNum - minPageNum + 1 : prevPageNum;
        wordInfo.setFinalPageNumber(pageNum);
        wordInfo.setLine(line);

        if (wordInfo.getFontSize() > 1) {
            wordList.add(wordInfo);
        }
        return wordList;
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
