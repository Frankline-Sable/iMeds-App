package com.fsdev.imeds;

/**
 * Created by Frankline Sable on 21/10/2017.
 */

public class Model_Menu {
    private String menuTitle, menuDesc;
    private int menuIcon, menuCount, menuBackground;

    public Model_Menu(String menuTitle, String menuDesc, int menuIcon, int menuCount, int menuBackground) {
        this.menuTitle = menuTitle;
        this.menuDesc = menuDesc;
        this.menuIcon = menuIcon;
        this.menuCount = menuCount;
        this.menuBackground = menuBackground;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public String getMenuDesc() {
        return menuDesc;
    }

    public void setMenuDesc(String menuDesc) {
        this.menuDesc = menuDesc;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public int getMenuCount() {
        return menuCount;
    }

    public void setMenuCount(int menuCount) {
        this.menuCount = menuCount;
    }

    public int getMenuBackground() {
        return menuBackground;
    }

    public void setMenuBackground(int menuBackground) {
        this.menuBackground = menuBackground;
    }
}