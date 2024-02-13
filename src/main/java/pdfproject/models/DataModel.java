package pdfproject.models;

public class DataModel {
    String path1;
    String path2;
    String outputFolder;
    int startPage1;
    int endPage1;
    int startPage2;
    int endPage2;
    Integer page;
    boolean isRange1;
    boolean isRange2;

    public DataModel(String path1, String path2, String outputFolder, String range1, String range2, Integer page) {
        this.path1 = path1;
        this.path2 = path2;
        this.outputFolder = outputFolder;
        if (range1 != null){
            isRange1 = true;
            this.startPage1 = getFirst(range1);
            this.endPage1 = getFirst(range1);
        }
        if (range2 != null){
            isRange2 = true;
            this.startPage2 = getFirst(range2);
            this.endPage2 = getLast(range2);
        }
        this.page = page;
    }

    private int getLast(String range) {
        if (!range.contains("-")){
            return Integer.parseInt(range);
        }
        range = range.substring(range.indexOf("-")+1);
        return Integer.parseInt(range);
    }

    private int getFirst(String range) {
        if (!range.contains("-")){
            return Integer.parseInt(range);
        }
        range = range.replace(range.substring(range.indexOf("-")),"");
        return Integer.parseInt(range);
    }

    public String getPath1() {
        return path1;
    }

    public void setPath1(String path1) {
        this.path1 = path1;
    }

    public String getPath2() {
        return path2;
    }

    public void setPath2(String path2) {
        this.path2 = path2;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public int getStartPage1() {
        return startPage1;
    }

    public void setStartPage1(int startPage1) {
        this.startPage1 = startPage1;
    }

    public int getEndPage1() {
        return endPage1;
    }

    public void setEndPage1(int endPage1) {
        this.endPage1 = endPage1;
    }

    public int getStartPage2() {
        return startPage2;
    }

    public void setStartPage2(int startPage2) {
        this.startPage2 = startPage2;
    }

    public int getEndPage2() {
        return endPage2;
    }

    public void setEndPage2(int endPage2) {
        this.endPage2 = endPage2;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public boolean isRange1() {
        return isRange1;
    }

    public void setRange1(boolean range1) {
        isRange1 = range1;
    }

    public boolean isRange2() {
        return isRange2;
    }

    public void setRange2(boolean range2) {
        isRange2 = range2;
    }
}
