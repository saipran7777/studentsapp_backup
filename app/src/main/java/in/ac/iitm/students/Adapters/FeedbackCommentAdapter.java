package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.ac.iitm.students.Objects.Feedback;
import in.ac.iitm.students.R;

/**
 * Created by arunp on 05-Mar-16.
 */
public class FeedbackCommentAdapter extends RecyclerView.Adapter<FeedbackCommentAdapter.ViewHolder> {
    ArrayList<Feedback.FeedbackComment> commentArrayList ;
    Context context;

    public FeedbackCommentAdapter(Context context,ArrayList<Feedback.FeedbackComment> commentArrayList) {
        this.commentArrayList = commentArrayList;
        this.context = context;
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedback_comment_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feedback.FeedbackComment comment =commentArrayList.get(position);
        holder.content.setText(comment.getContent());
        holder.username.setText(comment.getUser_name());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(comment.getCreated_at());
            holder.date.setText(getlongtoago(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView content, date,username;


        public ViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.comment_content);
            date = (TextView) itemView.findViewById(R.id.comment_time);
            username =(TextView) itemView.findViewById(R.id.commetn_name);

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

}
