package org.ajikhoji.passwordmanager.dto;

public class LabelUsage {

    private final long labelId;
    private final String labelName;
    private final int usageCount;

    public LabelUsage(final long id, final String name, final int usage) {
        labelId = id;
        labelName = name;
        usageCount = usage;
    }

    public long getLabelId() {
        return labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public int getUsageCount() {
        return usageCount;
    }

    @Override
    public boolean equals(final Object other) {
        if(other instanceof LabelUsage l) {
            return l.usageCount == usageCount && l.labelId == labelId &&
                ((l.labelName == null && labelName == null) || (l.labelName != null && l.labelName.equals(labelName)));
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("LabelUsage(id = %d, name = %s, usage = %d)", labelId, labelName, usageCount);
    }

}
