package org.ajikhoji.passwordmanager.model;

public class LabelEntity {

    public static final long UNDEFINED_LABEL_ID = -5;
    private long labelId = UNDEFINED_LABEL_ID;
    private String labelName;

    public LabelEntity(String labelName) {
        this.labelName = labelName;
    }

    public LabelEntity(long labelId, String labelName) {
        this.labelId = labelId;
        this.labelName = labelName;
    }

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

}
