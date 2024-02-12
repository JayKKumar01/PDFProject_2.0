package pdfproject.utils;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import pdfproject.Launcher;

import java.io.*;

public class WordToPdfConverter {
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    public static File toPDF(String docPath) {
        File inputWord = new File(docPath);
        if (!inputWord.exists()) {
            return null;
        }

        System.out.println("Converting... : " + inputWord.getName());

        DocumentType type = DocumentType.MS_WORD;
        File outputPdf = new File(TEMP_DIR + "/" + System.currentTimeMillis() + ".pdf");

        try (InputStream inputStream = new FileInputStream(inputWord);
             OutputStream outputStream = new FileOutputStream(outputPdf)) {

            IConverter converter = LocalConverter.builder().build();
            converter.convert(inputStream).as(type).to(outputStream).as(DocumentType.PDF).execute();
            converter.shutDown();

            Launcher.tempFiles.add(outputPdf);

            System.out.println("Converted!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputPdf;
    }

    private static boolean isWordRunning() {
        try {
            // Run 'tasklist' command and check if "WINWORD.EXE" is in the output
            Process process = Runtime.getRuntime().exec("tasklist");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("WINWORD.EXE")) {
                        return true;
                    }
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
