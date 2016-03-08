package in.ac.iitm.students.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import in.ac.iitm.students.R;
public class AcademicCalendarFragment extends Fragment {

    public AcademicCalendarFragment() {
        // Required empty public constructor
    }
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

                view=inflater.inflate(R.layout.fragment_academic_calendar, container, false);
        final SubsamplingScaleImageView imageView =
                (SubsamplingScaleImageView) view.findViewById(R.id.image);
        final LinearLayout layout =(LinearLayout) view.findViewById(R.id.layout);
        Button button1 =(Button) view.findViewById(R.id.button1);
        Button button2 =(Button) view.findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                Glide.with(getActivity())
                        .load("https://students.iitm.ac.in/studentsapp/calendar/1.jpg")
                        .asBitmap()
                        .placeholder(R.drawable.error_icon)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                imageView.setImage(ImageSource.bitmap(bitmap));
                                // thumbView.setImageBitmap(bitmap);
                            }
                        });
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                Glide.with(getActivity())
                        .load("https://students.iitm.ac.in/studentsapp/calendar/2.jpg")
                        .asBitmap()
                        .placeholder(R.drawable.error_icon)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                imageView.setImage(ImageSource.bitmap(bitmap));
                                // thumbView.setImageBitmap(bitmap);
                            }
                        });
            }
        });

        return view;
    }




}
