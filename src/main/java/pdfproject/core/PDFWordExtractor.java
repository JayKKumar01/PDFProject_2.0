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

public class PDFWordExtractor extends PDFTextStripper {
    private final List<WordInfo> wordList = new ArrayList<>();
    private final List<PDColor> colorList = new ArrayList<>();

    int colorIndex = 0;
    private int minPageNum = Integer.MAX_VALUE;
    private int maxPageNum = Integer.MIN_VALUE;
    private boolean modifyPageNum;

    private PDDocument document;

    public PDFWordExtractor(File file, List<Integer> pages) throws IOException {
        this.document = PDDocument.load(file, MemoryUsageSetting.setupTempFileOnly());
//        this.document = PDDocument.load(file);
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
        //super();
        for (int pageNum: pages){
            maxPageNum = Math.max(maxPageNum,pageNum);
            minPageNum = Math.min(minPageNum,pageNum);
        }
        if (pages.isEmpty()){
            this.getText(document);
        }else {
            modifyPageNum = true;
            for (int page: pages){
                this.setStartPage(page);
                this.setEndPage(page);
                this.getText(document);
            }
        }

    }

    public List<WordInfo> getWordList() {
        try {
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }

//    @Override
//    protected void processTextPosition(TextPosition text) {
//        super.processTextPosition(text);
//        PDColor color = getGraphicsState().getNonStrokingColor();
//        colorList.add(color);
//    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) {

        String[] words = string.split(getWordSeparator());
        int i = 0;

        for (String word : words) {
            if (!word.isEmpty()) {
                List<TextPosition> positions = new ArrayList<>();
//                if (textPositions.size() > i) {
//                    positions.add(textPositions.get(i));
//                }
                for (int j = i; j < i + word.length(); j++) {
                    positions.add(textPositions.get(j));
                }
                WordInfo wordInfo = new WordInfo(word, positions);
                wordInfo.setPageNumber(this.getCurrentPageNo());
                int pageNum = modifyPageNum ? this.getCurrentPageNo()-minPageNum + 1 : this.getCurrentPageNo();
                wordInfo.setFinalPageNumber(pageNum);
                PDColor color = getGraphicsState().getNonStrokingColor();
                wordInfo.setColor(color);
//                wordInfo.setColor(colorList.get(i+colorIndex));
                wordList.add(wordInfo);
            }
            i += word.length() + 1;
        }
//        colorIndex += textPositions.size();
    }



}
