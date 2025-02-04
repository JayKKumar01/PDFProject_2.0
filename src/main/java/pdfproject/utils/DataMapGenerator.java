package pdfproject.utils;

import pdfproject.models.MapModel;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class DataMapGenerator {

    // Predefined file paths to be copied
    private static final String PATH1 = "E:\\Report Project\\index.html";
    private static final String PATH2 = "E:\\Report Project\\script.js";
    private static final String PATH3 = "E:\\Report Project\\styles.css";

    public static void generateDataMapJs(List<MapModel> models, String outputDir) {
        // Ensure output directory exists
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // File path for data-map.js
        String filePath = outputDir + File.separator + "data-map.js";

        // StringBuilder to store JavaScript content
        StringBuilder jsContent = new StringBuilder();
        jsContent.append("// Map to store the item image data\n");
        jsContent.append("const itemImageMap = new Map([\n");

        int itemNumber = 1; // Start numbering items from 1

        for (MapModel model : models) {
            List<List<String>> validationImages = model.getValidationList();
            List<List<String>> alignmentImages = model.getAlignmentList();

            jsContent.append("    [\"Item ").append(itemNumber).append("\", {\n");

            // Validation images
            jsContent.append("        validationImages: [\n");
            for (List<String> validation : validationImages) {
                jsContent.append("            ").append(validation.toString()).append(",\n");
            }
            jsContent.append("        ],\n");

            // Alignment images
            jsContent.append("        alignmentImages: [\n");
            for (List<String> alignment : alignmentImages) {
                jsContent.append("            ").append(alignment.toString()).append(",\n");
            }
            jsContent.append("        ]\n");

            jsContent.append("    }],\n");

            itemNumber++; // Increment for next item
        }

        jsContent.append("]);\n\n");
        jsContent.append("// Example: Log the map to check the result\n");
        jsContent.append("console.log(itemImageMap);\n");

        // Write to file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsContent.toString());
            System.out.println("data-map.js generated successfully!");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        // Copy predefined files
        copyFile(PATH1, outputDir);
        copyFile(PATH2, outputDir);
        copyFile(PATH3, outputDir);
    }

    private static void copyFile(String sourcePath, String outputDir) {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            System.err.println("File not found: " + sourcePath);
            return;
        }

        File destinationFile = new File(outputDir, sourceFile.getName());
        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied: " + sourceFile.getName());
        } catch (IOException e) {
            System.err.println("Error copying file: " + sourceFile.getName() + " - " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Example data
        List<MapModel> models = List.of(
                new MapModel(
                        List.of(
                                List.of("align1.jpg", "align2.jpg", "align3.jpg"),
                                List.of("align4.jpg", "align5.jpg", "align6.jpg")
                        ),
                        List.of(
                                List.of("val1.jpg", "val2.jpg"),
                                List.of("val3.jpg", "val4.jpg")
                        )
                ),
                new MapModel(
                        List.of(
                                List.of("align7.jpg", "align8.jpg", "align9.jpg"),
                                List.of("align10.jpg", "align11.jpg", "align12.jpg")
                        ),
                        List.of(
                                List.of("val5.jpg", "val6.jpg"),
                                List.of("val7.jpg", "val8.jpg")
                        )
                )
        );

        generateDataMapJs(models, "output_directory_path");
    }
}
