package pdfproject;

import pdfproject.enums.Constants.*;

import java.awt.*;

/**
 * Configuration class for PDF comparison application settings.
 */
public class Config {

    /**
     * Specifies the batch size for word comparison, influencing processing speed.
     * Adjust this to optimize calculation speed.
     */
    public static final int WORD_BATCH_SIZE_FOR_COMPARISON = 20;

    /**
     * Sets the image quality parameter for processing, balancing speed and clarity.
     * Adjust this to optimize calculation speed.
     */
    public static final int IMAGE_QUALITY = ImageQuality.LOW;

    /**
     * Defines the file path for the input data (Excel file) containing PDF paths for comparison.
     * Modify this path as needed based on your directory structure.
     */
    public static final String INPUT_PATH = System.getProperty("user.dir") + "\\src\\main\\java\\files\\Data.xlsx";

    /**
     * Specifies the directory path where the output images highlighting differences will be stored.
     * Modify this path based on your desired output location.
     */
    public static final String OUTPUT_IMAGES_PATH = "E:\\Sample\\new\\img";

    /**
     * UserGuide class containing additional user guide configurations.
     */
    public static class UserGuide {
        /**
         * Indicates whether the application should check if Microsoft Word is running before converting.
         * Set to 'true' if the check is required; otherwise, set to 'false'.
         */
        public static final boolean CHECK_IF_WORD_IS_RUNNING = true;
    }

    /**
     * Class containing color constants for different operations.
     */
    public static class Colors {
        /**
         * Color constant for deleted operation.
         */
        public static final Color DELETED_OPERATION_COLOR = Color.RED;

        /**
         * Color constant for added operation.
         */
        public static final Color ADDED_OPERATION_COLOR = Color.GREEN;

        /**
         * Color constant for font name operation.
         */
        public static final Color FONT_NAME_OPERATION_COLOR = Color.MAGENTA;

        /**
         * Color constant for font size operation.
         */
        public static final Color FONT_SIZE_OPERATION_COLOR = Color.BLUE;

        /**
         * Color constant for font style operation.
         */
        public static final Color FONT_STYLE_OPERATION_COLOR = Color.CYAN;

        /**
         * Color constant for multiple operations.
         */
        public static final Color MULTIPLE_OPERATION_COLOR = Color.BLACK;
    }
}
