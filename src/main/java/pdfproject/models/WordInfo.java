package pdfproject.models;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.enums.Info.Operation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents information about a word in a PDF document.
 */

public class WordInfo {
    private final List<Operation> typeList = new ArrayList<>();
    private String word;
    private String info;
    private List<TextPosition> positions;
    private int pageNumber;
    private int finalPageNumber = -1;
    private PDColor color;
    private boolean shouldCheck = true;

    // Constructors
    public WordInfo(String word) {
        this.word = word;
    }

    public WordInfo(Operation type, String word) {
        typeList.add(type);
        this.word = word;
    }

    public WordInfo(String word, List<TextPosition> positions) {
        this.word = word;
        this.positions = positions;
    }

    // Accessors and Mutators


    public int getFinalPageNumber() {
        return finalPageNumber;
    }

    public void setFinalPageNumber(int finalPageNumber) {
        this.finalPageNumber = finalPageNumber;
    }

    public String getWord() {
        return word;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Operation> getTypeList() {
        return typeList;
    }

    public void addType(Operation type) {
        if ((type == Operation.EQUAL || type == Operation.ADDED || type == Operation.DELETED) && !typeList.isEmpty()){
            typeList.clear();
        }
        typeList.add(type);
    }

    public boolean isShouldCheck() {
        return shouldCheck;
    }

    public void setShouldCheck(boolean shouldCheck) {
        this.shouldCheck = shouldCheck;
    }

    public List<TextPosition> getPositions() {
        return positions;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public PDColor getColor() {
        return color;
    }

    public void setColor(PDColor color) {
        this.color = color;
    }

    // Text Position and Font-related methods
    public TextPosition getFirstTextPosition() {
        if (positions != null && !positions.isEmpty()) {
            return positions.get(0);
        }
        return null;
    }

    public PDFont getPDFont() {
        TextPosition position = getFirstTextPosition();
        if (position == null) {
            return null;
        }
        return position.getFont();
    }

    public String getJustFont() {
        return getPDFont().getName();
    }

    public String getFont() {
        String font = getJustFont();
        if (font == null) {
            return null;
        }
        if (font.contains("+")){
            font = font.substring(font.indexOf("+")+1);
        }
        if (font.contains("-")){
            font = font.replace(font.substring(font.lastIndexOf("-")),"");
        }else if (font.contains(",")){
            font = font.replace(font.substring(font.lastIndexOf(",")),"");
        }
        return font;
    }

    public String getFontStyle() {
        String font = getJustFont();
        if (font == null){
            return "unknown";
        }
        font = font.toLowerCase().replace("mt","");
        if (font.contains("-")){
            return (font.substring(font.lastIndexOf("-")+1));
        }else if (font.contains(",")){
            return (font.substring(font.lastIndexOf(",")+1));
        }

        return "regular";
    }

    public int getFontSize() {
        TextPosition position = getFirstTextPosition();
        if (position == null) {
            return -1;
        }
        return Math.round(position.getFontSize());
    }

    public int getPosition(){
        return (int) getFirstTextPosition().getY();
    }

    // toString method
    @Override
    public String toString() {
        return typeList.get(0).name() + ": " + word;
    }
}
