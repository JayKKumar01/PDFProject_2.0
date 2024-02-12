package pdfproject.utils;

import pdfproject.Config.Colors;
import pdfproject.enums.Info;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.util.List;

/**
 * Utility class providing various helper methods for PDF project.
 */
public class Base {

    /**
     * Gets color based on a list of operations.
     *
     * @param typeList List of operations.
     * @return Color corresponding to the operations.
     */
    public static Color getColorFromOperations(List<Info.Operation> typeList) {
        if (typeList.size() == 1) {
            Info.Operation operation = typeList.get(0);
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
            wordInfo2.addType(Info.Operation.FONT);
            isComma = true;
            builder.append(font1).append(divider).append(font2);
        }
        int size1 = wordInfo1.getFontSize();
        int size2 = wordInfo2.getFontSize();
        if (size1 != size2){
            wordInfo2.addType(Info.Operation.SIZE);
            if (isComma){
                builder.append(",");
            }
            builder.append(size1).append(divider).append(size2);
            isComma = true;
        }
        String style1 = wordInfo1.getFontStyle();
        String style2 = wordInfo2.getFontStyle();
        if (!style1.equals(style2)){
            wordInfo2.addType(Info.Operation.STYLE);
            if (isComma){
                builder.append(",");
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
    public static String getInfo(Info.Operation operation, WordInfo wordInfo) {
        return "["+operation.name()+": (Font: "+wordInfo.getFont()+", Size: "+wordInfo.getFontSize()+", Style: "+wordInfo.getFontStyle()+")]";
    }
}
