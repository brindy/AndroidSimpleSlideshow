package com.brindysoft.simpleslideshow.mvp;

import java.util.List;

public class SavePicturesEvent {

    private final List<PictureModel> pictures;

    public SavePicturesEvent(List<PictureModel> pictures) {
        this.pictures = pictures;
    }

    public List<PictureModel> getPictures() {
        return pictures;
    }

}
