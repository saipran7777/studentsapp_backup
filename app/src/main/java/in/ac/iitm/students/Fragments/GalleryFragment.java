package in.ac.iitm.students.Fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;

import in.ac.iitm.students.Adapters.GalleryAdapter;
import in.ac.iitm.students.Adapters.NewPauseOnScrollListener;
import in.ac.iitm.students.Objects.GridImage;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    RecyclerView mRecyclerView;
    GalleryAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    View v;
    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       v = inflater.inflate(R.layout.fragment_gallery, container, false);
         mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.contentView);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImages(v);
            }
        });
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) v.findViewById(R.id.hot_fragment_recycler);
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new NewPauseOnScrollListener(ImageLoader.getInstance(), true, true));
        new CountDownTimer(300, 1000) {
            public void onFinish() {
                getImages(v);
            }

            public void onTick(long millisUntilFinished) {
            }

        }.start();

        return v;
    }

    public ArrayList<GridImage> getImages(final View view) {
        String url =getString(R.string.url_images);
        final ArrayList<GridImage> images =new ArrayList<GridImage>();
        final RequestQueue queue = Volley.newRequestQueue(getContext());

        if (!mSwipeRefreshLayout.isRefreshing()) {
            Log.d("refreshing", "refresh");
            mSwipeRefreshLayout.setRefreshing(true);
        }
        final JSONArray[] jsonArray = {null};


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

               if(response!=null){
                   try {
                       jsonArray[0] =response.getJSONArray("images");
                   } catch (JSONException e) {
                       e.printStackTrace();

                   }
                   Log.d("Log", response.toString());
                   Utils.saveprefString("imgrespons", response.toString(),getActivity());
               }else{
                   try {
                       JSONObject jsonObject=new JSONObject(Utils.getprefString("imgrespons",getActivity()));
                       jsonArray[0] =jsonObject.getJSONArray("images");
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

               }
                for (int i =0;i< jsonArray[0].length();i++){
                    JSONObject jo = null;
                    try {
                        jo = jsonArray[0].getJSONObject(i);
                        images.add(new GridImage(jo.getString("flag"),jo.getString("country")));
                        Log.d("Log", jo.getString("flag"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mAdapter = new GalleryAdapter(getActivity(), images);
                mRecyclerView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // VolleyLog.d(TAG, "Error: " + error.getMessage());
                Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)
                        .show();

                    try {
                        JSONObject jsonObject=new JSONObject(Utils.getprefString("imgrespons",getActivity()));
                        jsonArray[0] =jsonObject.getJSONArray("images");
                        for (int i =0;i< jsonArray[0].length();i++) {
                            JSONObject jo = null;
                            jo = jsonArray[0].getJSONObject(i);
                            images.add(new GridImage(jo.getString("flag"), jo.getString("country")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                mAdapter = new GalleryAdapter(getActivity(), images);
                mRecyclerView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);


            }
        });
        queue.add(jsonObjReq);


        return images;
    }
}
