package pdfproject.models;

import java.util.List;

public class MapModel {
    List<List<String>> validationList;
    List<List<String>> alignmentList;

    public MapModel(List<List<String>> alignmentList, List<List<String>> validationList) {
        this.alignmentList = alignmentList;
        this.validationList = validationList;
    }

    public List<List<String>> getValidationList() {
        return validationList;
    }

    public List<List<String>> getAlignmentList() {
        return alignmentList;
    }
}

