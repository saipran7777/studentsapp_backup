package in.ac.iitm.students.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ac.iitm.students.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetaccessFragment extends Fragment {


    public NetaccessFragment() {
        // Required empty public constructor
    }
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_netaccess, container, false);
        return view;
    }

}
