package pdfproject.modifications;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.TextPosition;
import pdfproject.Launcher;
import pdfproject.enums.Info.Operation;
import pdfproject.imageutils.PDFToImageConverter;
import pdfproject.models.WordInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pdfproject.Config.Colors;
import pdfproject.utils.InfoDocUtil;

import static pdfproject.enums.Info.Constants.TEMP_DIR;

/**
 * Class for modifying PDF documents based on identified differences.
 */
public class ModifyPDF {

    private PDDocument doc1;
    private PDDocument doc2;
    private List<WordInfo> list;
    private List<File> files = new ArrayList<>();
    private List<List<InfoDocUtil.Info>> masterList;


    /**
     * Constructor to initialize the ModifyPDF object with two PDF documents and a list of WordInfo differences.
     *
     * @param pdf1 First PDF document.
     * @param pdf2 Second PDF document.
     * @param list List of WordInfo objects representing differences.
     */
    public ModifyPDF(File pdf1, File pdf2, List<WordInfo> list) {
        this.doc1 = getDoc(pdf1);
        this.doc2 = getDoc(pdf2);
        this.list = list;
    }

    /**
     * Updates PDF documents based on identified differences and creates output files.
     */
    public void updatePDFs() {
        for (WordInfo wordInfo : list) {
            List<Operation> typeList = wordInfo.getTypeList();

            if (typeList.size() == 1) {
                if (typeList.get(0) == Operation.ADDED) {
                    addRect(wordInfo, doc2, Colors.ADDED_OPERATION_COLOR);
                } else if (typeList.get(0) == Operation.FONT) {
                    addRect(wordInfo, doc2, Colors.FONT_NAME_OPERATION_COLOR);
                } else if (typeList.get(0) == Operation.SIZE) {
                    addRect(wordInfo, doc2, Colors.FONT_SIZE_OPERATION_COLOR);
                } else if (typeList.get(0) == Operation.STYLE) {
                    addRect(wordInfo, doc2, Colors.FONT_STYLE_OPERATION_COLOR);
                } else if (typeList.get(0) == Operation.DELETED) {
                    addRect(wordInfo, doc1, Colors.DELETED_OPERATION_COLOR);
                }
            } else if (typeList.size() > 1) {
                addRect(wordInfo, doc2, Colors.MULTIPLE_OPERATION_COLOR);
            }
        }

        String path1 = TEMP_DIR + "/old.pdf";
        String path2 = TEMP_DIR + "/new.pdf";
        String path3 = TEMP_DIR + "/edited.pdf";
        masterList = InfoDocUtil.setDoc(list, path3);
        decrypt(doc1);
        decrypt(doc2);

        try {
            doc1.save(path1);
            doc2.save(path2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                doc1.close();
                doc2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        files.add(new File(path1));
        files.add(new File(path2));
        files.add(new File(path3));
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
    private static void addRect(WordInfo wordInfo, PDDocument document, Color color) {
        List<TextPosition> textPositions = wordInfo.getPositions();
        TextPosition firstTextPosition = textPositions.get(0);
        TextPosition lastTextPosition = textPositions.get(textPositions.size() - 1);
        int pageIndex = wordInfo.getPageNumber() - 1;
        PDPage page = document.getPage(pageIndex);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.setLineWidth(2);
            contentStream.setStrokingColor(color);

            float padding = 2f;
            float x = firstTextPosition.getX() - padding;
            float y = page.getMediaBox().getHeight() - lastTextPosition.getY() - padding;
            float width = lastTextPosition.getX() + lastTextPosition.getWidth() - firstTextPosition.getX() + padding * 2;
            float height = lastTextPosition.getHeight() + padding * 2;

            contentStream.addRect(x, y, width, height);
            contentStream.stroke();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
