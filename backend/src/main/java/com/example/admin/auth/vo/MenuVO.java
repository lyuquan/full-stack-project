package com.example.admin.auth.vo;

/**
 * Menu data returned to the frontend.
 *
 * The backend decides which menu entries the current user can see, and the
 * frontend only renders this simple structure.
 */
public class MenuVO {

    /**
     * Stable menu key used by the frontend v-for key.
     */
    private String key;

    /**
     * Text displayed in the sidebar.
     */
    private String label;

    /**
     * Whether this menu is the current active entry.
     */
    private Boolean active;

    public MenuVO() {
    }

    public MenuVO(String key, String label, Boolean active) {
        this.key = key;
        this.label = label;
        this.active = active;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
