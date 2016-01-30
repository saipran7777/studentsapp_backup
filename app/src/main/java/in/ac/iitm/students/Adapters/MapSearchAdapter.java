package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import in.ac.iitm.students.Fragments.MapFragment;
import in.ac.iitm.students.Objects.Location;
import in.ac.iitm.students.R;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by arunp on 28-Jan-16.
 */
public class MapSearchAdapter  extends RecyclerView.Adapter<MapSearchAdapter.ViewHolder>{
    ArrayList<Location> Locations;
    Context context;
    Marker marker;
    GoogleMap map;
    CardView cardView;
    int x,y;
    public  MapSearchAdapter(Context context, ArrayList<Location> Locations,Marker marker,GoogleMap
            map,CardView cardView){
        this.cardView=cardView;
        this.Locations=Locations;
        this.context=context;
        this.marker=marker;
        this.map=map;
    }
    @Override
    public MapSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.map_search_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MapSearchAdapter.ViewHolder holder, int position) {
        final Location location=Locations.get(position);
        holder.description.setText(location.getLocdescrip());
        holder.name.setText(location.getLocname());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartAnimation(location);
            }
        });
        holder.layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Log.d(LOG_TAG, "x = " + motionEvent.getX() + " y = " + motionEvent.getY());
                x= (int) motionEvent.getX();
                y= (int) motionEvent.getY();

                Log.d(Integer.toString(x),Integer.toString(y));
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return Locations.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout layout;
        public TextView name,description;

        public ViewHolder(View itemView) {
            super(itemView);
            layout =(LinearLayout) itemView.findViewById(R.id.itemlayout);
            name =(TextView) itemView.findViewById(R.id.locationName);
            description =(TextView )itemView.findViewById(R.id.locationDescription);
        }
    }
    public void AddMarker(Location location){
        marker.remove();
        LatLng position = new LatLng(location.getLat(), location.getLng());
        marker = map.addMarker(new MarkerOptions().position(position)
                .title(location.getLocname()));
        marker.setSnippet(location.getLocdescrip());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        map.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null);
        marker.showInfoWindow();
        MapFragment.marker =marker;
        return;
    }
    void StartAnimation(final Location location){
        final SupportAnimator[] animator = new SupportAnimator[1];

        if (true) {
            Log.d("dgh","2");
            animator[0] = ViewAnimationUtils.createCircularReveal(cardView,cardView.getWidth()/2 ,
                   y, cardView.getHeight(), 0);
            animator[0].setDuration(500);
            animator[0].setInterpolator(new AccelerateDecelerateInterpolator());
            animator[0].start();

            animator[0].addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {
                }

                @Override
                public void onAnimationEnd() {
                    animator[0] = null;
                    cardView.setVisibility(cardView.INVISIBLE);
                    AddMarker(location);
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
        }
        return;
    }

}
