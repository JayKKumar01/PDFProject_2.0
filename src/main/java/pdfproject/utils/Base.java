package pdfproject.utils;

import org.apache.poi.ss.usermodel.IndexedColors;
import pdfproject.Config.Colors;
import pdfproject.enums.Constants;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class providing various helper methods for PDF project.
 */
public class Base {

    public static void transformList(List<WordInfo> inputList, int curIndex) {
        int size = inputList.size();
        if (size - curIndex < 2){
            return;
        }
        Iterator<WordInfo> itr = inputList.iterator();
        for (int i=0; i<curIndex; i++){
            itr.next();
        }
        WordInfo prev = null;
        WordInfo last = inputList.get(size-1);
        int footerY = Integer.MAX_VALUE;
        while (itr.hasNext()){
            prev = itr.next();
            if (prev.getPosition() > last.getPosition()){
                footerY = prev.getPosition();
                //System.out.println("Break: "+prev.getWord());
                break;
            }
        }

        if (prev == null){
            return;
        }
        int line = last.getLine()+1;
        List<WordInfo> list = new ArrayList<>();
        prev.setLine(line);
        prev.setFooter(true);
        list.add(prev);
        itr.remove();
        new SortingUtil();
        while (itr.hasNext()){
            WordInfo current = itr.next();

            if (footerY > current.getPosition()){
                // make sure footerY is the min position in footer
//                footerY =
//                if ()
////                System.out.println("Position 1: "+prev.getPosition()+" -> Position 2: "+current.getPosition());
//                System.out.println("2nd Break: "+prev.getWord()+" : "+current.getWord());
                break;
            }
            if (prev.getPosition() < current.getPosition()){
                line++;
            }
            current.setLine(line);
            current.setFooter(true);
            list.add(current);
            itr.remove();
            prev = current;
        }
        inputList.addAll(list);
    }

    /**
     * Gets color based on a list of operations.
     *
     * @param typeList List of operations.
     * @return Color corresponding to the operations.
     */
    public static Color getColorFromOperations(List<Constants.Operation> typeList) {
        if (typeList.size() == 1) {
            Constants.Operation operation = typeList.get(0);
            switch (operation) {
                case FONT:
                    return Colors.FONT_NAME_OPERATION_COLOR;
                case SIZE:
                    return Colors.FONT_SIZE_OPERATION_COLOR;
                case STYLE:
                    return Colors.FONT_STYLE_OPERATION_COLOR;
                case ADDED:
                    return Colors.ADDED_OPERATION_COLOR;
                case DELETED:
                    return Colors.DELETED_OPERATION_COLOR;
                default:
                    break;
            }
        }
        return Colors.MULTIPLE_OPERATION_COLOR;
    }

    /**
     * Checks if font information of two WordInfo objects is the same.
     *
     * @param wordInfo1 First WordInfo object.
     * @param wordInfo2 Second WordInfo object.
     * @return True if font information is the same, false otherwise.
     */
    public static boolean isFontInfoSame(WordInfo wordInfo1, WordInfo wordInfo2) {
        if (wordInfo1.getFont() == null || wordInfo2.getFont() == null) {
            return false;
        }
        return wordInfo1.getFont().equals(wordInfo2.getFont()) &&
                wordInfo1.getFontSize() == wordInfo2.getFontSize() &&
                wordInfo1.getFontStyle().equals(wordInfo2.getFontStyle());
    }

    /**
     * Updates font information of the second WordInfo object based on the differences with the first one.
     *
     * @param wordInfo1 First WordInfo object.
     * @param wordInfo2 Second WordInfo object.
     */
    public static void updateFontInfo(WordInfo wordInfo1, WordInfo wordInfo2) {

        StringBuilder builder = new StringBuilder();
        String divider = " : ";
        builder.append("[");
        boolean isComma = false;

        String font1 = wordInfo1.getFont();
        String font2 = wordInfo2.getFont();

        if (!font1.equals(font2)){
            wordInfo2.addType(Constants.Operation.FONT);
            isComma = true;
            builder.append(font1).append(divider).append(font2);
        }
        int size1 = wordInfo1.getFontSize();
        int size2 = wordInfo2.getFontSize();
        if (size1 != size2){
//            System.out.println(wordInfo2.getWord()+": "+wordInfo2.getTypeList().size());
            wordInfo2.addType(Constants.Operation.SIZE);
            if (isComma){
                builder.append(", ");
            }
            builder.append("Size(").append(size1).append(divider).append(size2).append(")");
            isComma = true;
        }
        String style1 = wordInfo1.getFontStyle();
        String style2 = wordInfo2.getFontStyle();
        if (!style1.equals(style2)){
            wordInfo2.addType(Constants.Operation.STYLE);
            if (isComma){
                builder.append(", ");
            }
            builder.append(style1).append(divider).append(style2);
        }
        builder.append("]");
        wordInfo2.setInfo(builder.toString());
    }

    /**
     * Gets information string based on the operation and WordInfo.
     *
     * @param operation Type of operation.
     * @param wordInfo  WordInfo object.
     * @return Information string.
     */
    public static String getInfo(Constants.Operation operation, WordInfo wordInfo) {
        return "["+operation.name()+": (Font: "+wordInfo.getFont()+", Size: "+wordInfo.getFontSize()+", Style: "+wordInfo.getFontStyle()+")]";
    }

    public static IndexedColors getIndexedColor(Color color) {
        if (color.equals(Colors.DELETED_OPERATION_COLOR)) {
            return IndexedColors.RED;
        } else if (color.equals(Colors.ADDED_OPERATION_COLOR)) {
            return IndexedColors.GREEN;
        } else if (color.equals(Colors.FONT_NAME_OPERATION_COLOR)) {
            return IndexedColors.PINK;
        } else if (color.equals(Colors.FONT_SIZE_OPERATION_COLOR)) {
            return IndexedColors.BLUE;
        } else if (color.equals(Colors.FONT_STYLE_OPERATION_COLOR)) {
            return IndexedColors.LIGHT_BLUE;
        }
        return IndexedColors.BLACK;
    }



}