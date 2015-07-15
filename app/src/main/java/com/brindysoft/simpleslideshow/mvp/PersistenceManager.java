package com.brindysoft.simpleslideshow.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PersistenceManager {

    private static final String SLIDESHOW_PERSISTENCE = "SLIDESHOW_PERSISTENCE";
    private static final String PICTURES_KEY = "pictures";
    private static final String WATERMARK_KEY = "watermark";

    @Inject
    private Context context;

    private final Gson gson = new GsonBuilder().create();

    public void savePictures(List<PictureModel> pictures) {
        SharedPreferences preferences = getSharedPreferences();
        preferences.edit().putString(PICTURES_KEY, toJson(pictures)).apply();
    }

    public List<PictureModel> loadPictures() {
        String json = getSharedPreferences().getString("pictures", "{}");
        return gson.fromJson(json, Pictures.class).pictures;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(SLIDESHOW_PERSISTENCE, Activity.MODE_PRIVATE);
    }

    private String toJson(List<PictureModel> list) {
        Pictures pictures = new Pictures();
        pictures.pictures = list;
        return gson.toJson(pictures);
    }

    public void removeWatermark() {
        getSharedPreferences().edit().remove(WATERMARK_KEY).apply();
    }

    public void saveWatermark(String uri) {
        getSharedPreferences().edit().putString(WATERMARK_KEY, uri).apply();
    }

    public String getWatermark() {
        return getSharedPreferences().getString(WATERMARK_KEY, null);
    }

    public static class Pictures {

        private List<PictureModel> pictures = new LinkedList<>();

    }

}
