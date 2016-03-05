package in.ac.iitm.students.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import in.ac.iitm.students.Adapters.EventsAdapter;
import in.ac.iitm.students.Adapters.FeedbackAdapter;
import in.ac.iitm.students.Adapters.MapSearchAdapter;
import in.ac.iitm.students.Objects.Feedback;
import in.ac.iitm.students.Objects.Location;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;


public class FeedbackFragment extends Fragment {

    View v;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    final Gson gson = new Gson();
    Context context ;
    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_feedback, container, false);
        context =getActivity();

        swipeRefreshLayout=(SwipeRefreshLayout) v.findViewById(R.id.feedback_contentView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchData();
            }
        });

        recyclerView =(RecyclerView) v.findViewById(R.id.feedback_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        FloatingActionButton floatingActionButton = (FloatingActionButton) v.findViewById(R.id.fabadd);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 
            }
        });
        FetchData();

        return v;
    }
    void FetchData(){
        swipeRefreshLayout=(SwipeRefreshLayout) v.findViewById(R.id.feedback_contentView);

        swipeRefreshLayout.setRefreshing(true);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getString(R.string.feedbackurl),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<Feedback> feedbackList =new ArrayList<Feedback>();

                      //  Log.d("response",response);
                        feedbackList = (ArrayList<Feedback>) gson.fromJson(response,
                                new TypeToken<ArrayList<Feedback>>() {
                                }.getType());
                       // Log.d("content",Float.toString(feedbackList.get(0).getAvg_anger()));
                        recyclerView.setAdapter(new FeedbackAdapter(getActivity(),feedbackList));
                        Utils.saveprefString(Strings.FEEDBACK,response,getActivity());
                        //Log.d("Location Name",locationList.get(1).getDepname());
                        swipeRefreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ArrayList<Feedback> feedbackList =new ArrayList<Feedback>();
                        String response =Utils.getprefString(Strings.FEEDBACK,context);
                        if(response==""){
                            Toast.makeText(context,"No internet connection",Toast.LENGTH_LONG).show();
                        }else {
                            feedbackList = (ArrayList<Feedback>) gson.fromJson(Utils.getprefString(Strings.FEEDBACK,context),
                                    new TypeToken<ArrayList<Feedback>>() {
                                    }.getType());
                         //   Log.d("content",Float.toString(feedbackList.get(0).getAvg_anger()));
                            recyclerView.setAdapter(new FeedbackAdapter(getActivity(),feedbackList));
                        }

                        //  Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);

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
