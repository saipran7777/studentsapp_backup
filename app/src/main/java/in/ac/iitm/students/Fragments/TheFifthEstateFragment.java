package in.ac.iitm.students.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.ac.iitm.students.Adapters.NewsAdapter;
import in.ac.iitm.students.LoginActivity;
import in.ac.iitm.students.Objects.News;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Utils;

import static com.android.volley.Request.Method.GET;

/**
 * A simple {@link Fragment} subclass.
 */
public class TheFifthEstateFragment extends Fragment {
    View v;
    final Gson gson = new Gson();
    RecyclerView mRecyclerView;
    Context context;
    public String NEWSSTRING="fifthestatestring";
    public String TAG ="TheFifthEstateFragment";
    public TheFifthEstateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context =getActivity();
        v =inflater.inflate(R.layout.fragment_the_fifth_estate, container, false);
        mRecyclerView  = (RecyclerView) v.findViewById(R.id.recyclerfifthsetate);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        getNews();

        return v;
    }
    public void getNews () {
        final ArrayList<News> newses =new ArrayList<News>();
        String url = getString(R.string.fifthettateurl);
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        int id = 0;
                        Long dateINT = null;
                        String title = null,summary = null,content = null;

                    for (int i =0;response.length()>i;i++){
                        try {
                            JSONObject jsonObject =response.getJSONObject(i);
                            id = jsonObject.getInt("id");
                            Date date = null;
                            SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd, yyyy hh:mm a");
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject.getString("modified").replace("T"," "));
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }
                            dateINT = date.getTime();
                            title = jsonObject.getJSONObject("title").getString("rendered");
                            summary = jsonObject.getJSONObject("excerpt").getString("rendered");
                            summary= Html.fromHtml(summary).toString();
                            content =jsonObject.getJSONObject("content").getString("rendered");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG,title);
                        newses.add(new News(id,title,summary,content,dateINT));
                    }
                        String json =gson.toJson(newses);
                        Utils.saveprefString(NEWSSTRING,json,context);
                        mRecyclerView.setAdapter(new NewsAdapter(context,newses));
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });


        queue.add(jsObjRequest);
    }
}

