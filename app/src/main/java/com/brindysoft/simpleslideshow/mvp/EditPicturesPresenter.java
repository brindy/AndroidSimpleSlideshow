package com.brindysoft.simpleslideshow.mvp;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class EditPicturesPresenter {

    @Inject
    private PersistenceManager persistenceManager;

    private List<PictureModel> pictures = new LinkedList<>();
    private int selectedPictureIndex = -1;
    private View view;

    public void init(View view) {
        this.view = view;
        pictures = persistenceManager.loadPictures();

        if (pictures.isEmpty()) {
            view.noPictures();
        } else {
            selectedPictureIndex = 0;
            view.picturesUpdated();
            view.editSelectedPicture();
        }
    }

    public void addPicture() {
        view.selectPicture();
    }

    public void imageSelected(String uri) {
        pictures.add(new PictureModel(uri, 5));
        save();

        view.picturesUpdated();
        selectedPictureIndex = pictures.size() - 1;
        view.editSelectedPicture();
    }

    private void save() {
        persistenceManager.savePictures(pictures);
    }

    public int picturesCount() {
        return pictures.size();
    }

    public PictureModel getPictureAt(int position) {
        return pictures.get(position);
    }

    public void pictureSelectedAt(int position) {
        selectedPictureIndex = position;
        view.editSelectedPicture();
    }

    public void setPictureDelay(int delaySeconds) {
        pictures.get(selectedPictureIndex).setDelaySeconds(delaySeconds);
        save();
        view.picturesUpdated();
    }

    public void deletePicture() {
        pictures.remove(selectedPictureIndex);
        save();

        selectedPictureIndex--;
        if (selectedPictureIndex < 0 && !pictures.isEmpty()) {
            selectedPictureIndex = 0;
        }

        view.picturesUpdated();

        if (hasSelectedPicture()) {
            view.editSelectedPicture();
        } else {
            view.noPictures();
        }
    }

    public boolean hasSelectedPicture() {
        return selectedPictureIndex >= 0;
    }

    public boolean isSelectedPictureIndex(int position) {
        return selectedPictureIndex == position;
    }

    public PictureModel getSelectedPicture() {
        return getPictureAt(selectedPictureIndex);
    }

    public interface View {

        void selectPicture();

        void picturesUpdated();

        void editSelectedPicture();

        void noPictures();
    }

}
