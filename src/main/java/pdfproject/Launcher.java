package pdfproject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Launcher {
    public static List<File> tempFiles = new ArrayList<>();
    public static void main(String[] args) {
        String pdf1 = "E:/Sample/new/1.docx";
        pdf1 = "C:/Users/JAY/Downloads/Documents/the-book-collector-example-2018-04.pdf";
        String pdf2 = "E:/Sample/new/2.docx";
        pdf2 = pdf1;

        PDFProject pdfProject = new PDFProject(pdf1,pdf2);
        pdfProject.compare();


        System.out.println("done");

        for (File f: tempFiles){
            if (f.delete()){
                System.out.println(f.getName() +" Deleted!");
            }
        }
    }
}
