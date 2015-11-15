package com.kumar.sunish.mygallery;

/**
 * Created by sunish on 20/8/15.
 */
public class AppData {

    private String url;
    private String allImgIds;

    private boolean serverRunning = false;

    private static AppData ourInstance = new AppData();

    public static AppData getInstance() {
        return ourInstance;
    }

    private AppData() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAllImgIds() {
        return allImgIds;
    }

    public void setAllImgIds(String allImgIds) {
        this.allImgIds = allImgIds;
    }

    public boolean isServerRunning() {
        return serverRunning;
    }

    public void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }
}
