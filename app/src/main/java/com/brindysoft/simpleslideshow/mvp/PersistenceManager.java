package com.brindysoft.simpleslideshow.mvp;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

@Singleton
public class PersistenceManager {

    private List<PictureModel> pictures = new LinkedList<>();

    public void savePictures(List<PictureModel> pictures) {
        this.pictures = pictures;
    }

    public List<PictureModel> loadPictures() {
        return pictures;
    }

}
