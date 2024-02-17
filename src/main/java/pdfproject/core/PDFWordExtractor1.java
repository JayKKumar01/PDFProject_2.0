package pdfproject.core;

import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
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
public class PDFWordExtractor1 extends PDFTextStripper {
    private final List<WordInfo> wordList = new ArrayList<>();
    private int minPageNum = Integer.MAX_VALUE;
    private int maxPageNum = Integer.MIN_VALUE;
    private boolean modifyPageNum;

    private PDDocument document;
    private int line = 1;
    private StringBuilder wordBuilder;
    private List<TextPosition> textPositions;
    private int prevPageNum = -1;
    private TextPosition prevText,prevT;
    private float maxGap  = Integer.MIN_VALUE;

    /**
     * Constructs a PDFWordExtractor for the specified file and pages.
     *
     * @param file  The PDF file to extract words from.
     * @param pages The list of page numbers to extract words from.
     * @throws IOException If an I/O error occurs while loading the PDF file.
     */
    public PDFWordExtractor1(File file, List<Integer> pages) throws IOException {
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


    @Override
    protected void writeString(String string, List<TextPosition> texts) {
        int curPageNum = this.getCurrentPageNo();
        if (prevPageNum != curPageNum){
            line = 1;
            prevText = null;
            prevT = null;
            wordBuilder = new StringBuilder();
            textPositions = new ArrayList<>();
        }

        for (int i=0; i<texts.size();i++){
            TextPosition curText = texts.get(i);
            String ch = curText.getUnicode();
            if (prevText == null){
                if (!ch.trim().isEmpty() && curText.getFontSize() > 1) {
                    wordBuilder.append(ch);
                    textPositions.add(curText);
                    prevT = curText;
                }
            }else {
                if (!ch.trim().isEmpty() && curText.getFontSize() > 1){
                    if (prevT == null){
                        wordBuilder.append(ch);
                        textPositions.add(curText);
                    }else {
                        float yGap = curText.getY() - prevT.getY();
                        float xGap = curText.getX() - (prevT.getX() + prevT.getWidth());
                        float space = 2;
                        if (space < xGap || yGap > 0) {

                            WordInfo wordInfo = new WordInfo(wordBuilder.toString(),textPositions);
                            wordInfo.setPageNumber(curPageNum);
                            int pageNum = modifyPageNum ? curPageNum - minPageNum + 1 : curPageNum;
                            wordInfo.setFinalPageNumber(pageNum);
                            wordInfo.setLine(line);

                            if (wordInfo.getFontSize()>1) {
                                wordList.add(wordInfo);
                            }

                            if (yGap > 0) {
                                line++;
                            }

                            wordBuilder = new StringBuilder();
                            textPositions = new ArrayList<>();
                        }

                        wordBuilder.append(ch);
                        textPositions.add(curText);

                    }
                    prevT = curText;
                }

            }

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
        String lastWord = wordBuilder.toString();
        if (!lastWord.isEmpty() && !textPositions.isEmpty()) {
            WordInfo wordInfo = new WordInfo(wordBuilder.toString(), textPositions);
            wordInfo.setPageNumber(prevPageNum);
            int pageNum = modifyPageNum ? prevPageNum - minPageNum + 1 : prevPageNum;
            wordInfo.setFinalPageNumber(pageNum);
            wordInfo.setLine(line);

            if (wordInfo.getFontSize() > 1) {
                wordList.add(wordInfo);
            }
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
