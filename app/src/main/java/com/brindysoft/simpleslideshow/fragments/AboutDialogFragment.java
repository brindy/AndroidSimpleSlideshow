package com.brindysoft.simpleslideshow.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.brindysoft.simpleslideshow.R;

public class AboutDialogFragment extends DialogFragment {

    public static AboutDialogFragment newInstance() {
        return new AboutDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_dialog, container, false);
        String title = getString(R.string.about_dialog_title, getVersionName(), getVersionCode());
        TextView.class.cast(view.findViewById(R.id.about_dialog_title)).setText(title);
        Button.class.cast(view.findViewById(R.id.about_dialog_button)).setOnClickListener(createOnClickListener());
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    private View.OnClickListener createOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };
    }

    private int getVersionCode() {
        return getPackageInfo().versionCode;
    }

    private String getVersionName() {
        return getPackageInfo().versionName;
    }

    private PackageInfo getPackageInfo() {
        try {
            return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
