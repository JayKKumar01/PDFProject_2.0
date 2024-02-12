package pdfproject.enums;

/**
 * Enumerations and constants related to PDF project information.
 */
public class Info {

    /**
     * Enumeration for different operations related to WordInfo.
     */
    public enum Operation {
        DELETED, ADDED, EQUAL, FONT, STYLE, SIZE
    }

    /**
     * Constants related to image quality.
     */
    public static class ImageQuality {
        public static final int LOW = 100;
        public static final int MEDIUM = 150;
        public static final int HIGH = 300;
    }

    /**
     * Constants related to file formats.
     */
    public static class Constants {
        public static final String PDF = ".pdf";
        public static final String DOCX = ".docx";
        public static final String DOC = ".doc";
    }
}
