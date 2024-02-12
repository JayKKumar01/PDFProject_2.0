package pdfproject.utils;

import pdfproject.core.PDFWordExtractor;
import pdfproject.models.WordInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for extracting word information from a PDF file.
 */
public class PDFUtil {

    /**
     * Extracts word information from a PDF file for specified pages.
     *
     * @param file   The PDF file from which to extract word information.
     * @param pages  The list of page numbers for which to extract word information.
     * @return       A list of WordInfo objects representing the words on the specified pages.
     */
    public static List<WordInfo> WordList(File file, List<Integer> pages) {
        List<WordInfo> list = new ArrayList<>();
        try {
            // Create a PDFWordExtractor to extract word information
            PDFWordExtractor extractor = new PDFWordExtractor(file, pages);

            // Retrieve the list of WordInfo objects from the extractor
            return extractor.getWordList();
        } catch (Exception e) {
            // Handle any exceptions that may occur during the extraction process
            e.printStackTrace();
        }
        return list;
    }
}
