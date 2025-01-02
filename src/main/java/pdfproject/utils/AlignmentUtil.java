package pdfproject.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlignmentUtil {

    public static List<List<String>> saveAlignmentImages(File pdf1, File pdf2, List<Integer> pagesPDF1, List<Integer> pagesPDF2, String outputPath) {
        List<List<String>> alignmentFileNames = new ArrayList<>();

        try (PDDocument doc1 = PDDocument.load(pdf1); PDDocument doc2 = PDDocument.load(pdf2)) {
            PDFRenderer renderer1 = new PDFRenderer(doc1);
            PDFRenderer renderer2 = new PDFRenderer(doc2);

            int minPages = Math.min(pagesPDF1.size(), pagesPDF2.size());

            for (int i = 0; i < minPages; i++) {
                int pageIndex1 = pagesPDF1.get(i) - 1; // Convert 1-based to 0-based index
                int pageIndex2 = pagesPDF2.get(i) - 1;

                // Render images for the pages
                BufferedImage image1 = renderer1.renderImageWithDPI(pageIndex1, 300);
                BufferedImage image2 = renderer2.renderImageWithDPI(pageIndex2, 300);

                // Create difference image
                BufferedImage diffImage = createDifferenceImage(image1, image2);

                // Save images to output directory
                String file1 = "Alignment_Page_" + pagesPDF1.get(i) + "_A.png";
                String file2 = "Alignment_Page_" + pagesPDF2.get(i) + "_B.png";
                String file3 = "Alignment_Page_" + pagesPDF1.get(i) + "_C.png";

                saveImage(image1, outputPath, file1);
                saveImage(image2, outputPath, file2);
                saveImage(diffImage, outputPath, file3);

                // Add file names to result list
                List<String> fileNames = new ArrayList<>();
                fileNames.add(file1);
                fileNames.add(file2);
                fileNames.add(file3);
                alignmentFileNames.add(fileNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return alignmentFileNames;
    }

    public static List<String> saveValidationImageSet(int i, BufferedImage pdf1Image, BufferedImage pdf2Image, BufferedImage pdf3Image, String outputPath) {
        List<String> validationFileNames = new ArrayList<>();
        try {
            // Combine pdf1Image and pdf2Image side by side
            int combinedWidth = pdf1Image.getWidth() + pdf2Image.getWidth();
            int combinedHeight = Math.max(pdf1Image.getHeight(), pdf2Image.getHeight());
            BufferedImage combinedImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_RGB);

            // Draw pdf1Image and pdf2Image onto combinedImage
            combinedImage.getGraphics().drawImage(pdf1Image, 0, 0, null);
            combinedImage.getGraphics().drawImage(pdf2Image, pdf1Image.getWidth(), 0, null);

            // Save combined image
            String combinedFile = "Validation_Page_" + i + "_Combined.png";
            saveImage(combinedImage, outputPath, combinedFile);
            validationFileNames.add(combinedFile);

            if (pdf3Image != null) {
                // Save pdf3Image alone
                String singleFile = "Validation_Page_" + i + "_Single.png";
                saveImage(pdf3Image, outputPath, singleFile);
                validationFileNames.add(singleFile);
            } else {
                validationFileNames.add(null); // Indicate missing pdf3Image
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return validationFileNames;
    }

    private static BufferedImage createDifferenceImage(BufferedImage img1, BufferedImage img2) {
        int width = Math.min(img1.getWidth(), img2.getWidth());
        int height = Math.min(img1.getHeight(), img2.getHeight());

        BufferedImage diffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color1 = new Color(img1.getRGB(x, y));
                Color color2 = new Color(img2.getRGB(x, y));

                int diffRed = Math.abs(color1.getRed() - color2.getRed());
                int diffGreen = Math.abs(color1.getGreen() - color2.getGreen());
                int diffBlue = Math.abs(color1.getBlue() - color2.getBlue());

                Color diffColor = new Color(diffRed, diffGreen, diffBlue);
                diffImage.setRGB(x, y, diffColor.getRGB());
            }
        }

        return diffImage;
    }

    private static void saveImage(BufferedImage image, String outputPath, String fileName) {
        if(image == null){
            return;
        }
        try {
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
