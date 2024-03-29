package pdfproject.enums;

/**
 * Enumerations and constants related to PDF project information.
 */
public class Constants {

    /**
     * Enumeration for different operations related to WordInfo.
     */
    public enum Operation {
        DELETED, ADDED, EQUAL, FONT, STYLE, SIZE
    }

    /**
     * FileFormat related to image quality.
     */
    public static class ImageQuality {
        public static final int LOW = 100;
        public static final int MEDIUM = 150;
        public static final int HIGH = 300;
    }

    /**
     * FileFormat related to file formats and directories.
     */
    public static class FileFormat {
        public static final String PDF = ".pdf";
        public static final String DOCX = ".docx";
        public static final String DOC = ".doc";

        public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    }
}
