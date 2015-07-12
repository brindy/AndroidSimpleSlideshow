package com.brindysoft.simpleslideshow.mvp;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import roboguice.event.EventManager;
import roboguice.event.EventThread;
import roboguice.event.Observes;

public class SlideshowPresenter {

    @Inject
    private PersistenceManager persistenceManager;

    @Inject
    private EventManager eventManager;

    private Timer timer;
    private View view;
    private boolean fullScreen;
    private List<PictureModel> pictures;
    private int index;

    public void init(View view) {
        this.view = view;

        timer = new Timer();
        pictures = persistenceManager.loadPictures();

        if (pictures.isEmpty()) {
            view.noPictures();
        } else {
            this.fullScreen = true;
            view.showPictures();
            view.goFullScreen(TimeUnit.SECONDS.toMillis(3));
            view.picturesUpdated();
            gotoPicture(0);
        }

    }

    public void toggleFullScreen() {

        if (pictures.isEmpty()) {
            view.leaveFullScreen();
            fullScreen = false;
            return;
        }

        if (fullScreen) {
            view.leaveFullScreen();
            fullScreen = false;
        } else {
            view.goFullScreen(0);
            fullScreen = true;
        }

    }

    public void editPictures() {
        timer.cancel();
        view.gotoEditPictures();
    }

    public void selectWatermark() {
        view.selectWatermark();
    }

    public void watermarkSelected(String uri) {
        view.showWatermark(uri);
    }

    private void gotoPicture(int index) {
        eventManager.fire(new GotoPictureEvent(index));

        Log.d(getClass().getSimpleName(), "scheduling for " + pictures.get(index).getDelaySeconds() + " seconds");

        timer.schedule(createGotoNextPictureTask(),
                TimeUnit.SECONDS.toMillis(pictures.get(index).getDelaySeconds()));
    }

    private void gotoNextPicture() {
        Log.d(getClass().getSimpleName(), "going to picture next picture " + index);
        index++;
        if (index >= pictures.size()) {
            index = 0;
        }
        gotoPicture(index);
    }

    public int picturesCount() {
        return pictures == null ? 0 : pictures.size();
    }

    public PictureModel getPictureAt(int position) {
        return pictures.get(position);
    }

    private TimerTask createGotoNextPictureTask() {
        return new TimerTask() {
            @Override
            public void run() {
                gotoNextPicture();
            }
        };
    }

    public interface View {

        void goFullScreen(long delay);

        void leaveFullScreen();

        void picturesUpdated();

        void observe(GotoPictureEvent event);

        void gotoEditPictures();

        void selectWatermark();

        void noPictures();

        void showPictures();

        void showWatermark(String uri);

    }

}
