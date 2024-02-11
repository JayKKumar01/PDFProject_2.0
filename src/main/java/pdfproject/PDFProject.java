package pdfproject;

import pdfproject.core.StringDiff;
import pdfproject.enums.Info;
import pdfproject.enums.Info.Constants;
import pdfproject.models.WordInfo;
import pdfproject.utils.PDFUtil;
import pdfproject.utils.WordToPdfConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PDFProject {
    private String outputPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "PDFProject";
    private File pdf1;
    private File pdf2;

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
        List<WordInfo> list = new ArrayList<>();
//        StringDiff.CustomList l = StringDiff.getList(list1, list2);
//        List<WordInfo> lDel = l.getResultDel();
//        List<WordInfo> lAdd = l.getResultAdd();
//        List<WordInfo> lEql = l.getResultEql();
//        list.addAll(lEql);
//        list.addAll(lDel);
//        list.addAll(lAdd);
//        System.out.println(lEql.size() + " "+lDel.size()+" "+lAdd.size());
//        for (WordInfo wordInfo: list){
//            Info.Operation type = wordInfo.getType();
//            if (type == Info.Operation.EQUAL){
//                continue;
//            }
//            System.out.println(wordInfo.getWord()+": "+type);
//            //System.out.println(wordInfo.getWord()+": "+wordInfo.getFont()+" - "+wordInfo.getFontSize() +" - "+wordInfo.getFontStyle());
//        }

        int count = 0;
        copyList(list1,count);
        copyList(list2,count);
        list = StringDiff.List(list1,list2);






        int i=0;
        for (WordInfo wordInfo: list){
            Info.Operation type = wordInfo.getType();
            if (type == Info.Operation.EQUAL){
//                System.out.println(++i);
                continue;
            }
            System.out.println(wordInfo.getWord()+": "+type);
            //System.out.println(wordInfo.getWord()+": "+wordInfo.getFont()+" - "+wordInfo.getFontSize() +" - "+wordInfo.getFontStyle());
        }
        System.out.println(list1.size()+" "+list2.size()+": "+list.size());

        // store list of wordInfo from pdf1
        // store list of wordInfo from pdf2

        //but using a different class using pdfbox library but take the list page by page only to optimize for
        //large pdf files

    }

    private void copyList(List<WordInfo> list, int count) {
        List<WordInfo> listAdd = new ArrayList<>();
        for (int i=0; i<count; i++){
            listAdd.addAll(list);
        }
        list.addAll(listAdd);
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
}
