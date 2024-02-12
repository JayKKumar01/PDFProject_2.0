package pdfproject.utils;

import pdfproject.enums.Info;
import pdfproject.models.WordInfo;

import java.util.ArrayList;
import java.util.List;

public class Base {
    public static boolean isFontInfoSame(WordInfo wordInfo1, WordInfo wordInfo2) {
        if (wordInfo1.getFont() == null || wordInfo2.getFont() == null) {
            return false;
        }
        return wordInfo1.getFont().equals(wordInfo2.getFont()) &&
                wordInfo1.getFontSize() == wordInfo2.getFontSize() &&
                wordInfo1.getFontStyle().equals(wordInfo2.getFontStyle());
    }

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
}
