package in.ac.iitm.students;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.ac.iitm.students.Adapters.FeedbackAdapter;
import in.ac.iitm.students.Adapters.FeedbackCommentAdapter;
import in.ac.iitm.students.Objects.Feedback;

public class FeedbackActivity extends AppCompatActivity {
    final Gson gson = new Gson();
    public TextView title, summary, date,username;
    ChipView chipDefault;
    ImageView imageView ;
    LinearLayout angry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        String data = getIntent().getExtras().getString("data");
        Feedback feedback = gson.fromJson(data,Feedback.class);
        RecyclerView recyclerView =(RecyclerView) findViewById(R.id.comment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        ViewGroup.LayoutParams params=recyclerView.getLayoutParams();
        int size=feedback.getComments().size();
        if(size==0){
            params.height=100;
        }
        else if(size==1){
            params.height=300;
        }else if(size == 2){
            params.height =500;
        }
        else if(size == 3){
            params.height =600;
        }else {
            params.height =700;
        }
        recyclerView.setLayoutParams(params);
        recyclerView.setAdapter(new FeedbackCommentAdapter(this,feedback.getComments()));
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);



        title = (TextView) findViewById(R.id.feedback_title);
        summary = (TextView) findViewById(R.id.feedback_summary);
        date = (TextView) findViewById(R.id.feedback_time);
        chipDefault = (ChipView) findViewById(R.id.chipview);
        angry =(LinearLayout) findViewById(R.id.feedback_andry);
        imageView = (ImageView) findViewById(R.id.user);
        username =(TextView) findViewById(R.id.feedback_user);


        summary.setText(Html.fromHtml(feedback.getContent()).toString());
        title.setText(feedback.getTitle());
        try {
            Date dateD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(feedback.getCreated_at());
            date.setText(FeedbackAdapter.getlongtoago(dateD.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Chip> chipList = new ArrayList<>();

        for (Feedback.Tag tag : feedback.getTags()) {
            chipList.add(new Tag(tag.getTagname_name()));

        }
        ChipViewAdapter adapter = new MainChipViewAdapter(this);
        chipDefault.setAdapter(adapter);
        chipDefault.setChipList(chipList);


        if(feedback.getAnonymous()==1){
            Glide.with(this)
                    .load(R.drawable.anonymous)
                    .centerCrop()
                    .crossFade()
                    .into( imageView);
            username.setVisibility(View.INVISIBLE);

        }
        else {
            Glide.with(this)
                    .load(R.drawable.user)
                    .centerCrop()
                    .crossFade()
                    .into( imageView);
            username.setText(feedback.getUser_name());
            username.setVisibility(View.VISIBLE);

        }
        setAngry(angry,feedback.getAvg_anger());


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
                Glide.with(this)
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
}
