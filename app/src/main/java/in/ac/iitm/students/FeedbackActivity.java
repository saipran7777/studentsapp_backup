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
    public static RecyclerView recyclerView;
    public static FeedbackCommentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        String data = getIntent().getExtras().getString("data");

        Feedback feedback = gson.fromJson(data,Feedback.class);
        recyclerView =(RecyclerView) findViewById(R.id.comment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        ViewGroup.LayoutParams params=recyclerView.getLayoutParams();

        recyclerView.setLayoutParams(params);
         adapter =new FeedbackCommentAdapter(this,feedback);
        recyclerView.setAdapter(adapter);







    }


}
