package in.ac.iitm.students.Fragments;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.Adapters.FeedbackAdapter;
import in.ac.iitm.students.Objects.Feedback;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;
import in.ac.iitm.students.Views.FlowLayout;
import in.ac.iitm.students.Views.TagView;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;


public class FeedbackFragment extends Fragment {

    View v;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    final Gson gson = new Gson();
    Context context;
    CardView myView;
    FloatingActionButton fabaddImage;
    FloatingActionButton fabtic;
    int angryLevel = 0;
    ArrayList<Tag> tags;
    ArrayList<TagView> tagViews;
    CheckBox checkBox;
    EditText content, title;
    ArrayList<ImageView> angryimageViews;
    public static ArrayList<Feedback> feedbackList;
    public static FeedbackAdapter feedbackAdapter;
    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_feedback, container, false);
        context = getActivity();

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.feedback_contentView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchData();
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.feedback_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        fabaddImage = (FloatingActionButton) v.findViewById(R.id.fabadd);
        fabtic = (FloatingActionButton) v.findViewById(R.id.fabtic);
        fabtic.hide();
        myView = (CardView) v.findViewById(R.id.cardView);
        content = (EditText) v.findViewById(R.id.feedback_content);
        title = (EditText) v.findViewById(R.id.feedback_title);

        checkBox = (CheckBox) v.findViewById(R.id.anonymous);

        final SupportAnimator[] animator = new SupportAnimator[1];
        fabaddImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fabaddImage.hide();
                fabtic.show();
                myView.setVisibility(View.VISIBLE);
                // get the center for the c   lipping circle
                int cx = (myView.getLeft() + myView.getRight()) / 2;
                int cy = (myView.getTop() + myView.getBottom()) / 2;

                // get the final radius for the clipping circle
                int dx = Math.max(cx, myView.getWidth() - cx);
                int dy = Math.max(cy, myView.getHeight() - cy);
                float finalRadius = (float) Math.hypot(dx, dy);
                animator[0] =
                        ViewAnimationUtils.createCircularReveal(myView, myView.getWidth(), myView.getHeight(), 0, myView.getHeight());
                animator[0].setInterpolator(new AccelerateDecelerateInterpolator());
                animator[0].setDuration(700);
                animator[0].start();
            }
        });
        fabtic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Utils.isEmpty(content) || Utils.isEmpty(title)) {
                    Toast.makeText(context, "You haven't filled everything", Toast.LENGTH_LONG).show();
                } else if (angryLevel == 0) {
                    Toast.makeText(context, "You haven't selected angry level", Toast.LENGTH_LONG).show();

                } else {
                    fabaddImage.show();
                    fabtic.hide();
                    animator[0] = ViewAnimationUtils.createCircularReveal(myView, myView.getWidth(),
                            myView.getHeight(), 2 * myView.getHeight(), 0);
                    animator[0].setDuration(700);
                    animator[0].start();

                    animator[0].addListener(new SupportAnimator.AnimatorListener() {
                        @Override
                        public void onAnimationStart() {
                        }

                        @Override
                        public void onAnimationEnd() {
                            animator[0] = null;
                            myView.setVisibility(myView.INVISIBLE);
                            PostPost();
                        }

                        @Override
                        public void onAnimationCancel() {
                        }

                        @Override
                        public void onAnimationRepeat() {
                        }
                    });
                }


            }
        });

        FetchData();
        angryimageViews = new ArrayList<ImageView>();
        angryimageViews.add((ImageView) v.findViewById(R.id.ang1));
        angryimageViews.add((ImageView) v.findViewById(R.id.ang2));
        angryimageViews.add((ImageView) v.findViewById(R.id.ang3));
        angryimageViews.add((ImageView) v.findViewById(R.id.ang4));
        angryimageViews.add((ImageView) v.findViewById(R.id.ang5));
        //for angry level
        for (int i = 0; i < 5; i++) {
            final int finalI = i;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                angryimageViews.get(i).setImageAlpha((int) (255 * .2));
            } else {
                angryimageViews.get(i).setAlpha((float) 0.2);
            }

            angryimageViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    angryLevel = finalI + 1;
                    for (int j = 0; j < 5; j++) {
                        float alpha;
                        if (j <= finalI) alpha = (float) 1;
                        else alpha = (float) 0.2;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            angryimageViews.get(j).setImageAlpha((int) (255 * alpha));
                        } else {
                            angryimageViews.get(j).setAlpha(alpha);
                        }

                    }
                }
            });
        }

        tags = new ArrayList<Tag>();
        tags.add(new Tag("sports", 1));
        tags.add(new Tag("cultural", 2));
        tags.add(new Tag("co-curricular", 3));
        tags.add(new Tag("academic", 4));
        tags.add(new Tag("research", 5));
        tags.add(new Tag("Hostel Affairs", 6));
        tags.add(new Tag("General Student Facilities", 7));
        tags.add(new Tag("Internal Opportunities and Alumni Network", 8));

        tagViews = new ArrayList<TagView>();
        FlowLayout flowLayout = (FlowLayout) v.findViewById(R.id.flow_layout);
        for (Tag tag : tags) {
            TagView tagView = new TagView(context);
            tagView.setTag(false, tag.getTagname_name());
            flowLayout.addView(tagView);
            tagViews.add(tagView);
        }

        ImageButton closeButton =(ImageButton) v.findViewById(R.id.imageButton_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinitialisepost();
                hidePostDialoage();
            }
        });

        //  flowLayout.addView(tag);


        return v;
    }

    void FetchData() {
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.feedback_contentView);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getString(R.string.feedbackurl),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //  Log.d("response",response);
                        try {
                            feedbackList = (ArrayList<Feedback>) gson.fromJson(response,
                                    new TypeToken<ArrayList<Feedback>>() {
                                    }.getType());
                            // Log.d("content",Float.toString(feedbackList.get(0).getAvg_anger()));
                            feedbackAdapter =new FeedbackAdapter(getActivity(), feedbackList);
                            recyclerView.setAdapter(feedbackAdapter);
                            Utils.saveprefString(Strings.FEEDBACK, response, getActivity());
                            //Log.d("Location Name",locationList.get(1).getDepname());
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JsonSyntaxException e) {
                            Toast.makeText(context, "no posts", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String response = Utils.getprefString(Strings.FEEDBACK, context);
                        if (response == "") {
                            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                        } else {
                            feedbackList = (ArrayList<Feedback>) gson.fromJson(Utils.getprefString(Strings.FEEDBACK, context),
                                    new TypeToken<ArrayList<Feedback>>() {
                                    }.getType());
                            //   Log.d("content",Float.toString(feedbackList.get(0).getAvg_anger()));
                            feedbackAdapter =new FeedbackAdapter(getActivity(), feedbackList);

                            recyclerView.setAdapter(feedbackAdapter);
                        }

                        //  Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authorise", "ds");
                return params;
            }
        };
        queue.add(stringRequest);
        return;
    }

    private void addChildTo(FlowLayout flowLayout, TagView tag) {

        flowLayout.addView(tag);

    }

    public int dp2px(int dpValue) {
        return (int) (dpValue * getResources().getDisplayMetrics().density);
    }

    public class Tag {
        String tagname_name;
        int tag_id;

        public Tag(String tagname_name, int tag_id) {
            this.tagname_name = tagname_name;
            this.tag_id = tag_id;
        }

        public String getTagname_name() {
            return tagname_name;
        }

        public void setTagname_name(String tagname_name) {
            this.tagname_name = tagname_name;
        }

        public int getTag_id() {
            return tag_id;
        }

        public void setTag_id(int tag_id) {
            this.tag_id = tag_id;
        }
    }

    public void PostPost() {
        //final String comment = commentEdittext.getText().toString();
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setMessage("posting  ...");
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                context.getString(R.string.feedbackposturel),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FetchData();
                        reinitialisepost();
                        progress.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                        showPostDialoage();
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                int anonymous;
                if (checkBox.isChecked()) {
                    anonymous = 1;
                } else anonymous = 0;
                ArrayList<Tag> posttags = new ArrayList<>();
                for (int i = 0; i < tagViews.size(); i++) {
                    if (tagViews.get(i).getIsSelected()) {
                        posttags.add(new Tag("sdkgl", i + 1));
                        Log.d("tags bitch", tagViews.toString());

                    }

                }
                Log.d("tags bitch", gson.toJson(posttags));
                Map<String, String> params = new HashMap<String, String>();
                params.put("authorise", "ds");
                params.put("post", content.getText().toString());
                params.put("user", Utils.getprefString(Strings.ROLLNO, context));
                params.put("tags", gson.toJson(posttags));
                params.put("title", title.getText().toString());
                params.put("anonymous", Integer.toString(anonymous));
                params.put("anger", Integer.toString(angryLevel));

                return params;
            }
        };

        queue.add(stringRequest);
    }

    public void reinitialisepost() {
        angryLevel = 0;
        content.setText("");
        title.setText("");
        checkBox.setChecked(false);
        tagViews = new ArrayList<TagView>();
        tagViews.clear();
        FlowLayout flowLayout = (FlowLayout) v.findViewById(R.id.flow_layout);
        flowLayout.removeAllViews();
        for (Tag tag : tags) {
            TagView tagView = new TagView(context);
            tagView.setTag(false, tag.getTagname_name());
            flowLayout.addView(tagView);
            tagViews.add(tagView);
        }
        for (int i = 0; i < 5; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                angryimageViews.get(i).setImageAlpha((int) (255 * .2));
            } else {
                angryimageViews.get(i).setAlpha((float) 0.2);
            }
        }

    }

    public void showPostDialoage() {
        final SupportAnimator[] animator = new SupportAnimator[1];

        fabaddImage.hide();
        fabtic.show();
        myView.setVisibility(View.VISIBLE);
        // get the center for the c   lipping circle
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, myView.getWidth() - cx);
        int dy = Math.max(cy, myView.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);
        animator[0] =
                ViewAnimationUtils.createCircularReveal(myView, myView.getWidth(), myView.getHeight(), 0, myView.getHeight());
        animator[0].setInterpolator(new AccelerateDecelerateInterpolator());
        animator[0].setDuration(700);
        animator[0].start();
    }
    public void hidePostDialoage() {
        fabaddImage.show();
        fabtic.hide();
        final SupportAnimator[] animator = new SupportAnimator[1];

        animator[0] = ViewAnimationUtils.createCircularReveal(myView, myView.getWidth(),
                myView.getHeight(), 2 * myView.getHeight(), 0);
        animator[0].setDuration(700);
        animator[0].start();

        animator[0].addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
                animator[0] = null;
                myView.setVisibility(myView.INVISIBLE);

            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });

    }

    }
