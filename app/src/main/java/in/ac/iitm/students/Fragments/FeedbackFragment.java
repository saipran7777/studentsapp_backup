package in.ac.iitm.students.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.Adapters.MapSearchAdapter;
import in.ac.iitm.students.Objects.Feedback;
import in.ac.iitm.students.Objects.Location;
import in.ac.iitm.students.R;


public class FeedbackFragment extends Fragment {

    View v;
    final Gson gson = new Gson();
    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_feedback, container, false);
        FetchData();
        return v;
    }
    void FetchData(){


        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getString(R.string.feedbackurl),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<Feedback> feedbackList =new ArrayList<Feedback>();

                        Log.d("response",response);
                        feedbackList = (ArrayList<Feedback>) gson.fromJson(response,
                                new TypeToken<ArrayList<Feedback>>() {
                                }.getType());
                        Log.d("content",Float.toString(feedbackList.get(0).getAvg_anger()));
                        //Log.d("Location Name",locationList.get(1).getDepname());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("authorise","ds");
                return params;
            }
        };
        queue.add(stringRequest);
        return ;
    }

}
