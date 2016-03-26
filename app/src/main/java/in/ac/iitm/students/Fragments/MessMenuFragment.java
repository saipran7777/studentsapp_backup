package in.ac.iitm.students.Fragments;


import android.graphics.Bitmap;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MessMenuFragment extends Fragment {


    public MessMenuFragment() {
        // Required empty public constructor
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mess_menu, container, false);
        final SubsamplingScaleImageView imageView =
                (SubsamplingScaleImageView) view.findViewById(R.id.image);
        final LinearLayout layout =(LinearLayout) view.findViewById(R.id.layout);
        Button button1 =(Button) view.findViewById(R.id.button1);
        Button button2 =(Button) view.findViewById(R.id.button2);
        Button button3 =(Button) view.findViewById(R.id.button4);
        Button button4 =(Button) view.findViewById(R.id.button5);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                Glide.with(getActivity())
                        .load("https://students.iitm.ac.in/studentsapp/messmenu/1.jpg")
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
                        .load("https://students.iitm.ac.in/studentsapp/messmenu/2.jpg")
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
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                Glide.with(getActivity())
                        .load("https://students.iitm.ac.in/studentsapp/messmenu/3.jpg")
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
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                Glide.with(getActivity())
                        .load("https://students.iitm.ac.in/studentsapp/messmenu/4.jpg")
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
