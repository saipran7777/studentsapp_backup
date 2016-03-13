package in.ac.iitm.students.Fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ac.iitm.students.Adapters.GameRadarAdapter;
import in.ac.iitm.students.GameRadarProfileEditActivity;
import in.ac.iitm.students.MainActivity;
import in.ac.iitm.students.Objects.Feedback;
import in.ac.iitm.students.Objects.GameRadarGame;
import in.ac.iitm.students.Objects.GameRadarUser;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameradarFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton fabaddImage;
    FloatingActionButton fabtic;

    public GameradarFragment() {
        // Required empty public constructor
    }

    View v;
    Context context;
    Firebase myFirebaseRef;
    final Gson gson = new Gson();
    ArrayList<GameRadarGame> gameRadarGames;
    GameRadarAdapter gameRadarAdapter;
    CardView myView;
    static Calendar calendar;
    static Long todayTiminmills;
    static EditText game, location, capacity;
    static TextView time, date;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_gameradar, container, false);
        context = getActivity();
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase(context.getString(R.string.firebaseurl));
        if (Utils.getprefString(Strings.GAMERADARUSER, context) == "") {
            Intent intent = new Intent(getActivity(), GameRadarProfileEditActivity.class);
            context.startActivity(intent);
            getActivity().finish();
        }else{
            fabaddImage = (FloatingActionButton) v.findViewById(R.id.fabadd);
            fabtic = (FloatingActionButton) v.findViewById(R.id.fabtic);
            fabtic.hide();
            myView = (CardView) v.findViewById(R.id.cardView);
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
                    if (Utils.isEmpty(game) || Utils.isEmpty(location) || Utils.isEmpty(capacity)) {
                        Toast.makeText(context, "You haven't filled everything", Toast.LENGTH_LONG).show();
                    } else if (calendar == null || todayTiminmills == null) {
                        Toast.makeText(context, "You haven't selcected date and time", Toast.LENGTH_LONG).show();
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
                                // PostPost();
                                GameRadarUser gameRadarUser = gson.fromJson(Utils.getprefString(Strings.GAMERADARUSER, context), GameRadarUser.class);

                                GameRadarGame NewGame = new GameRadarGame(gameRadarUser, null,
                                        location.getText().toString(), game.getText().toString(), calendar
                                        .getTimeInMillis() + todayTiminmills, System.currentTimeMillis(), Long.parseLong(capacity.getText().toString(), 10));
                                Firebase gameRef = myFirebaseRef.child("game_radar").child("games");
                                Firebase newPostRef = gameRef.push();
                                newPostRef.setValue(NewGame);

                                myFirebaseRef.child("game_radar").child("users").child(gameRadarUser.getRollno())
                                        .child("posts").child(newPostRef.getKey()).setValue(true);

                                game.setText("");
                                location.setText("");
                                capacity.setText("");
                                time.setText("Time");
                                date.setText("Date");


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
            ImageButton closeButton = (ImageButton) v.findViewById(R.id.imageButton_close);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hidePostDialoage();
                }
            });
            game = (EditText) v.findViewById(R.id.edit_gameradar_game);
            date = (TextView) v.findViewById(R.id.edit_gameradar_date);
            time = (TextView) v.findViewById(R.id.edit_gameradar_time);
            location = (EditText) v.findViewById(R.id.edit_gameradar_location);
            capacity = (EditText) v.findViewById(R.id.edit_gameradar_capacity);

            ((Button) v.findViewById(R.id.button_gameradar_pick_time)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vi) {
                    showTimePickerDialog(v);
                    Log.d("iam herw", "hai i am here");
                }
            });
            ((Button) v.findViewById(R.id.button_gameradar_pick_date)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vi) {
                    showDatePickerDialog(v);
                }
            });


            recyclerView = (RecyclerView) v.findViewById(R.id.gameradar_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);

            gameRadarGames = new ArrayList<GameRadarGame>();
            gameRadarAdapter = new GameRadarAdapter(gameRadarGames, context);
            recyclerView.setAdapter(gameRadarAdapter);
            gameRadarGames = (ArrayList<GameRadarGame>) gson
                    .fromJson(Utils.getprefString(Strings.GAMERADARDATA, context), new TypeToken<ArrayList<GameRadarGame>>() {
                    }.getType());
            if (gameRadarGames!=null ){
                gameRadarAdapter.setGameList(reverse(gameRadarGames));
                gameRadarAdapter.notifyDataSetChanged();
            }


            final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Loading Data ...", true);
            Firebase gameRef = myFirebaseRef.child("game_radar").child("games");
            gameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    // Log.d("Aqel", snapshot.getValue().toString());
                    gameRadarGames =new ArrayList<GameRadarGame>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        GameRadarGame game = postSnapshot.getValue(GameRadarGame.class);
                        game.setId(postSnapshot.getKey());
                        Log.d("key", postSnapshot.getKey());
                        gameRadarGames.add(game);
                    }
                    gameRadarAdapter.setGameList(reverse(gameRadarGames));
                    ringProgressDialog.dismiss();
                    Utils.saveprefString(Strings.GAMERADARDATA, gson.toJson(gameRadarGames), context);
                    gameRadarAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    ringProgressDialog.dismiss();
                    Log.d("no", "no net Connection");
                }
            });


        }
/*
        ArrayList<GameRadarUser> tempuser =new ArrayList<GameRadarUser>();
        tempuser.add(gameRadarUser);
        tempuser.add(gameRadarUser);
        tempuser.add(gameRadarUser);
        GameRadarGame temp = new GameRadarGame(gameRadarUser,tempuser,"jamuna","footer",
                System.currentTimeMillis(),System.currentTimeMillis(),50);
        Firebase gameRef =myFirebaseRef.child("game_radar").child("games");
        gameRef.push().setValue(temp);
        Map timestamp = ServerValue.TIMESTAMP;*/


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.showItem(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.hideItem(0);

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            Date datedsf = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();

            Integer total = (hourOfDay * 60 + minute) * 60 * 1000;
            todayTiminmills = total.longValue();
            Date date1 = new Date(total.longValue() + datedsf.getTime());
            SimpleDateFormat df2 = new SimpleDateFormat("KK:mm a");
            time.setText(df2.format(date1));

        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        Calendar c;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            calendar = c;
            calendar.set(year, month, day, 0, 0, 0);
            Date date1 = new Date(calendar.getTimeInMillis());
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMM");
            date.setText(df2.format(date1));
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public static <T> ArrayList<T> reverse(ArrayList<T> list) {
        int length = list.size();
        ArrayList<T> result = new ArrayList<T>(length);

        for (int i = length - 1; i >= 0; i--) {
            result.add(list.get(i));
        }

        return result;
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
                game.setText("");
                location.setText("");
                capacity.setText("");
                time.setText("Time");
                date.setText("Date");
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
