package com.app.flamingo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import com.app.flamingo.R;
import com.app.flamingo.activity.AdminDashboardActivity;
import com.app.flamingo.activity.OnBoardingActivity;
import com.app.flamingo.activity.UserDashboardActivity;

public class PrivacyPolicySettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_privacy_policy, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setToolBar(view);
        return view;
    }

    private void setToolBar(View view) {

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            if (getActivity() instanceof OnBoardingActivity) {
                ((OnBoardingActivity) getActivity()).setSupportActionBar(toolbar);
                Objects.requireNonNull(((OnBoardingActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
                Objects.requireNonNull(((OnBoardingActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OnBoardingActivity) Objects.requireNonNull(getActivity())).onBackPressed();
                    }
                });
            } else if (getActivity() instanceof AdminDashboardActivity) {
                ((AdminDashboardActivity) getActivity()).setSupportActionBar(toolbar);
                Objects.requireNonNull(((AdminDashboardActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
                Objects.requireNonNull(((AdminDashboardActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AdminDashboardActivity) Objects.requireNonNull(getActivity())).onBackPressed();
                    }
                });
            } else if (getActivity() instanceof UserDashboardActivity) {
                ((UserDashboardActivity) getActivity()).setSupportActionBar(toolbar);
                Objects.requireNonNull(((UserDashboardActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
                Objects.requireNonNull(((UserDashboardActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((UserDashboardActivity) Objects.requireNonNull(getActivity())).onBackPressed();
                    }
                });
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get our html content
        String htmlAsString = getString(R.string.label_privacy_policy);
        WebView webView = getView().findViewById(R.id.webView);
        webView.loadDataWithBaseURL(null, htmlAsString, "text/html", "utf-8", null);

        //Spanned htmlAsSpanned = Html.fromHtml(htmlAsString); // used by TextView
        // set the html content on the TextView
        //textview.setText(htmlAsSpanned);


    }
}