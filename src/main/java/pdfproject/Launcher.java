package pdfproject;

import pdfproject.models.DataModel;
import pdfproject.utils.SheetUtil;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class to launch the PDFProject and demonstrate its functionality.
 */
public class Launcher {

    // List to store temporary files created during the execution of the program
    public static List<File> tempFiles = new ArrayList<>();

    /**
     * The main method that serves as the entry point for the program.
     * @param args Command line arguments (not used in this example).
     */
    public static void main(String[] args) {

        List<DataModel> list = SheetUtil.getData();
        for (DataModel data: list){
            PDFProject pdfProject = new PDFProject(data.getPath1(),data.getPath2());
            if (data.isRange1()){
                pdfProject.setPageRangeForFile1(data.getStartPage1(),data.getEndPage1());
            }
            if (data.isRange2()){
                pdfProject.setPageRangeForFile2(data.getStartPage2(),data.getEndPage2());
            }
            File outputFolder = new File(Config.OUTPUT_IMAGES_PATH);
            outputFolder = new File(outputFolder,data.getOutputFolder());
            pdfProject.setOutputPath(outputFolder.getAbsolutePath());
            if (!outputFolder.exists()){
                System.out.println("Couldn't create: "+outputFolder.getAbsolutePath());
                continue;
            }
            if (pdfProject.compare()){
                System.out.println(data.getOutputFolder()+": Comparison Done!");
            }else {
                System.out.println(data.getOutputFolder()+": Something went wrong!");
            }

        }


        for (File f : tempFiles) {
            // Uncomment the next line if you want to print the absolute path of deleted files
            // System.out.println(f.getAbsolutePath());

            // Deleting the file
            if (f.delete()) {
                // Uncomment the next line if you want to print the name of deleted files
                // System.out.println(f.getName() + " Deleted!");
            }
        }

        boolean x = true;
        if (x){
            return;
        }

        // Paths to the DOCX files to be compared
        String pdf1 = "E:/Sample/new/1.docx";
        String pdf2 = "E:/Sample/new/2.docx";

        // Creating an instance of PDFProject with the provided DOCX file paths
        PDFProject pdfProject = new PDFProject(pdf1, pdf2);

        // Setting a specific page range for the second file (optional)
        pdfProject.setPageRangeForFile2(1, 2);

        // Initiating the comparison process
        pdfProject.compare();

        // Displaying a message to indicate that the process is completed
        System.out.println("Comparison done.");

        // Deleting temporary files created during the process
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
