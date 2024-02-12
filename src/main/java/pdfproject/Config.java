package pdfproject;

import pdfproject.enums.Info.*;

import java.awt.*;

public class Config {
    public static final int WORD_BATCH_SIZE_FOR_COMPARISON = 20; // Adjust this to get faster calculation
    public static final int IMAGE_QUALITY = ImageQuality.LOW; // Adjust this to get faster calculation

    /**
     * Class containing color constants for different operations.
     */
    public static class Colors {
        public static final Color DELETED_OPERATION_COLOR = Color.RED;
        public static final Color ADDED_OPERATION_COLOR = Color.GREEN;
        public static final Color FONT_NAME_OPERATION_COLOR = Color.MAGENTA;
        public static final Color FONT_SIZE_OPERATION_COLOR = Color.BLUE;
        public static final Color FONT_STYLE_OPERATION_COLOR = Color.CYAN;
        public static final Color MULTIPLE_OPERATION_COLOR = Color.BLACK;
    }
}
