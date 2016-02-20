package in.ac.iitm.students.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParserFactory;

import in.ac.iitm.students.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TheFifthEstateFragment extends Fragment {
    View v;

    public TheFifthEstateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =inflater.inflate(R.layout.fragment_the_fifth_estate, container, false);
        // Inflate the layout for this fragment

        return v;
    }

}
