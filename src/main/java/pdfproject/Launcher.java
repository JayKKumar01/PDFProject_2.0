package pdfproject;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pdfproject.enums.Info;
import pdfproject.models.DataModel;
import pdfproject.utils.InfoDocUtil;
import pdfproject.utils.SheetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static pdfproject.enums.Info.Constants.TEMP_DIR;

/**
 * The main class to launch the PDFProject and demonstrate its functionality.
 */
public class Launcher {

    // List to store temporary files created during the execution of the program
    public static List<File> tempFiles = new ArrayList<>();
    public static boolean isInProgress;
    private static final String excelFilePath = Config.OUTPUT_IMAGES_PATH+"\\report_"+System.currentTimeMillis()+".xlsx";

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
                    updateOutputData(pdfProject.getMasterList(),i+2);
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

    private static void updateOutputData(List<List<InfoDocUtil.Info>> masterList, int rowNumber) {
        // Update with your actual Excel file path
        File excelFile = new File(excelFilePath);
        File tempFile = new File(new File(TEMP_DIR),System.currentTimeMillis()+".tmp");  // Temporary file path

        Workbook workbook = null;
        FileOutputStream outputStream = null;

        try {
            workbook = excelFile.exists() ? new XSSFWorkbook(new FileInputStream(excelFile)) : new XSSFWorkbook();
            outputStream = new FileOutputStream(tempFile);  // Use temporary file

            int sheetIndex = workbook.getNumberOfSheets();
            Sheet sheet = workbook.createSheet("Sheet" + (sheetIndex + 1));

            int rowIndex = findNextAvailableRow(sheet);

            // Iterate through each inner list of masterList (representing each page)
            for (int i = 0; i < masterList.size(); i++) {
                List<InfoDocUtil.Info> pageInfoList = masterList.get(i);

                Row pageRow = sheet.createRow(rowIndex++);
                Cell pageCell = pageRow.createCell(0);
                pageCell.setCellValue("Page " + (i + 1));

                for (InfoDocUtil.Info info : pageInfoList) {
                    Row row = sheet.createRow(rowIndex++);
                    int cellIndex = 1;

                    Cell cellSentence = row.createCell(cellIndex++);
                    cellSentence.setCellValue(info.getSentence());

                    Color color = info.getColor();
                    Cell cellInfo = row.createCell(cellIndex);
                    cellInfo.setCellValue(info.getInfo());
                }
            }

            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            // If everything is successful, replace the original file with the temporary one
            Files.move(tempFile.toPath(), excelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error updating Excel sheet for input line " + (rowNumber) + ": " + e.getMessage());
        }
    }


    private static int findNextAvailableRow(Sheet sheet) {
        int rowIndex = 0;
        while (sheet.getRow(rowIndex) != null) {
            rowIndex++;
        }
        return rowIndex;
    }
}
