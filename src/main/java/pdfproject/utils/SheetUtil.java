package pdfproject.utils;

import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.PaneType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pdfproject.Config;
import pdfproject.models.DataModel;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class SheetUtil {
    private static final DataFormatter formatter = new DataFormatter();
    private static final Pattern pattern = Pattern.compile("\\d+-\\d+");
    private static final Pattern pattern1 = Pattern.compile("\\d+");

    public static List<DataModel> getData(){
        List<DataModel> list = new ArrayList<>();
        Iterator<Row> itr = getRowIterator(Config.INPUT_PATH);
        if (itr == null){
            return null;
        }
        if (itr.hasNext()){
            itr.next();
        }
        while (itr.hasNext()){
            Row row = itr.next();

            Cell path1 = row.getCell(0);
            Cell path2 = row.getCell(1);
            Cell folder = row.getCell(2);
            Cell range1 = row.getCell(3);
            Cell range2 = row.getCell(4);

            if (path1 == null || path2 == null || folder == null){
                continue;
            }
            String strPath1 = formatVal(path1);
            String strPath2 = formatVal(path2);
            String strFolder = formatVal(folder);

            if (strPath1 == null || strPath2 == null || strFolder == null){
                continue;
            }

            if (!isValidRangeFormat(range1) || !isValidRangeFormat(range2)){
                System.out.println(strFolder+": Wrong Range Pattern!");
                continue;
            }


            DataModel dataModel = new DataModel(strPath1,strPath2,strFolder,
                    formatVal(range1),formatVal(range2),null);
            list.add(dataModel);



        }
        return list;

    }

    private static String formatVal(Cell val) {
        if (val == null || formatter.formatCellValue(val).trim().isEmpty()){
            return null;
        }
        return formatter.formatCellValue(val).trim();
    }

    private static boolean isValidRangeFormat(Cell range) {
        if (range == null || formatVal(range) == null){
            return true;
        }
        String val = formatVal(range);
        if (!pattern.matcher(val).matches()){
            if (pattern1.matcher(val).matches()){
                return true;
            }
            return false;
        }
        String[] pages = val.split("-");
        if (pages.length != 2){
            return false;
        }
        return Integer.parseInt(pages[0]) <= Integer.parseInt(pages[1]);
    }

    private static Iterator<Row> getRowIterator(String path){
        FileInputStream fis;
        XSSFWorkbook wb = null;
        try {
            fis = new FileInputStream(path);
            wb = new XSSFWorkbook(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wb == null){
            return null;
        }
        return wb.getSheetAt(0).rowIterator();
    }
}
