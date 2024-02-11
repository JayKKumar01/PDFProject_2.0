package pdfproject.models;


import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.enums.Info.Operation;

import java.util.List;

public class WordInfo {
    private Operation type;  // Equal, Deleted, or Added
    private String word;
    private List<TextPosition> positions;
    private int pageNumber;
    private PDColor color;
    public WordInfo(String word){
        this.word = word;
    }

    public WordInfo(Operation type, String word) {
        this.type = type;
        this.word = word;
    }

    public WordInfo(String word, List<TextPosition> positions) {
        this.word = word;
        this.positions = positions;
    }


    public TextPosition getFirstTextPosition() {
        if (positions != null && !positions.isEmpty()) {
            return positions.get(0);
        }
        return null;
    }
    public String getFont() {
        TextPosition position = getFirstTextPosition();
        if (position == null){
            return null;
        }
        return position.getFont().getName().toLowerCase().replace("mt","");
    }

    public String getFontStyle() {
        String font = getFont();
        if (font == null){
            return null;
        }
        if (font.contains("-")){
            return (font.substring(font.lastIndexOf("-")+1)).toLowerCase();
        }else if (font.contains(",")){
            return (font.substring(font.lastIndexOf(",")+1));
        }

        return "regular";
    }

    public int getFontSize() {
        TextPosition position = getFirstTextPosition();
        if (position == null){
            return -1;
        }
        return Math.round(position.getFontSize());
    }

    public PDColor getColor() {
        return color;
    }

    public void setColor(PDColor color) {
        this.color = color;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<TextPosition> getPositions() {
        return positions;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public Operation getType() {
        return type;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return type.name() + ": " + word;
    }
}
