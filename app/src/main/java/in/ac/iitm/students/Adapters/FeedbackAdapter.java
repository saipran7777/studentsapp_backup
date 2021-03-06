package in.ac.iitm.students.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.ChipViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ac.iitm.students.FeedbackActivity;
import in.ac.iitm.students.Objects.Feedback;
import in.ac.iitm.students.Objects.GameRadarGame;
import in.ac.iitm.students.Objects.GameRadarUser;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

/**
 * Created by arunp on 03-Mar-16.
 */
public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    public ArrayList<Feedback> feedbacks;
    Context context;
    final Gson gson = new Gson();

    public FeedbackAdapter(Context context, ArrayList<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
        this.context = context;
    }
    public void setFeedbacks(ArrayList<Feedback> feedbacks){
        this.feedbacks =feedbacks;
    }
    public ArrayList<Feedback> getFeedbacks(){
        return feedbacks;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedback_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Feedback feedback = feedbacks.get(position);
        Log.d("efsdfsdfsdf fuck it",feedback.getRollno());

        if (feedback.getRollno().toLowerCase().trim().equals(Utils.getprefString(Strings.ROLLNO, context).toLowerCase().trim())) {
            holder.imageButtonclose.setVisibility(View.VISIBLE);
        } else {
            holder.imageButtonclose.setVisibility(View.GONE);
        }

        holder.imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePost(feedback.getId(),position);

            }
        });

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


        if (feedback.getAnonymous() == 1) {
            Glide.with(context)
                    .load(R.drawable.anonymous)
                    .centerCrop()
                    .crossFade()
                    .into(holder.imageView);
            holder.username.setVisibility(View.INVISIBLE);

        } else {
            Glide.with(context)
                    .load(R.drawable.user)
                    .centerCrop()
                    .crossFade()
                    .into(holder.imageView);
            holder.username.setText(feedback.getUser_name());
            holder.username.setVisibility(View.VISIBLE);

        }
        setAngry(holder.angry, feedback.getAvg_anger());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedbackActivity.class);
                intent.putExtra("data", gson.toJson(feedback));
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, summary, date, username;
        LinearLayout  angry;
        RelativeLayout layout;
        ChipView chipDefault;
        ImageView imageView;
        ImageView imageButtonclose;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.feedback_title);
            summary = (TextView) itemView.findViewById(R.id.feedback_summary);
            date = (TextView) itemView.findViewById(R.id.feedback_time);
            layout = (RelativeLayout) itemView.findViewById(R.id.feedback_layout);
            chipDefault = (ChipView) itemView.findViewById(R.id.chipview);
            angry = (LinearLayout) itemView.findViewById(R.id.feedback_andry);
            imageView = (ImageView) itemView.findViewById(R.id.user);
            username = (TextView) itemView.findViewById(R.id.feedback_user);
            imageButtonclose = (ImageView) itemView.findViewById(R.id.imageButton_close);

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

    public void setAngry(View view, float level) {
        ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
        imageViews.add((ImageView) view.findViewById(R.id.ang1));
        imageViews.add((ImageView) view.findViewById(R.id.ang2));
        imageViews.add((ImageView) view.findViewById(R.id.ang3));
        imageViews.add((ImageView) view.findViewById(R.id.ang4));
        imageViews.add((ImageView) view.findViewById(R.id.ang5));
        int alevel = (int) level;
        for (int i = 0; i < 5; i++) {
            if (alevel >= i + 1) {
                Glide.with(context)
                        .load(R.drawable.emoji_angry)
                        .centerCrop()
                        .crossFade()
                        .into(imageViews.get(i));

            }
        }
    }

    public void removePost(final int id, final int position) {
        final ProgressDialog progress = new ProgressDialog(context);

        final RequestQueue queue = Volley.newRequestQueue(context);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST,
                context.getString(R.string.feedbackpostremoveurel),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("success") == 1) {
                                feedbacks.remove(position);
                                notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("authorise", "ds");
                params.put("postid", Integer.toString(id));

                return params;
            }
        };
        new AlertDialog.Builder(context)
                .setTitle("Delete post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        queue.add(stringRequest);
                        progress.setCancelable(false);
                        progress.setMessage("removing posting  ...");
                        progress.show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show();

    }

}