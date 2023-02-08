package com.nightowl.ThinkAgain.Fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nightowl.ThinkAgain.R;

public class AboutUs extends BottomSheetDialogFragment {

    BottomSheetDialog screen;
    BottomSheetBehavior<View> bottomSheetBehavior;

    private ScrollView scrollView;
    private CoordinatorLayout background;
    private ConstraintLayout toolbar;
    private ImageView crossImg;
    private TextView txt;
    private ImageView roundedImageView;
    private CardView cardView2;
    private TextView textView4;
    private CardView cardView3;
    private AppCompatButton followBtn;
    private AppCompatButton privacyPolicyBtn;
    private AppCompatButton contactUsBtn;
    private AppCompatButton rate_us_btn;

    public AboutUs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);


        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        background = (CoordinatorLayout) view.findViewById(R.id.background);
        toolbar = (ConstraintLayout) view.findViewById(R.id.toolbar);
        crossImg = (ImageView) view.findViewById(R.id.cross_img);
        txt = (TextView) view.findViewById(R.id.txt);
        roundedImageView =  view.findViewById(R.id.roundedImageView);
        cardView2 = (CardView) view.findViewById(R.id.cardView2);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        cardView3 = (CardView) view.findViewById(R.id.cardView3);
        followBtn = (AppCompatButton) view.findViewById(R.id.follow_btn);
        rate_us_btn = (AppCompatButton) view.findViewById(R.id.rate_us_btn);
        privacyPolicyBtn = (AppCompatButton) view.findViewById(R.id.privacy_policy_btn);
        contactUsBtn = (AppCompatButton) view.findViewById(R.id.contact_us_btn);



        crossImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUs.this.requireDialog().dismiss();
            }
        });

        rate_us_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+requireActivity().getPackageName())); /// here "yourpackegName" from your app packeg Name
                startActivity(intent);
            }
        });

        privacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://thenightowldev.blogspot.com/2023/01/thing-again-privacy-policy.html")));
            }
        });

        contactUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "contactnightowldev@gmail.com"));
                    intent.setPackage("com.google.android.gm");
                    startActivity(intent);
                } catch (ActivityNotFoundException e){
                    //TODO smth
                }
            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/night_owl_dev/");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.instagram.com/night_owl_dev/")));
                }
            }
        });

        return view;
    }




    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        screen = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        return screen;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setDraggable(false);

        CoordinatorLayout layout = screen.findViewById(R.id.background);
        assert layout != null;
        layout.setFitsSystemWindows(true);
        layout.animate().alphaBy(0.6f).start();
        layout.setElevation(1f);
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);


    }
}