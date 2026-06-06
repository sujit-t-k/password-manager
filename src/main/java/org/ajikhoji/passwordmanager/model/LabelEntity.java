package org.ajikhoji.passwordmanager.model;

public class LabelEntity {

    public static final long UNDEFINED_LABEL_ID = -5;
    public static final String DEFAULT_LABEL_NAME = "Unlabeled";
    public static final LabelEntity NULL_LABEL = new LabelEntity(UNDEFINED_LABEL_ID, "None");
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

    @Override
    public String toString() {
        return String.format("LabelEntity (label id = %d, label name = %s)", labelId, labelName);
    }

}
