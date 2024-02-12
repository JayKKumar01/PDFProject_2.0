package pdfproject;

import pdfproject.core.StringDiff;
import pdfproject.enums.Info;
import pdfproject.enums.Info.Constants;
import pdfproject.imageutils.PDFToImageConverter;
import pdfproject.models.WordInfo;
import pdfproject.modifications.ModifyPDF;
import pdfproject.utils.Base;
import pdfproject.utils.PDFUtil;
import pdfproject.utils.WordToPdfConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PDFProject {
    private String outputPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "PDFProject";
    private File pdf1;
    private File pdf2;
    List<Integer> pagesPDF1 = new ArrayList<>();
    List<Integer> pagesPDF2 = new ArrayList<>();

    public PDFProject(String pdf1, String pdf2) {
        this.pdf1 = getFile(pdf1);
        this.pdf2 = getFile(pdf2);
        createOutputFolder();
    }

    public void compare(){
        if (!isValid()){
            return;
        }
        List<WordInfo> list1 = PDFUtil.WordList(pdf1,new ArrayList<>());
        List<WordInfo> list2 = PDFUtil.WordList(pdf2,new ArrayList<>());
        List<WordInfo> list = StringDiff.List(list1,list2);


        Iterator<WordInfo> itr = list.iterator();
        while (itr.hasNext()){
            WordInfo wordInfo = itr.next();
            List<Info.Operation> typeList = wordInfo.getTypeList();
            if (typeList.get(0) == Info.Operation.EQUAL){
                itr.remove();
                continue;
            }
            System.out.println(wordInfo.getWord()+": "+wordInfo.getInfo());
        }
        System.out.println(list1.size()+" "+list2.size()+": "+list.size());

        ModifyPDF modifyPDF = new ModifyPDF(pdf1,pdf2,list);
        modifyPDF.updatePDFs();
        List<File> files = modifyPDF.getFiles();
        File file1 = files.get(0);
        File file2 = files.get(1);
        File file3 = files.get(2);
        Launcher.tempFiles.add(file1);
        Launcher.tempFiles.add(file2);
        Launcher.tempFiles.add(file3);

        try {
            PDFToImageConverter.createImage(file1, file2,file3,outputPath,pagesPDF1,pagesPDF2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private boolean isValid() {
        return true;
    }


    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        createOutputFolder();
    }

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

    private File getFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + path);
        }

        String name = file.getName().toLowerCase();
        if (name.endsWith(Constants.PDF)) {
            return file;
        } else if (name.endsWith(Constants.DOCX) || name.endsWith(Constants.DOC)) {
            File pdfFile = WordToPdfConverter.toPDF(path);
            if (pdfFile == null) {
                throw new RuntimeException("Failed to convert Word document to PDF: " + path);
            }
            return pdfFile;
        }
        throw new IllegalArgumentException("Unsupported file format: " + path);
    }

    public void setPageForFile1(int page){
        if (!pagesPDF1.isEmpty()){
            pagesPDF1.clear();
        }
        pagesPDF1.add(page);
    }
    public void setPageForFile2(int page){
        if (!pagesPDF2.isEmpty()){
            pagesPDF2.clear();
        }
        pagesPDF2.add(page);
    }




    public void setPageRangeForFile1(int startPage, int endPage) {
        if (!pagesPDF1.isEmpty()){
            pagesPDF1.clear();
        }
        for (int i=startPage; i<=endPage; i++){
            pagesPDF1.add(i);
        }
    }
    public void setPageRangeForFile2(int startPage, int endPage) {
        if (!pagesPDF2.isEmpty()){
            pagesPDF2.clear();
        }
        for (int i=startPage; i<=endPage; i++){
            pagesPDF2.add(i);
        }
    }
}
