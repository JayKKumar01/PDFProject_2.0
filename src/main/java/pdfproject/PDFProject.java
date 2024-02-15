package pdfproject;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import pdfproject.core.StringDiff;
import pdfproject.enums.Constants;
import pdfproject.enums.Constants.FileFormat;
import pdfproject.imageutils.PDFToImageConverter;
import pdfproject.models.WordInfo;
import pdfproject.modifications.ModifyPDF;
import pdfproject.utils.InfoDocUtil;
import pdfproject.utils.PDFUtil;
import pdfproject.utils.WordToPdfConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The main class responsible for handling PDF comparison and modification.
 */
public class PDFProject {

    // Default output path for modified PDFs
    private String outputPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "PDFProject";

    // Files to be compared
    private final File pdf1;
    private final File pdf2;

    // Lists to store selected pages for each PDF
    private final List<Integer> pagesPDF1 = new ArrayList<>();
    private final List<Integer> pagesPDF2 = new ArrayList<>();
    private List<List<InfoDocUtil.Info>> masterList;

    /**
     * Constructor to initialize PDFProject with two PDF file paths.
     *
     * @param pdf1 Path to the first PDF file.
     * @param pdf2 Path to the second PDF file.
     */
    public PDFProject(String pdf1, String pdf2) {
//        this.pdf1 = getFile(pdf1);
        this.pdf2 = getFile(pdf2);
        this.pdf1 = null;
        createOutputFolder();
    }

    /**
     * Initiates the comparison process, identifies differences, and applies modifications.
     */
    public boolean compare() {
//        if (!isValid()) {
//            System.out.println("Input Data is not Valid!");
//            return false;
//        }

        List<WordInfo> list3 = PDFUtil.WordList(pdf2, pagesPDF2);
        System.out.println(list3.size());

        boolean x = true;
        if (x){
            return false;
        }
        // Extracting WordInfo lists for each PDF and finding differences
        List<WordInfo> list1 = PDFUtil.WordList(pdf1, pagesPDF1);
        List<WordInfo> list2 = PDFUtil.WordList(pdf2, pagesPDF2);

        List<WordInfo> list = StringDiff.List(list1, list2);

        // Iterating through the differences and printing relevant information
        Iterator<WordInfo> itr = list.iterator();
        while (itr.hasNext()) {
            WordInfo wordInfo = itr.next();
            List<Constants.Operation> typeList = wordInfo.getTypeList();
            if (typeList.get(0) == Constants.Operation.EQUAL) {
                itr.remove();
                continue;
            }
            //System.out.println(wordInfo.getWord() + ": " + wordInfo.getInfo());
        }
        //System.out.println(list1.size() + " " + list2.size() + ": " + list.size());

        // Modifying the PDFs based on identified differences
        ModifyPDF modifyPDF = new ModifyPDF(pdf1, pdf2, list);
        modifyPDF.updatePDFs();
        masterList = modifyPDF.getMasterList();

        // Obtaining modified files and adding them to the temporary files list
        List<File> files = modifyPDF.getFiles();
        File file1 = files.get(0);
        File file2 = files.get(1);
        File file3 = files.get(2);
        Launcher.tempFiles.add(file1);
        Launcher.tempFiles.add(file2);
        Launcher.tempFiles.add(file3);

        try {
            // Converting modified PDFs to images for visual comparison
            PDFToImageConverter.createImage(file1, file2, file3, outputPath, pagesPDF1, pagesPDF2);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided PDF files are valid for comparison.
     *
     * @return True if valid, false otherwise.
     */
    private boolean isValid() {
        if (pdf1 == null || pdf2 == null){
            return false;
        }

        int pageLenPdf1 = getPagesCount(pdf1);
        int pageLenPdf2 = getPagesCount(pdf2);
        for (int i: pagesPDF1){
            if (i < 1 || i > pageLenPdf1){
                return false;
            }
        }
        for (int i: pagesPDF2){
            if (i < 1 || i > pageLenPdf2){
                return false;
            }
        }
        // Additional validation logic can be added here if needed
        return true;
    }

    private int getPagesCount(File pdf) {
        try {
            PDDocument doc = PDDocument.load(pdf, MemoryUsageSetting.setupTempFileOnly());
            int page = doc.getNumberOfPages();
            doc.close();
            return page;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the output path for the modified PDFs and creates the output folder if it does not exist.
     *
     * @param outputPath New output path.
     */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        createOutputFolder();
    }

    public List<List<InfoDocUtil.Info>> getMasterList() {
        return masterList;
    }

    /**
     * Creates the output folder if it does not exist.
     */
    private void createOutputFolder() {
        File outputFolder = new File(outputPath);
        if (!outputFolder.exists()) {
            if (outputFolder.mkdirs()) {
                System.out.println("Output folder created: " + outputPath);
            } else {
                throw new RuntimeException("Failed to create output folder: " + outputPath);
            }
        }
    }

    /**
     * Converts the given file path to a File object, handling Word document conversion if needed.
     *
     * @param path Path to the file.
     * @return File object representing the file.
     */
    private File getFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + path);
        }

        String name = file.getName().toLowerCase();
        if (name.endsWith(FileFormat.PDF)) {
            return file;
        } else if (name.endsWith(FileFormat.DOCX) || name.endsWith(Constants.FileFormat.DOC)) {
            File pdfFile = WordToPdfConverter.toPDF(path);
            if (pdfFile == null) {
                throw new RuntimeException("Failed to convert Word document to PDF: " + path);
            }
            return pdfFile;
        }
        throw new IllegalArgumentException("Unsupported file format: " + path);
    }

    /**
     * Sets a specific page for the first PDF file.
     *
     * @param page Page number to set.
     */
    public void setPageForFile1(int page) {
        if (!pagesPDF1.isEmpty()) {
            pagesPDF1.clear();
        }
        pagesPDF1.add(page);
    }

    /**
     * Sets a specific page for the second PDF file.
     *
     * @param page Page number to set.
     */
    public void setPageForFile2(int page) {
        if (!pagesPDF2.isEmpty()) {
            pagesPDF2.clear();
        }
        pagesPDF2.add(page);
    }

    /**
     * Sets a range of pages for the first PDF file.
     *
     * @param startPage Starting page number.
     * @param endPage   Ending page number.
     */
    public void setPageRangeForFile1(int startPage, int endPage) {
        if (!pagesPDF1.isEmpty()) {
            pagesPDF1.clear();
        }
        for (int i = startPage; i <= endPage; i++) {
            pagesPDF1.add(i);
        }
    }

    /**
     * Sets a range of pages for the second PDF file.
     *
     * @param startPage Starting page number.
     * @param endPage   Ending page number.
     */
    public void setPageRangeForFile2(int startPage, int endPage) {
        if (!pagesPDF2.isEmpty()) {
            pagesPDF2.clear();
        }
        for (int i = startPage; i <= endPage; i++) {
            pagesPDF2.add(i);
        }
    }
}
