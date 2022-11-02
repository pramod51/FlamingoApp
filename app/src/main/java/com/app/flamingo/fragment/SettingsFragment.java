package com.app.flamingo.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import com.app.flamingo.BuildConfig;
import com.app.flamingo.R;
import com.app.flamingo.activity.AdminDashboardActivity;
import com.app.flamingo.activity.OnBoardingActivity;
import com.app.flamingo.activity.UserDashboardActivity;
import com.app.flamingo.utils.CommonMethods;
import com.app.flamingo.utils.SharePreferences;

public class SettingsFragment extends Fragment implements View.OnClickListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.item_logout);
        if (item != null)
            item.setVisible(false);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //https://gist.github.com/ferdy182/d9b3525aa65b5b4c468a
        view.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), android.R.color.white));
        // To run the animation as soon as the view is layout in the view hierarchy we add this
        // listener and remove it
        // as soon as it runs to prevent multiple animations if the view changes bounds
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int cx = 20;
                int cy = 20;

                // get the hypothenuse so the radius is from one corner to the other
                int radius = (int) Math.hypot(right, bottom);

                Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                reveal.setInterpolator(new DecelerateInterpolator(2f));
                reveal.setDuration(1000);
                reveal.start();
            }
        });


        setToolbar(view);
        init(view);
        return view;
    }

    private void setToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.toolbar_title_setting));
        if (getActivity() instanceof OnBoardingActivity) {

            ((OnBoardingActivity) getActivity()).setSupportActionBar(toolbar);
            Objects.requireNonNull(((OnBoardingActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(true);
            Objects.requireNonNull(((OnBoardingActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        if (getActivity() instanceof OnBoardingActivity) {
                            ((OnBoardingActivity) getActivity()).finish();
                        }
                    }
                }
            });
        } else if (getActivity() instanceof AdminDashboardActivity) {


            ((AdminDashboardActivity) getActivity()).setSupportActionBar(toolbar);
            Objects.requireNonNull(((AdminDashboardActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(true);
            Objects.requireNonNull(((AdminDashboardActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        if (getActivity() instanceof AdminDashboardActivity) {
                            ((AdminDashboardActivity) getActivity()).onBackPressed();
                        }
                    }
                }
            });
        } else if (getActivity() instanceof UserDashboardActivity) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((UserDashboardActivity) getActivity()).getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),
                        R.color.colorPrimaryDark));
            }

            ((UserDashboardActivity) getActivity()).setSupportActionBar(toolbar);
            Objects.requireNonNull(((UserDashboardActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(true);
            Objects.requireNonNull(((UserDashboardActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        if (getActivity() instanceof UserDashboardActivity) {
                            ((UserDashboardActivity) getActivity()).onBackPressed();
                        }
                    }
                }
            });
        }
    }

    private void init(View view) {

        LinearLayout llPrivacyPolicy = view.findViewById(R.id.ll_privacy_policy);
        LinearLayout llFeedback = view.findViewById(R.id.ll_feedback);
        LinearLayout llApplicationVersion= view.findViewById(R.id.ll_application_version);
        LinearLayout llLogout= view.findViewById(R.id.ll_logout);
        TextView tvApplicationVersion= view.findViewById(R.id.tv_application_version);

        Button btnNext = view.findViewById(R.id.btn_next);
        if (getActivity() != null) {
            if (getActivity() instanceof AdminDashboardActivity
                    || getActivity() instanceof UserDashboardActivity) {
                btnNext.setVisibility(View.GONE);
            } else {
                llLogout.setVisibility(View.GONE);
            }
        }
        tvApplicationVersion.setText(BuildConfig.VERSION_NAME);

        llPrivacyPolicy.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        llFeedback.setOnClickListener(this);
        llLogout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_privacy_policy:
                if (getActivity() != null) {
                    if (getActivity() instanceof OnBoardingActivity) {
                        ((OnBoardingActivity) getActivity()).addFragment(new PrivacyPolicySettingFragment(), null);
                    } else if (getActivity() instanceof AdminDashboardActivity) {
                        ((AdminDashboardActivity) getActivity()).loadFragment(new PrivacyPolicySettingFragment(), null);
                    } else if (getActivity() instanceof UserDashboardActivity) {
                        ((UserDashboardActivity) getActivity()).loadFragment(new PrivacyPolicySettingFragment(), null);
                    }
                }
                break;
            case R.id.btn_next:
                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        Objects.requireNonNull(getActivity()),R.style.CustomDialogTheme);
                alertDialogBuilder.setTitle("Important");
                alertDialogBuilder.setMessage("Have you checked application flow and their provided features by visiting presentation?\n\nIf not than kindly visit it once for better application experience.")
                        .setPositiveButton("MOVE AHEAD", null)
                        .setNegativeButton("VIEW PRESENTATION", null);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        btnPositive.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();

                                SharePreferences.setBool(SharePreferences.KEY_IS_WELCOME_SCREEN_SHOWN, true);
                                if (getActivity() != null) {
                                    if (getActivity() instanceof OnBoardingActivity) {
                                        ((OnBoardingActivity) getActivity()).launchActivity();
                                    }
                                }
                            }
                        });

                        Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        btnNegative.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();*/

                SharePreferences.setBool(SharePreferences.KEY_IS_WELCOME_SCREEN_SHOWN, true);
                if (getActivity() != null) {
                    if (getActivity() instanceof OnBoardingActivity) {
                        ((OnBoardingActivity) getActivity()).launchActivity();
                    }
                }

                break;
            case R.id.ll_feedback:
                DisplayMetrics displaymetrics = new DisplayMetrics();
                Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels;
                int width = displaymetrics.widthPixels;
                PackageManager manager = getActivity().getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                assert info != null;
                String version = info.versionName;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.developer_email)});
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name).concat(" " + version));
                i.putExtra(Intent.EXTRA_TEXT,
                        "\n\n\n" + " Device :" + CommonMethods.getDeviceName() +
                                "\n" + " System Version:" + Build.VERSION.SDK_INT +
                                "\n" + " Display Height  :" + height + "px" +
                                "\n" + " Display Width  :" + width + "px" +
                                "\n");
                startActivity(Intent.createChooser(i, "Send Email"));
                break;

            case R.id.ll_logout:
                CommonMethods.showAlertForLogout(getActivity());
                break;
            default:
                break;
        }
    }
}
