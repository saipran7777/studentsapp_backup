package in.ac.iitm.students.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

/**
 * Created by arunp on 05-Mar-16.
 */
public class FeedbackCommentAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    ArrayList<Feedback.FeedbackComment> commentArrayList ;
    Feedback feedback;
    Context context;
    final Gson gson = new Gson();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public FeedbackCommentAdapter(Context context,Feedback feedback) {
        this.feedback=feedback;
        this.commentArrayList = feedback.getComments();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.feedback_header, parent, false);
            return new HeaderViewHolder (v);
        } else if(viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.feedback_footer, parent, false);
            return new FooterViewHolder (v);
        } else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.feedback_comment_listitem, parent, false);
            return new GenericViewHolder (v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.summary.setText(Html.fromHtml(feedback.getContent()).toString());
            headerHolder.title.setText(feedback.getTitle());
            try {
                Date dateD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(feedback.getCreated_at());
               headerHolder. date.setText(FeedbackAdapter.getlongtoago(dateD.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<Chip> chipList = new ArrayList<>();

            for (Feedback.Tag tag : feedback.getTags()) {
                chipList.add(new Tag(tag.getTagname_name()));

            }
            ChipViewAdapter adapter = new MainChipViewAdapter(context);
            headerHolder.chipDefault.setChipList(chipList);


            if(feedback.getAnonymous()==1){
                Glide.with(context)
                        .load(R.drawable.anonymous)
                        .centerCrop()
                        .crossFade()
                        .into(headerHolder. imageView);
                headerHolder.username.setVisibility(View.INVISIBLE);

            }
            else {
                Glide.with(context)
                        .load(R.drawable.user)
                        .centerCrop()
                        .crossFade()
                        .into(headerHolder. imageView);
               headerHolder. username.setText(feedback.getUser_name());
                headerHolder.username.setVisibility(View.VISIBLE);

            }
            setAngry(headerHolder.angry,feedback.getAvg_anger());
            //footer
        } else if(holder instanceof FooterViewHolder) {
            final FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.fab_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    footerHolder.edit_comment.getText().toString();
                    postComment(footerHolder.edit_comment,Utils.getprefString(Strings.ROLLNO,context)
                            , Integer.toString(feedback.getId()));
                }
            });

        } else if(holder instanceof GenericViewHolder) {
            GenericViewHolder currentItem = (GenericViewHolder) holder;
            final Feedback.FeedbackComment comment =commentArrayList.get(position-1);
            currentItem.content.setText(comment.getContent());
            currentItem.username.setText(comment.getUser_name());
            if (comment.getRollno().toLowerCase().trim().equals(Utils.getprefString(Strings.ROLLNO, context).toLowerCase().trim())) {
               currentItem.imageButtonremove.setVisibility(View.VISIBLE);
            } else {
                currentItem.imageButtonremove.setVisibility(View.GONE);
            }

            currentItem.imageButtonremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePostComment(comment.getId(),position-1);

                }
            });
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(comment.getCreated_at());
                currentItem.date.setText(getlongtoago(date.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    public int getItemCount() {
        return commentArrayList.size()+2;
    }

    @Override
    public int getItemViewType (int position) {
        if(isPositionHeader (position)) {
            return TYPE_HEADER;
        } else if(isPositionFooter (position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader (int position) {
        return position == 0;
    }

    private boolean isPositionFooter (int position) {
        return position == commentArrayList.size () + 1;
    }





    class FooterViewHolder extends RecyclerView.ViewHolder {
        AppCompatEditText edit_comment;
        FloatingActionButton fab_comment;

        public FooterViewHolder (View itemView) {
            super (itemView);
            edit_comment = (AppCompatEditText) itemView.findViewById(R.id.edit_comment);
            fab_comment = (FloatingActionButton) itemView.findViewById(R.id.fab_post_comment);
          //  this.txtTitleFooter = (TextView) itemView.findViewById (R.id.txtFooter);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView title, summary, date,username;
        ChipView chipDefault;
        ImageView imageView ;
        LinearLayout angry;
        public HeaderViewHolder (View itemView) {
            super (itemView);
            title = (TextView) itemView.findViewById(R.id.feedback_title);
            summary = (TextView) itemView.findViewById(R.id.feedback_summary);
            date = (TextView) itemView.findViewById(R.id.feedback_time);
            chipDefault = (ChipView) itemView.findViewById(R.id.chipview);
            angry =(LinearLayout) itemView.findViewById(R.id.feedback_andry);
            imageView = (ImageView) itemView.findViewById(R.id.user);
            username =(TextView) itemView.findViewById(R.id.feedback_user);
          //  this.txtTitleHeader = (TextView) itemView.findViewById (R.id.txtHeader);
        }
    }

    class GenericViewHolder extends RecyclerView.ViewHolder {
        public TextView content, date,username;
        ImageButton imageButtonremove;
        public GenericViewHolder (View itemView) {
            super (itemView);
            imageButtonremove=(ImageButton) itemView.findViewById(R.id.imageButton_close);
            content = (TextView) itemView.findViewById(R.id.comment_content);
            date = (TextView) itemView.findViewById(R.id.comment_time);
            username =(TextView) itemView.findViewById(R.id.commetn_name);

            //   this.txtName = (TextView) itemView.findViewById (R.id.txtName);
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
    public void postComment(final AppCompatEditText commentEdittext, final String user, final String postid){
        final String comment = commentEdittext.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                context.getString(R.string.feedbackcommenturel),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("sdfasdfasdfsad",response);
                        String jsArrayString ="[]";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsArrayString =jsonObject.getJSONArray("comments").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ArrayList<Feedback.FeedbackComment>  feedbackComments=new ArrayList<Feedback.FeedbackComment>();

                        //  Log.d("response",response);
                        feedbackComments = (ArrayList<Feedback.FeedbackComment>) gson.fromJson(jsArrayString,
                                new TypeToken<ArrayList<Feedback.FeedbackComment>>() {
                                }.getType());
                        commentArrayList =feedbackComments;
                        feedback.setComments(feedbackComments);
                        commentEdittext.setText("");
                        notifyDataSetChanged();
                        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(commentEdittext.getWindowToken(), 0);

                        // Log.d("content",Float.toString(feedbackList.get(0).getAvg_anger()));
                       //  FeedbackActivity.recyclerView.setAdapter(new FeedbackCommentAdapter(context,feedback));
                       // FeedbackActivity.adapter.notifyDataSetChanged();
                       // FeedbackActivity.recyclerView.scrollToPosition(FeedbackActivity.recyclerView.getAdapter().getItemCount() - 1);

                        //  Utils.saveprefString(Strings.FEEDBACK,response,getActivity());
                       // swipeRefreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"No internet connection",Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("authorise","ds");
                params.put("postid",postid);
                params.put("user",user);
                params.put("comment",comment);

                return params;
            }
        };

        queue.add(stringRequest);
    }


    public void removePostComment(final int id, final int position) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setMessage("removing comment  ...");
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                context.getString(R.string.feedbackpostcommentremoveurel),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("success") == 1) {
                                commentArrayList.remove(position);
                                feedback.setComments(commentArrayList);
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
                params.put("commentid", Integer.toString(id));

                return params;
            }
        };

        queue.add(stringRequest);
    }
}
