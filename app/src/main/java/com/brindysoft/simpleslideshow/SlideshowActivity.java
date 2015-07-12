package com.brindysoft.simpleslideshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.brindysoft.simpleslideshow.fragments.ImageFragment;
import com.brindysoft.simpleslideshow.mvp.GotoPictureEvent;
import com.brindysoft.simpleslideshow.mvp.SlideshowPresenter;

import javax.inject.Inject;

import roboguice.event.EventThread;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.slideshow_activity)
public class SlideshowActivity extends RoboAppCompatActivity implements SlideshowPresenter.View {

    private static final int REQUEST_CODE_SELECT_PICTURE = 1;

    @Inject
    private SlideshowPresenter presenter;

    @InjectView(android.R.id.content)
    private View content;

    @InjectView(R.id.slideshow_view_pager)
    private ViewPager viewPager;

    @InjectView(R.id.slideshow_watermark_image)
    private ImageView watermark;

    @InjectView(R.id.slideshow_no_pictures_message)
    private View noPicturesMessage;

    @Override
    protected void postOnCreate(Bundle savedInstance) {
        configureViewPager();
    }

    @Override
    protected void initPresenter() {
        presenter.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.slideshow_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.slideshow_menu_edit_pictures:
                presenter.editPictures();
                return true;

            case R.id.slideshow_menu_choose_watermark:
                presenter.selectWatermark();
                return true;

            case R.id.slideshow_menu_clear_watermark:
                presenter.watermarkCleared();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goFullScreen(long delay) {
        content.postDelayed(new Runnable() {
            @Override
            public void run() {
                goFullScreen();
            }
        }, delay);
    }

    @Override
    public void leaveFullScreen() {
        content.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void picturesUpdated() {
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void observe(@Observes(EventThread.UI) GotoPictureEvent event) {
        viewPager.setCurrentItem(event.getIndex(), true);
    }

    @Override
    public void gotoEditPictures() {
        startActivity(new Intent(this, EditPicturesActivity.class));
    }

    @Override
    public void selectWatermark() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_SELECT_PICTURE);
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
        String selectedImagePath = getPath(selectedImageUri);
        presenter.watermarkSelected(selectedImagePath);
    }

    @Override
    public void noPictures() {
        viewPager.setVisibility(View.GONE);
        noPicturesMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPictures() {
        viewPager.setVisibility(View.VISIBLE);
        noPicturesMessage.setVisibility(View.GONE);
    }

    @Override
    public void showWatermark(String uri) {
        watermark.setImageURI(Uri.parse(uri));
    }

    @Override
    public void clearWatermark() {
        watermark.setImageURI(null);
    }

    public void onClick(View view) {
        presenter.toggleFullScreen();
    }

    private void configureViewPager() {
        viewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                presenter.pictureSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void goFullScreen() {
        content.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LOW_PROFILE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    class ImagePagerAdapter extends FragmentPagerAdapter {

        public ImagePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return presenter.picturesCount();
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putString(ImageFragment.ARG_URI, presenter.getPictureAt(position).getUri());

            Fragment fragment = new ImageFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }


}
