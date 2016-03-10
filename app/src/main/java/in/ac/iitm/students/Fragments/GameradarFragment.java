package in.ac.iitm.students.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

import java.util.Map;

import in.ac.iitm.students.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameradarFragment extends Fragment {


    public GameradarFragment() {
        // Required empty public constructor
    }
    View v;
    Context context;
    Firebase myFirebaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =inflater.inflate(R.layout.fragment_gameradar, container, false);
        context = getActivity();
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase(context.getString(R.string.firebaseurl));
        Map timestamp = ServerValue.TIMESTAMP;


        return v;
    }

}
