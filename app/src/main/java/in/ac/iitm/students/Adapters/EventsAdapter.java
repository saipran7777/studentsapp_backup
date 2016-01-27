package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import in.ac.iitm.students.Objects.GridImage;
import in.ac.iitm.students.R;

import java.util.ArrayList;

/**
 * Created by arunp on 19-Jan-16.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder>{
    Context context;
    private DisplayImageOptions options;
    ArrayList<GridImage> Images=new ArrayList<GridImage>();
    public EventsAdapter(Context context) {
        this.context=context;
    }
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.vButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent("Music Event","crc",0,4);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout vButton;

        public ViewHolder(View itemView) {
            super(itemView);
            vButton = (LinearLayout) itemView.findViewById(R.id.venue);
        }
    }
    public void addEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
