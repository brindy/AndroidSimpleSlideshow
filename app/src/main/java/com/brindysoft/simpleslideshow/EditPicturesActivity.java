package com.brindysoft.simpleslideshow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.brindysoft.simpleslideshow.mvp.EditPicturesPresenter;
import com.brindysoft.simpleslideshow.mvp.PictureModel;
import com.makeramen.dragsortadapter.DragSortAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.edit_pictures_activity)
public class EditPicturesActivity extends RoboAppCompatActivity implements EditPicturesPresenter.View {

    @Inject
    private EditPicturesPresenter presenter;

    @InjectView(R.id.edit_pictures_recycler)
    private RecyclerView recyclerView;

    @InjectView(R.id.edit_pictures_unselected_message)
    private TextView unselectedMessage;

    @InjectView(R.id.edit_pictures_form)
    private View form;

    @InjectView(R.id.edit_pictures_form_delay_seeker)
    private SeekBar seeker;

    @InjectView(R.id.edit_pictures_form_image)
    private ImageView image;

    @Override
    protected void postOnCreate(Bundle savedInstance) {
        configureForm();
        configureSeeker();
        configureRecyclerView();
    }

    private void configureSeeker() {
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                presenter.setPictureDelay(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void configureForm() {
        form.setVisibility(View.GONE);
    }

    @Override
    protected void initPresenter() {
        presenter.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_pictures_menu, menu);
        menu.findItem(R.id.edit_pictures_menu_delete_picture).setEnabled(presenter.hasSelectedPicture());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.edit_pictures_menu_add_picture:
                presenter.addPicture();
                return true;

            case R.id.edit_pictures_menu_delete_picture:
                presenter.deletePicture();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void picturesUpdated() {
        invalidateOptionsMenu();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void editSelectedPicture() {
        unselectedMessage.setVisibility(View.GONE);
        form.setVisibility(View.VISIBLE);

        PictureModel model = presenter.getSelectedPicture();

        image.setImageURI(Uri.parse(model.getUri()));
        seeker.setProgress(model.getDelaySeconds() - 1);

        picturesUpdated();
    }

    @Override
    public void noPictures() {
        unselectedMessage.setVisibility(View.VISIBLE);
        form.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode != REQUEST_CODE_SELECT_PICTURE) {
            return;
        }

        Uri selectedImageUri = data.getData();
        presenter.imageSelected(selectedImageUri.toString());
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PicturesAdapter(recyclerView));
    }

    class PicturesAdapter extends DragSortAdapter<PictureViewHolder> {

        public PicturesAdapter(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_pictures_row, null);
            return new PictureViewHolder(this, view);
        }

        @Override
        public void onBindViewHolder(PictureViewHolder holder, int position) {
            PictureModel model = presenter.getPictureAt(position);
            holder.thumbnail.setImageURI(Uri.parse(model.getUri()));
            holder.uri.setText(model.getUri());
            holder.delay.setText(getString(R.string.edit_pictures_delay, model.getDelaySeconds()));
            holder.position = position;

            if (presenter.isSelectedPictureIndex(position)) {
                setElevation(holder.card, getResources().getDisplayMetrics().density * 10);
            } else {
                setElevation(holder.card, 0);
            }

        }

        private void setElevation(CardView cardView, float f) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(f);
            } else {
                cardView.setCardElevation(f);
            }
        }

        @Override
        public int getItemCount() {
            return presenter.picturesCount();
        }

        @Override
        public long getItemId(int position) {
            return presenter.idOfPictureAt(position);
        }

        @Override
        public int getPositionForId(long id) {
            return presenter.positionForPictureWithId(id);
        }

        @Override
        public boolean move(int fromPosition, int toPosition) {
            presenter.movePicture(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onDrop() {
            Log.d(getClass().getSimpleName(), "onDrop");
            super.onDrop();
        }
    }

    class PictureViewHolder extends DragSortAdapter.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final CardView card;
        private final ImageView thumbnail;
        private final TextView uri;
        private final TextView delay;

        private int position;

        public PictureViewHolder(DragSortAdapter<?> dragSortAdapter, View itemView) {
            super(dragSortAdapter, itemView);
            this.card = (CardView)itemView.findViewById(R.id.edit_pictures_row_card);
            this.thumbnail = (ImageView)itemView.findViewById(R.id.edit_pictures_row_image);
            this.uri = (TextView)itemView.findViewById(R.id.edit_pictures_row_uri);
            this.delay = (TextView)itemView.findViewById(R.id.edit_pictures_row_delay);

            configureClickListener(itemView);
            configureLongClickListener(itemView);
        }

        private void configureLongClickListener(View itemView) {
            itemView.setOnLongClickListener(this);
        }

        private void configureClickListener(View itemView) {
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            presenter.pictureSelectedAt(position);
        }

        @Override
        public boolean onLongClick(View view) {
            startDrag();
            return true;
        }
    }

}
