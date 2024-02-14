package pdfproject;

import pdfproject.models.DataModel;
import pdfproject.utils.SheetUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class to launch the PDFProject and demonstrate its functionality.
 */
public class Launcher {

    // List to store temporary files created during the execution of the program
    public static List<File> tempFiles = new ArrayList<>();
    public static boolean isInProgress;

    /**
     * The main method that serves as the entry point for the program.
     *
     * @param args Command line arguments (not used in this example).
     */
    public static void main(String[] args) {

        // Retrieve data models from the Excel sheet
        List<DataModel> list = SheetUtil.getData();

        // Check if the data is valid
        if (list == null) {
            System.out.println("Wrong Input Data");
            return;
        }

        // Iterate through the list of DataModel objects
        for (int i=0; i<list.size(); i++) {
            DataModel data = list.get(i);
            if (data == null){
                System.out.println("Skipped Row: "+(i+2));
                continue;
            }

            try {
                // Create an instance of PDFProject with paths from DataModel
                PDFProject pdfProject = new PDFProject(data.getPath1(), data.getPath2());

                // Set page range for the first file if specified in DataModel
                if (data.isRange1()) {
                    pdfProject.setPageRangeForFile1(data.getStartPage1(), data.getEndPage1());
                }

                // Set page range for the second file if specified in DataModel
                if (data.isRange2()) {
                    pdfProject.setPageRangeForFile2(data.getStartPage2(), data.getEndPage2());
                }

                // Create the output folder based on the configuration
                File outputFolder = new File(Config.OUTPUT_IMAGES_PATH);
                outputFolder = new File(outputFolder, data.getOutputFolder());
                pdfProject.setOutputPath(outputFolder.getAbsolutePath());

                // Check if the output folder exists, otherwise skip the current iteration
                if (!outputFolder.exists()) {
                    System.out.println("Couldn't create: " + outputFolder.getAbsolutePath());
                    continue;
                }

                // Perform the PDF comparison
                if (pdfProject.compare()) {
                    System.out.println(data.getOutputFolder() + ": Comparison Done!");
                } else {
                    System.out.println(data.getOutputFolder() + ": Something went wrong!");
                }
            } catch (Exception e) {
                System.out.println("Error: "+e.getMessage());
                System.out.println("Skipped Row: "+(i+2));
            }
        }

        // Clean up temporary files
        for (File f : tempFiles) {
            // Uncomment the next line if you want to print the absolute path of deleted files
            // System.out.println(f.getAbsolutePath());

            // Deleting the file
            if (f.delete()) {
                // Uncomment the next line if you want to print the name of deleted files
                // System.out.println(f.getName() + " Deleted!");
            }
        }
    }
}
