package com.brindysoft.simpleslideshow.mvp;

public class PictureModel {

    private int delaySeconds;
    private String uri;

    public PictureModel(String uri, int delaySeconds) {
        this.uri = uri;
        this.delaySeconds = delaySeconds;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public String getUri() {
        return uri;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

}
