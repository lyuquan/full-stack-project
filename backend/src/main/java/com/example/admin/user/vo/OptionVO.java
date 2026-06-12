package com.example.admin.user.vo;

/**
 * Common option returned to frontend select boxes.
 *
 * value is the real value submitted to the backend.
 * label is the text displayed on the page.
 */
public class OptionVO {

    /**
     * Actual role value used by DTO validation and database records.
     */
    private String value;

    /**
     * Display text shown in frontend select options.
     */
    private String label;

    public OptionVO() {
    }

    public OptionVO(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
