package com.brindysoft.simpleslideshow.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brindysoft.simpleslideshow.R;

public class ImageFragment extends Fragment {

    public static final String ARG_URI = "arg_uri";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String uri = getArguments().getString(ARG_URI);
        ImageView imageView = (ImageView)inflater.inflate(R.layout.slideshow_image, null);
        imageView.setImageURI(Uri.parse(uri));
        return imageView;
    }

}
