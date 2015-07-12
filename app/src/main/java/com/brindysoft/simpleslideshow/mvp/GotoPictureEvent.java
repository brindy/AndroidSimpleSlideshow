package com.brindysoft.simpleslideshow.mvp;

public class GotoPictureEvent {

    private final int index;

    public GotoPictureEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
