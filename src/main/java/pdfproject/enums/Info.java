package pdfproject.enums;

public class Info {
    public static class ImageQuality {
        public static final int LOW = 100;
        public static final int MEDIUM = 150;
        public static final int HIGH = 300;
    }
    public class Constants {
        public static final String PDF = ".pdf";

        public static final String DOCX = ".docx";
        public static final String DOC = ".doc";
    }
    public enum Operation{
        DELETED,ADDED,EQUAL,FONT,STYLE,SIZE
    }
}
