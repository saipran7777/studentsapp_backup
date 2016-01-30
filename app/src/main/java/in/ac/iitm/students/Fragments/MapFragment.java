package in.ac.iitm.students.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.Adapters.EventsAdapter;
import in.ac.iitm.students.Adapters.MapSearchAdapter;
import in.ac.iitm.students.MainActivity;
import in.ac.iitm.students.Objects.Location;
import in.ac.iitm.students.R;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


/**
 * Created by arun on 14-Jul-15.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap map;
    AutoCompleteTextView search;
    final Gson gson = new Gson();
    Boolean inRequest=false;
    RecyclerView mRecyclerView;
    public static Marker  marker;
    CardView myView = null;
    public static int xCard,yCard;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        ((MainActivity) getActivity()).hideViews();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        search =(AutoCompleteTextView) v.findViewById(R.id.searcheditText) ;
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.setPadding(0, 70, 0, 0);
        MapsInitializer.initialize(this.getActivity());
        map.setMyLocationEnabled(true);

        LatLng position = new LatLng(12.9884871, 80.2355152);
        marker = map.addMarker(new MarkerOptions().position(position)
                .title("IIT Madras"));
        marker.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));
        map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        //map.getUiSettings().setMyLocationButtonEnabled(false);


        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

        myView =(CardView) v.findViewById(R.id.cardSearch);
        ImageButton Bsearch=(ImageButton) v.findViewById(R.id.buttonSearch);

        final int[] x = new int[1];
        final int[] y = new int[1];
        final SupportAnimator[] animator = new SupportAnimator[1];
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.setVisibility(View.VISIBLE);
                // get the center for the c   lipping circle
                int cx = (myView.getLeft() + myView.getRight()) / 2;
                int cy = (myView.getTop() + myView.getBottom()) / 2;

                // get the final radius for the clipping circle
                int dx = Math.max(cx, myView.getWidth() - cx);
                int dy = Math.max(cy, myView.getHeight() - cy);
                float finalRadius = (float) Math.hypot(dx, dy);
                animator[0] =
                        ViewAnimationUtils.createCircularReveal(myView, x[0],0, 0,2*myView.getHeight());
                animator[0].setInterpolator(new AccelerateDecelerateInterpolator());
                animator[0].setDuration(500);
                animator[0].start();
            }
        });
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               // Log.d(LOG_TAG, "x = " + motionEvent.getX() + " y = " + motionEvent.getY());
                x[0] = (int) motionEvent.getX();
                y[0] = (int) motionEvent.getY();
                return false;
            }
        });

        Bsearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (animator[0] != null && !animator[0].isRunning()) {
                    animator[0] = ViewAnimationUtils.createCircularReveal(myView, myView.getWidth(), 0, myView.getHeight(), 0);
                    animator[0].setDuration(500);
                    animator[0].start();

                    animator[0].addListener(new SupportAnimator.AnimatorListener() {
                        @Override
                        public void onAnimationStart() {
                        }

                        @Override
                        public void onAnimationEnd() {
                            animator[0] = null;
                            myView.setVisibility(myView.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel() {

                        }

                        @Override
                        public void onAnimationRepeat() {

                        }
                    });
                }
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //Log.d("sajdf",s.toString());


                if(!inRequest){
                    FetchData(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mRecyclerView = (RecyclerView) v.findViewById(R.id.mapRecycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);


        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        ((MainActivity) getActivity()).hideViews();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).hideViews();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("hay", "map is redy");
    }


    void FetchData(final String Word){


        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getString(R.string.StudentsMapSearchurl),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                        if(!response.equals("1")){

                            ArrayList<Location> locationList= null;
                            try {
                                locationList = (ArrayList<Location>) gson.fromJson(response,
                                        new TypeToken<ArrayList<Location>>() {
                                        }.getType());
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                            if(locationList!=null){
                                mRecyclerView.setAdapter(new MapSearchAdapter(getActivity(),locationList,marker,map,myView));
                            }
                        }
                        inRequest=false;
                        //Log.d("Location Name",locationList.get(1).getDepname());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        inRequest=false;
                        Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.d("volley",error.toString());
                        //  Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("word",Word);
                return params;
            }
        };
        queue.add(stringRequest);
        return ;
    }

}
