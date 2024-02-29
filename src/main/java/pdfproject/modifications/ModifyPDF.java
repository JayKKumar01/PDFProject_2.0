package pdfproject.modifications;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.Config.Colors;
import pdfproject.enums.Constants.Operation;
import pdfproject.models.WordInfo;
import pdfproject.utils.InfoDocUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String TEMP_PATH = TEMP_DIR + "%s.pdf";
    private static final String path1 = String.format(TEMP_PATH, "old");
    private static final String path2 = String.format(TEMP_PATH, "new");
    private static final String path3 = String.format(TEMP_PATH, "edited");

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

    public List<File> getFiles() {
        return files;
    }

    public List<List<InfoDocUtil.Info>> getMasterList() {
        return masterList;
    }

    /**
     * Updates PDF documents based on identified differences and creates output files.
     */
    public void updatePDFs() {
        List<WordInfo> listForDoc1 = new ArrayList<>();
        List<WordInfo> listForDoc2 = new ArrayList<>();
        separateWordInfoLists(listForDoc1, listForDoc2);

        updatePDF(pdf1, listForDoc1, path1);
        updatePDF(pdf2, listForDoc2, path2);

        masterList = InfoDocUtil.setDoc(list, path3);

        files.add(new File(path1));
        files.add(new File(path2));
        files.add(new File(path3));
    }

    private void separateWordInfoLists(List<WordInfo> listForDoc1, List<WordInfo> listForDoc2) {
        for (WordInfo wordInfo : list) {
            List<Operation> typeList = wordInfo.getTypeList();
            if (typeList.size() == 1 && typeList.get(0) == Operation.DELETED) {
                listForDoc1.add(wordInfo);
            } else {
                listForDoc2.add(wordInfo);
            }
        }
    }

    private void updatePDF(File pdf, List<WordInfo> list, String path) {
        Map<Integer, List<WordInfo>> map = new HashMap<>();
        list.forEach(wordInfo -> map.computeIfAbsent(wordInfo.getPageNumber() - 1, k -> new ArrayList<>()).add(wordInfo));

        PDDocument doc = getDoc(pdf);

        map.forEach((pageIndex, wordInfoList) -> addRect(pageIndex, doc, wordInfoList));

        decrypt(doc);

        saveAndCloseDocument(doc, path);
    }

    private void addRect(int pageIndex, PDDocument doc, List<WordInfo> list) {
        PDPage page = doc.getPage(pageIndex);
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

            contentStream.setLineWidth(2);
            list.forEach(wordInfo -> {
                try {
                    drawRectangle(contentStream, page, wordInfo);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException("Error creating PDPageContentStream: " + e.getMessage(), e);
        }
    }

    private void drawRectangle(PDPageContentStream contentStream, PDPage page, WordInfo wordInfo) throws IOException {
        List<TextPosition> textPositions = wordInfo.getPositions();
        TextPosition firstTextPosition = textPositions.get(0);
        TextPosition lastTextPosition = textPositions.get(textPositions.size() - 1);

        contentStream.setStrokingColor(getColor(wordInfo));
        float padding = 2f;
        float x = firstTextPosition.getX() - padding;
        float y = page.getMediaBox().getHeight() - lastTextPosition.getY() - padding;
        float width = lastTextPosition.getX() + lastTextPosition.getWidth() - firstTextPosition.getX() + padding * 2;
        float height = lastTextPosition.getHeight() + padding * 2;

        contentStream.addRect(x, y, width, height);
        contentStream.stroke();
    }

    private Color getColor(WordInfo wordInfo) {
        List<Operation> typeList = wordInfo.getTypeList();

        if (typeList.size() == 1) {
            Operation operation = typeList.get(0);

            switch (operation) {
                case ADDED:
                    return Colors.ADDED_OPERATION_COLOR;
                case FONT:
                    return Colors.FONT_NAME_OPERATION_COLOR;
                case SIZE:
                    return Colors.FONT_SIZE_OPERATION_COLOR;
                case STYLE:
                    return Colors.FONT_STYLE_OPERATION_COLOR;
                case DELETED:
                    return Colors.DELETED_OPERATION_COLOR;
                default:
                    return Colors.MULTIPLE_OPERATION_COLOR;
            }
        }
        return Colors.MULTIPLE_OPERATION_COLOR;
    }

    private void saveAndCloseDocument(PDDocument doc, String path) {
        try {
            doc.save(path);
            doc.close();
        } catch (IOException e) {
            throw new RuntimeException("Error saving or closing document: " + e.getMessage(), e);
        }
    }

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
