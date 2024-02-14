package pdfproject.utils;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import pdfproject.Config;
import pdfproject.Launcher;

import java.io.*;
import java.util.concurrent.*;

public class WordToPdfConverter {
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final int MAX_RETRIES = 2;
    private static final int RETRY_SLEEP_MILLIS = 1000;
    private static final int TIMEOUT_SECONDS = 20;
    private static IConverter converter = null;

    public static File toPDF(String docPath) {
        File inputWord = new File(docPath);

        if (!inputWord.exists()) {
            return null;
        }
        if (!Launcher.isInProgress){
            if(!isAllowed()){
                return null;
            }
        }

        Launcher.isInProgress = true;

        System.out.println("Converting... : " + inputWord.getName());

        DocumentType type = DocumentType.MS_WORD;
        File outputPdf = new File(TEMP_DIR + "/" + System.currentTimeMillis() + ".pdf");


        int remainingAttempts = 2;

        while (remainingAttempts > 0) {
            CompletableFuture<File> conversionFuture = CompletableFuture.supplyAsync(() -> {
                File attemptOutputPdf = new File(TEMP_DIR + "/" + System.currentTimeMillis() + ".pdf");

                for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                    try (InputStream inputStream = new FileInputStream(inputWord);
                         OutputStream outputStream = new FileOutputStream(attemptOutputPdf)) {

                        converter = LocalConverter.builder().build();
                        converter.convert(inputStream).as(type).to(outputStream).as(DocumentType.PDF).execute();
                        Launcher.tempFiles.add(attemptOutputPdf);

                        System.out.println("Converted!");
                        return attemptOutputPdf; // Successful conversion, exit the loop

                    } catch (Exception e) {
                        handleConversionException(inputWord, attempt, e);

                        if (attempt < MAX_RETRIES) {
                            sleepBeforeRetry();
                        }
                    } finally {
                        closeConverter(converter);
                    }
                }

                return null; // If the conversion fails after all retries
            });

            try {
                outputPdf = conversionFuture.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                break;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.out.println("Conversion process timed out: " + e.getMessage());
                remainingAttempts--;
            }finally {
                closeConverter(converter);
            }
        }

        return outputPdf;
    }

    private static boolean isAllowed() {
        if (Config.UserGuide.CHECK_IF_WORD_IS_RUNNING) {
            if (isWordRunning()) {
                System.out.println("Make sure to close MS Word!");
                return false;
            }
        } else {
            if (isWordRunning()) {
                System.out.println("To save your MS Word work data, Can't Proceed as MS Word is still Running");
                return false;
            }
        }
        return true;
    }

    private static void handleConversionException(File inputWord, int attempt, Exception e) {
        System.out.println("Error: " + inputWord.getName());
        System.out.println("Re-attempt: " + attempt);
    }

    private static void sleepBeforeRetry() {
        try {
            TimeUnit.MILLISECONDS.sleep(RETRY_SLEEP_MILLIS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private static void closeConverter(IConverter converter) {
        try {
            if (converter != null) {
                converter.shutDown();
            }
        } catch (Exception ignored) {
            // Log or handle the exception as needed
        }
    }

    private static boolean isWordRunning() {
        try {
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
