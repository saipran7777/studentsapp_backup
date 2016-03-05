package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.ChipViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.ac.iitm.students.FeedbackActivity;
import in.ac.iitm.students.Objects.Feedback;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

/**
 * Created by arunp on 03-Mar-16.
 */
public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    ArrayList<Feedback> feedbacks;
    Context context;
    final Gson gson = new Gson();

    public FeedbackAdapter(Context context, ArrayList<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedback_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Feedback feedback = feedbacks.get(position);
        holder.summary.setText(Html.fromHtml(feedback.getContent()).toString());
        holder.title.setText(feedback.getTitle());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(feedback.getCreated_at());
            holder.date.setText(getlongtoago(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Chip> chipList = new ArrayList<>();

        for (Feedback.Tag tag : feedback.getTags()) {
            chipList.add(new Tag(tag.getTagname_name()));

        }
        ChipViewAdapter adapter = new MainChipViewAdapter(context);
        holder.chipDefault.setAdapter(adapter);
        holder.chipDefault.setChipList(chipList);


        if(feedback.getAnonymous()==1){
            Glide.with(context)
                    .load(R.drawable.anonymous)
                    .centerCrop()
                    .crossFade()
                    .into( holder.imageView);
            holder.username.setVisibility(View.INVISIBLE);

        }
        else {
            Glide.with(context)
                    .load(R.drawable.user)
                    .centerCrop()
                    .crossFade()
                    .into( holder.imageView);
            holder.username.setText(feedback.getUser_name());
            holder.username.setVisibility(View.VISIBLE);

        }
        setAngry(holder.angry,feedback.getAvg_anger());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedbackActivity.class);
                intent.putExtra("data",gson.toJson(feedback));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, summary, date,username;
        LinearLayout layout,angry;
        ChipView chipDefault;
        ImageView imageView ;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.feedback_title);
            summary = (TextView) itemView.findViewById(R.id.feedback_summary);
            date = (TextView) itemView.findViewById(R.id.feedback_time);
            layout = (LinearLayout) itemView.findViewById(R.id.feedback_layout);
            chipDefault = (ChipView) itemView.findViewById(R.id.chipview);
            angry =(LinearLayout) itemView.findViewById(R.id.feedback_andry);
            imageView = (ImageView) itemView.findViewById(R.id.user);
            username =(TextView) itemView.findViewById(R.id.feedback_user);

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
        long diffYears = diffDays / 356;

        String time = null;
        if (diffYears > 0) {
            if (diffYears == 1) {
                time = diffYears + " year ago ";
            } else {
                time = diffYears + " years ago ";
            }
        } else {
            if (diffDays > 0) {
                if (diffDays == 1) {
                    time = diffDays + " day ago ";
                } else {
                    time = diffDays + " days ago ";
                }
            } else {
                if (diffHours > 0) {
                    if (diffHours == 1) {
                        time = diffHours + " hr ago";
                    } else {
                        time = diffHours + " hrs ago";
                    }
                } else {
                    if (diffMinutes > 0) {
                        if (diffMinutes == 1) {
                            time = diffMinutes + " min ago";
                        } else {
                            time = diffMinutes + " mins ago";
                        }
                    } else {
                        if (diffSeconds > 0) {
                            time = diffSeconds + " secs ago";
                        }
                    }

                }

            }
        }
        return time;
    }

    public class Tag implements Chip {
        private String mName;
        private int mType = 0;

        public Tag(String name, int type) {
            this(name);
            mType = type;
        }

        public Tag(String name) {
            mName = name;
        }

        @Override
        public String getText() {
            return mName;
        }

        public int getType() {
            return mType;
        }
    }

    public class MainChipViewAdapter extends ChipViewAdapter {
        public MainChipViewAdapter(Context context) {
            super(context);
        }


        @Override
        public int getLayoutRes(int position) {
            return 0;
        }

        @Override
        public int getBackgroundRes(int position) {
            return 0;
        }

        @Override
        public int getBackgroundColor(int position) {
            return getColor(R.color.colorAccent);
        }

        @Override
        public int getBackgroundColorSelected(int position) {
            return 0;
        }

        @Override
        public void onLayout(View view, int position) {
            Tag tag = (Tag) getChip(position);
            ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.WHITE);

        }
    }

    public void setAngry(View view,float level){
        ArrayList<ImageView> imageViews =new ArrayList<ImageView>();
        imageViews.add((ImageView) view.findViewById(R.id.ang1));
        imageViews.add((ImageView) view.findViewById(R.id.ang2));
        imageViews.add((ImageView) view.findViewById(R.id.ang3));
        imageViews.add((ImageView) view.findViewById(R.id.ang4));
        imageViews.add((ImageView) view.findViewById(R.id.ang5));
        int alevel =(int) level;
        for(int i=0;i<5;i++){
        if (alevel>=i+1){
            Glide.with(context)
                    .load(R.drawable.emoji_angry)
                    .centerCrop()
                    .crossFade()
                    .into(imageViews.get(i));

        }
        }
    }
}
