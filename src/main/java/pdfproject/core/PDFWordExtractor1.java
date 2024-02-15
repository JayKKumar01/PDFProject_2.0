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
    private int line = 0;
    private int previousPageNum = 0;
    private StringBuilder wordBuilder = new StringBuilder();
    List<TextPosition> textPositions = new ArrayList<>();

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
//            this.getText(document);
            String text = this.getText(document);
            String[] textArr = text.split("\\n");
            int size = 0;
            for (int i = 0; i< textArr.length; i++){
                int line = i+1;
                String[] wordArr = textArr[i].split(getWordSeparator());
                for (String word: wordArr) {
                    if (!word.trim().isEmpty()) {
                        WordInfo wordInfo = wordList.get(size++);
                        wordInfo.setLine(line);
                        System.out.print(wordInfo.getWord()+" ");
                    }
                }
                System.out.println();
            }
            System.out.println("Lines: "+ textArr.length);
            System.out.println("Words Size: "+size);
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
//        System.out.println(wordList.size());
        return wordList;
    }

    // Uncomment the following method if you want to process each TextPosition individually
    // and store corresponding colors in colorList.


    @Override
    protected void processTextPosition(TextPosition text) {
        super.processTextPosition(text);

        int curPageNum = this.getCurrentPageNo();
        if (previousPageNum != 0 && previousPageNum != curPageNum) {
            line = 1;
        }
        previousPageNum = curPageNum;


        String ch = text.getUnicode();
        if (ch.trim().isEmpty()){
            if (!wordBuilder.toString().trim().isEmpty() && !textPositions.isEmpty()){
                WordInfo wordInfo = new WordInfo(wordBuilder.toString(),textPositions);
                wordInfo.setLine(line);
                wordInfo.setPageNumber(curPageNum);
                int pageNum = modifyPageNum ? curPageNum - minPageNum + 1 : curPageNum;
                wordInfo.setFinalPageNumber(pageNum);
                if(wordInfo.getFontSize() > 1){
                    wordList.add(wordInfo);
                }
            }
            wordBuilder = new StringBuilder();
            textPositions = new ArrayList<>();
        }else {
            wordBuilder.append(ch);
            textPositions.add(text);
        }
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
