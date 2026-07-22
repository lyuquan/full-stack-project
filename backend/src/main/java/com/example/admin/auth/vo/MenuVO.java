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
     * Frontend page path represented by this menu.
     */
    private String path;

    /**
     * Whether this menu is the current active entry.
     */
    private Boolean active;

    public MenuVO() {
    }

    public MenuVO(String key, String label, String path, Boolean active) {
        this.key = key;
        this.label = label;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
