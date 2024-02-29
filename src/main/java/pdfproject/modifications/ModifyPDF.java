package pdfproject.modifications;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.enums.Constants.Operation;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import pdfproject.Config.Colors;
import pdfproject.utils.InfoDocUtil;

import static pdfproject.enums.Constants.FileFormat.TEMP_DIR;

/**
 * Class for modifying PDF documents based on identified differences.
 */
public class ModifyPDF {

    private final File pdf1;
    private final File pdf2;
    private final List<WordInfo> list;
    private final List<File> files = new ArrayList<>();
    private List<List<InfoDocUtil.Info>> masterList;

    private static final String path1 = TEMP_DIR + "old.pdf";
    private static final String path2 = TEMP_DIR + "new.pdf";
    private static final String path3 = TEMP_DIR + "edited.pdf";


    /**
     * Constructor to initialize the ModifyPDF object with two PDF documents and a list of WordInfo differences.
     *
     * @param pdf1 First PDF document.
     * @param pdf2 Second PDF document.
     * @param list List of WordInfo objects representing differences.
     */
    public ModifyPDF(File pdf1, File pdf2, List<WordInfo> list) {
        this.pdf1 = pdf1;
        this.pdf2 = pdf2;
        this.list = list;
    }

    /**
     * Updates PDF documents based on identified differences and creates output files.
     */
    public void updatePDFs() {
        List<WordInfo> listForDoc1 = new ArrayList<>();
        List<WordInfo> listForDoc2 = new ArrayList<>();
        for(WordInfo wordInfo: list){
            List<Operation> typeList = wordInfo.getTypeList();
            if (typeList.size()  == 1 && typeList.get(0) == Operation.DELETED){
                listForDoc1.add(wordInfo);
            }else {
                listForDoc2.add(wordInfo);
            }
        }

        updatePDF(pdf1, listForDoc1,path1);
        updatePDF(pdf2, listForDoc2,path2);

        masterList = InfoDocUtil.setDoc(list, path3);


        files.add(new File(path1));
        files.add(new File(path2));
        files.add(new File(path3));
    }

    private void updatePDF(File pdf, List<WordInfo> list, String path) {
        HashMap<Integer,List<WordInfo>> map = new HashMap<>();
        for (WordInfo wordInfo: list){
            int pageIndex = wordInfo.getPageNumber() - 1;
            if (map.containsKey(pageIndex)){
                map.get(pageIndex).add(wordInfo);
            }else {
                List<WordInfo> l = new ArrayList<>();
                l.add(wordInfo);
                map.put(pageIndex,l);
            }
        }

        PDDocument doc = getDoc(pdf);

        for (int pageIndex: map.keySet()){
            addRect(pageIndex,doc,map.get(pageIndex));
        }

        decrypt(doc);

        try {
            doc.save(path);
            doc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRect(int pageIndex,PDDocument doc, List<WordInfo> list) {
        PDPage page = doc.getPage(pageIndex);
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

            contentStream.setLineWidth(2);
            for (WordInfo wordInfo: list){
                List<TextPosition> textPositions = wordInfo.getPositions();
                TextPosition firstTextPosition = textPositions.get(0);
                TextPosition lastTextPosition = textPositions.get(textPositions.size() - 1);

                Color color = getColor(wordInfo);
                contentStream.setStrokingColor(color);
                float padding = 2f;
                float x = firstTextPosition.getX() - padding;
                float y = page.getMediaBox().getHeight() - lastTextPosition.getY() - padding;
                float width = lastTextPosition.getX() + lastTextPosition.getWidth() - firstTextPosition.getX() + padding * 2;
                float height = lastTextPosition.getHeight() + padding * 2;

                contentStream.addRect(x, y, width, height);
                contentStream.stroke();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Color getColor(WordInfo wordInfo) {
        Color color = null;
        List<Operation> typeList = wordInfo.getTypeList();

        if (typeList.size() == 1) {
            Operation operation = typeList.get(0);

            switch (operation) {
                case ADDED:
                    color = Colors.ADDED_OPERATION_COLOR;
                    break;
                case FONT:
                    color = Colors.FONT_NAME_OPERATION_COLOR;
                    break;
                case SIZE:
                    color = Colors.FONT_SIZE_OPERATION_COLOR;
                    break;
                case STYLE:
                    color = Colors.FONT_STYLE_OPERATION_COLOR;
                    break;
                case DELETED:
                    color = Colors.DELETED_OPERATION_COLOR;
                    break;
                default:
                    color = color;
                    break;
            }
        } else if (typeList.size() > 1) {
            color = Colors.MULTIPLE_OPERATION_COLOR;
        }
        return color;
    }

    /**
     * Gets the list of output files generated during PDF modification.
     *
     * @return List of output files.
     */
    public List<File> getFiles() {
        return files;
    }

    public List<List<InfoDocUtil.Info>> getMasterList() {
        return masterList;
    }

    /**
     * Adds a rectangle highlighting a WordInfo object on a PDF page.
     *
     * @param wordInfo  WordInfo object representing the text to be highlighted.
     * @param document  PDDocument representing the PDF document.
     * @param color     Color of the rectangle.
     */

    /**
     * Decrypts a PDF document if it is encrypted.
     *
     * @param document PDDocument to be decrypted.
     */
    private static void decrypt(PDDocument document) {
        if (document.isEncrypted()) {
            document.setAllSecurityToBeRemoved(true);
        }
    }

    /**
     * Loads a PDF document from a file.
     *
     * @param pdf File representing the PDF document.
     * @return PDDocument object representing the loaded PDF document.
     */
    private PDDocument getDoc(File pdf) {
        try {
            return PDDocument.load(pdf, MemoryUsageSetting.setupTempFileOnly());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}