package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.ac.iitm.students.NewsActivity;
import in.ac.iitm.students.Objects.Location;
import in.ac.iitm.students.Objects.News;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

/**
 * Created by arunp on 20-Feb-16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    ArrayList<News> newses =new ArrayList<News>();
    Context context;
    final Gson gson = new Gson();

    public  NewsAdapter(Context context, ArrayList<News> Newses){
        this.newses=Newses;
        this.context=context;
    }
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        final News news =newses.get(position);
        holder.date.setText(getlongtoago(news.getDate()));
        holder.title.setText(news.getTitle());
        holder.summary.setText(news.getSummary());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, NewsActivity.class);
                intent.putExtra("news",gson.toJson(news));
                context.startActivity(intent);
            }
        });
        if(news.getImageerl()!=""){
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(news.getImageerl())
                    .centerCrop()
                    .crossFade()
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return newses.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title,summary,date;
        LinearLayout layout;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            title =(TextView) itemView.findViewById(R.id.news_title);
            summary =(TextView )itemView.findViewById(R.id.news_summary);
            date =(TextView )itemView.findViewById(R.id.news_time);
            layout =(LinearLayout) itemView.findViewById(R.id.news_layout);
            imageView =(ImageView) itemView.findViewById(R.id.news_image);

        }
    }
    public static String getlongtoago(long createdAt) {
        DateFormat userDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        DateFormat dateFormatNeeded = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS");
        Date date = null;
        date = new Date(createdAt);
        String crdate1 = dateFormatNeeded.format(date);

        // Date Calculation
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        crdate1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);

        // get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(crdate1);
            current = dateFormat.parse(currenttime);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = current.getTime() - CreatedAt.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        String time = null;
        if (diffDays > 0) {
            if (diffDays == 1) {
                time = diffDays + "day ago ";
            } else {
                time = diffDays + "days ago ";
            }
        } else {
            if (diffHours > 0) {
                if (diffHours == 1) {
                    time = diffHours + "hr ago";
                } else {
                    time = diffHours + "hrs ago";
                }
            } else {
                if (diffMinutes > 0) {
                    if (diffMinutes == 1) {
                        time = diffMinutes + "min ago";
                    } else {
                        time = diffMinutes + "mins ago";
                    }
                } else {
                    if (diffSeconds > 0) {
                        time = diffSeconds + "secs ago";
                    }
                }

            }

        }
        return time;
    }

}
