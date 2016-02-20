package in.ac.iitm.students;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;

import in.ac.iitm.students.Objects.News;

public class NewsActivity extends AppCompatActivity {
    final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String data = getIntent().getExtras().getString("news");
        News news =gson.fromJson(data,News.class);
        TextView textView =(TextView) findViewById(R.id.newsa_title);
        WebView webView =(WebView) findViewById(R.id.webView);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
       // webView.getSettings().setLoadWithOverviewMode(true);
       // webView.getSettings().setUseWideViewPort(true);

        textView.setText(news.getTitle());
        webView.loadData("<style>img{display: inline; height: auto; max-width: 100%;}</style>"+news.getContent(),"text/html", "UTF-8");
    }

}
