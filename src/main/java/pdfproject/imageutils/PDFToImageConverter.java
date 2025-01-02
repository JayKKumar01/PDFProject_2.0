package pdfproject.imageutils;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import pdfproject.Config;
import pdfproject.utils.AlignmentUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting PDF documents to images.
 */
public class PDFToImageConverter {

    private static final String IMAGE_FORMAT = "png";
    private static final String IMAGE_NAME_PREFIX = "combined_page_";

    /**
     * Creates a list of BufferedImages from a PDF file for specified pages.
     *
     * @param pdfFile PDF file to convert.
     * @param pages   List of page numbers to convert, empty for all pages.
     * @return List of BufferedImages for the specified pages.
     * @throws IOException If an error occurs during PDF processing.
     */
    public static int getImagesSizeFromPdf(File pdfFile, List<Integer> pages) throws IOException {
        if (!pages.isEmpty()) {
            return pages.size();
        }
        try (PDDocument document = PDDocument.load(pdfFile)) {
            // If no specific pages are provided, convert all pages
            return document.getNumberOfPages();
        }
    }

    public static BufferedImage createImageFromPdf(File pdfFile, List<Integer> pagesPDF, int index) throws IOException {
        if (index < pagesPDF.size()) {
            index = pagesPDF.get(index) - 1;
        }
        try (PDDocument document = PDDocument.load(pdfFile, MemoryUsageSetting.setupMainMemoryOnly())) {
            PDFRenderer renderer = new PDFRenderer(document);
            if (index + 1 > document.getNumberOfPages()) {
                return null;
            }
            return renderer.renderImageWithDPI(index, Config.IMAGE_QUALITY); // set the DPI to 300
        }
    }

    /**
     * Combines images from three PDF files and saves the resulting image to the specified output path.
     *
     * @param pdf1       First PDF file.
     * @param pdf2       Second PDF file.
     * @param pdf3       Third PDF file.
     * @param outputPath Output path for the combined image.
     * @param pagesPDF1  List of page numbers to extract from the first PDF.
     * @param pagesPDF2  List of page numbers to extract from the second PDF.
     * @throws IOException If an error occurs during PDF processing or image creation.
     */
    public static List<List<String>> combineImagesFromPDFsAndSave(File pdf1, File pdf2, File pdf3, String outputPath, List<Integer> pagesPDF1, List<Integer> pagesPDF2) throws IOException {
        List<List<String>> list = new ArrayList<>();
        // Load the images for each page from the three PDF files
        int len1 = getImagesSizeFromPdf(pdf1, pagesPDF1);
        int len2 = getImagesSizeFromPdf(pdf2, pagesPDF2);
        int len3 = getImagesSizeFromPdf(pdf3, new ArrayList<>());

        // Determine the number of pages in the combined PDF (the larger of the three)
        int numPages = Math.max(Math.max(len1, len2), len3);

        // Combine the images of each page side by side and write to a new image file
        for (int i = 0; i < numPages; i++) {
            BufferedImage pdf1Image = createImageFromPdf(pdf1, pagesPDF1, i);
            BufferedImage pdf2Image = createImageFromPdf(pdf2, pagesPDF2, i);
            BufferedImage pdf3Image = createImageFromPdf(pdf3, new ArrayList<>(), i);

            list.add(combineAndSaveImages(i, pdf1Image, pdf2Image, pdf3Image, outputPath));
        }

        System.out.println("Combined images created at: " + outputPath);
        return list;
    }



    private static List<String> combineAndSaveImages(int i, BufferedImage pdf1Image, BufferedImage pdf2Image, BufferedImage pdf3Image, String outputPath) throws IOException {

        List<String> list = AlignmentUtil.saveValidationImageSet(i, pdf1Image, pdf2Image, pdf3Image, outputPath);

        BufferedImage combinedImage = getBufferedImage(pdf1Image, pdf2Image, pdf3Image);

        if (pdf1Image != null) {
            combinedImage.createGraphics().drawImage(pdf1Image, 0, 0, null);
        }
        if (pdf2Image != null) {
            combinedImage.createGraphics().drawImage(pdf2Image, pdf1Image != null ? pdf1Image.getWidth() : 0, 0, null);
        }
        if (pdf3Image != null) {
            combinedImage.createGraphics().drawImage(pdf3Image,
                    (pdf1Image != null ? pdf1Image.getWidth() : 0) +
                            (pdf2Image != null ? pdf2Image.getWidth() : 0), 0, null);
        }

        File combinedImageFile = new File(outputPath, IMAGE_NAME_PREFIX + (i + 1) + "." + IMAGE_FORMAT);
        ImageIO.write(combinedImage, IMAGE_FORMAT, combinedImageFile);
        System.out.println("Created at: " + combinedImageFile.getAbsolutePath());
        return list;
    }

    private static BufferedImage getBufferedImage(BufferedImage pdf1Image, BufferedImage pdf2Image, BufferedImage pdf3Image) {
        int width =
                (pdf1Image != null ? pdf1Image.getWidth() : 0) +
                        (pdf2Image != null ? pdf2Image.getWidth() : 0) +
                        (pdf3Image != null ? pdf3Image.getWidth() : 0);

        int height = Math.max(pdf1Image != null ? pdf1Image.getHeight() : 0,
                pdf2Image != null ? pdf2Image.getHeight() : 0);

        height = Math.max(height, pdf3Image != null ? pdf3Image.getHeight() : 0);

        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
}
