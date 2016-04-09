package in.ac.iitm.students.Adapters;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.ac.iitm.students.GameRadarProfileActivity;
import in.ac.iitm.students.MainActivity;
import in.ac.iitm.students.Objects.GameRadarGame;
import in.ac.iitm.students.Objects.GameRadarUser;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Services.NotificationPublisher;
import in.ac.iitm.students.Utils.GcmUtils;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;
import in.ac.iitm.students.Views.CircleImageView;

/**
 * Created by arunp on 11-Mar-16.
 */
public class GameRadarAdapter extends RecyclerView.Adapter<GameRadarAdapter.ViewHolder> {
    ArrayList<GameRadarGame> gameRadarGames;
    Context context;
    Firebase myFirebaseRef;
    Gson gson = new Gson();
    String myRollno ;

    public GameRadarAdapter(ArrayList<GameRadarGame> gameRadarGames, Context context) {
        this.gameRadarGames = gameRadarGames;
        this.context = context;
        myFirebaseRef = new Firebase(context.getString(R.string.firebaseurl));
        myRollno= Utils.getprefString(Strings.ROLLNO, context).toLowerCase();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gameradar_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GameRadarGame gameRadarGame = gameRadarGames.get(position);
        final GameRadarUser admin = gameRadarGame.getAdmin();
        if(gameRadarGame.getPlayers()==null) gameRadarGame.setPlayers(new ArrayList<GameRadarUser>());  //to prevent null pointer exceptoin
        final ArrayList<GameRadarUser> players = gameRadarGame.getPlayers();

        if(admin.getRollno().toLowerCase().equals(myRollno)){
            holder.imageViewtrash.setVisibility(View.VISIBLE);
            holder.imageViewtrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RemovePost(gameRadarGame);
                }
            });
        }else {
            holder.imageViewtrash.setVisibility(View.GONE);
        }


        if (amiInThisGame(gameRadarGame)) {
            holder.useradd.setImageResource(R.drawable.leave_game);
            holder.useradd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameRadarUser gameRadarUser = gson.fromJson(Utils.getprefString(Strings.GAMERADARUSER, context), GameRadarUser.class);
                    players.remove(getMyPoition(players));
                    myFirebaseRef.child("game_radar").child("games").child(gameRadarGame.getId())
                            .child("players").setValue(players);

                    myFirebaseRef.child("game_radar").child("users").child(gameRadarUser.getRollno())
                            .child("games").child(gameRadarGame.getId()).removeValue();

                }
            });
        } else {
            holder.useradd.setImageResource(R.drawable.join_game);
            holder.useradd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GameRadarUser gameRadarUser = gson.fromJson(Utils.getprefString(Strings.GAMERADARUSER, context), GameRadarUser.class);
                    players.add(gameRadarUser);
                    myFirebaseRef.child("game_radar").child("games").child(gameRadarGame.getId())
                            .child("players").setValue(players);

                    myFirebaseRef.child("game_radar").child("users").child(gameRadarUser.getRollno())
                            .child("games").child(gameRadarGame.getId()).setValue(true);

                    String ids[] = {Utils.getprefString(Strings.GCMTOKEN, context)};
                    GcmUtils.requestWithSomeHttpHeaders(context, ids, "hai handsome", "football");
                    BuildNotofication(1,"Game Radar",gameRadarGame.getGame()+" will start at "+
                            getTimeString(gameRadarGame.getStartTime()),gameRadarGame.getStartTime()-10*1000*60);
                }
            });
        }

        Glide.with(context)
                .load(admin.getDpurl())
                .centerCrop()
                .crossFade()
                .into(holder.adminDP);
        holder.adminDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameRadarProfileActivity.class);
                intent.putExtra("data", gson.toJson(gameRadarGame.getAdmin()));
                context.startActivity(intent);
            }
        });

        holder.game.setText(gameRadarGame.getGame());
        holder.location.setText(gameRadarGame.getLocation());
        holder.time.setText(getDateString(gameRadarGame.getStartTime()));


        holder.capacity.setText(Integer.toString(gameRadarGame.getPlayers().size()) + "/"
                + gameRadarGame.getCapacity());
        holder.userlistContainer.removeAllViews();
        ArrayList<GameRadarUser> playersReverse =reverse(players);

        for (int i = 0; (i < 3) && (gameRadarGame.getPlayers().size() > i); i++) {
            final GameRadarUser gameRadarUser = playersReverse.get(i);
            View v = LayoutInflater.from(holder.userlistContainer.getContext())
                    .inflate(R.layout.gameradar_user_list_item, holder.userlistContainer, false);
            Glide.with(context)
                    .load(gameRadarUser.getDpurl())
                    .centerCrop()
                    .crossFade()
                    .into(((CircleImageView) v.findViewById(R.id.gameradar_user_dp))
                    );
            CircleImageView dp = ((CircleImageView) v.findViewById(R.id.gameradar_user_dp));
            dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GameRadarProfileActivity.class);
                    intent.putExtra("data", gson.toJson(gameRadarUser));
                    context.startActivity(intent);
                }
            });


            ((TextView) v.findViewById(R.id.gameradar_user_name)).setText(gameRadarUser.getName());
            ((TextView) v.findViewById(R.id.gameradar_user_hostal)).setText(gameRadarUser.getHostal());


            holder.userlistContainer.addView(v);
        }
        if(players.size()>3){
            holder.moreContainer.setVisibility(View.VISIBLE);
            holder.moreCount.setText(Integer.toString(players.size()-3)+" more");
            holder.moreContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowDiloge(players);
                }
            });
        }else {
            holder.moreContainer.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return gameRadarGames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView adminDP, useradd;
        TextView game, time, location, capacity ,moreCount;
        LinearLayout userlistContainer,moreContainer;
        ImageView imageViewtrash;

        public ViewHolder(View itemView) {
            super(itemView);
            adminDP = (CircleImageView) itemView.findViewById(R.id.gameradar_admin_dp);
            useradd = (CircleImageView) itemView.findViewById(R.id.gameradar_button_add);
            game = (TextView) itemView.findViewById(R.id.gameradar_game);
            location = (TextView) itemView.findViewById(R.id.gameradar_location);
            time = (TextView) itemView.findViewById(R.id.gameradar_time);
            capacity = (TextView) itemView.findViewById(R.id.gameradar_capacity);
            userlistContainer = (LinearLayout) itemView.findViewById(R.id.gameradar_players_container);
            imageViewtrash = (ImageView) itemView.findViewById(R.id.button_trash);


            moreContainer = (LinearLayout) itemView.findViewById(R.id.gameradar_extra_conainer);
            moreCount = (TextView) itemView.findViewById(R.id.gameradar_extra);


        }
    }

    private String getDateString(Long dateLong) {
        //long val = 1346524199000l;
        Date date = new Date(dateLong);
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM , KK:mm a");
        return df2.format(date);
    }
    private String getTimeString(Long dateLong) {
        //long val = 1346524199000l;
        Date date = new Date(dateLong);
        SimpleDateFormat df2 = new SimpleDateFormat("KK:mm a");
        return df2.format(date);
    }

    public void setGameList(ArrayList<GameRadarGame> gameRadarGamestemp) {
        this.gameRadarGames = gameRadarGamestemp;
    }

    void rotateImage(ImageView imageView) {
        {
            imageView.setRotation(45f);
        }
    }

    private boolean amiInThisGame(GameRadarGame gameRadarGame) {
        String myRollno = Utils.getprefString(Strings.ROLLNO, context).toLowerCase();
        ArrayList<GameRadarUser> gameRadarUsers = gameRadarGame.getPlayers();
        if(gameRadarUsers==null) return false;
        for (GameRadarUser gameRadarUser : gameRadarUsers) {
            if (gameRadarUser.getRollno().toLowerCase()
                    .equals(myRollno))
                return true;
        }
        return false;
    }

    private int getMyPoition(ArrayList<GameRadarUser> players) {
        String myRollno = Utils.getprefString(Strings.ROLLNO, context).toLowerCase();
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).getRollno().toLowerCase().equals(myRollno)) return i;
        return -1;
    }
    public static <T> ArrayList<T> reverse(ArrayList<T> list) {
        int length = list.size();
        ArrayList<T> result = new ArrayList<T>(length);

        for (int i = length - 1; i >= 0; i--) {
            result.add(list.get(i));
        }

        return result;
    }
    public void RemovePost(final GameRadarGame post){
        new AlertDialog.Builder(context)
                .setTitle("Delete post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myFirebaseRef.child("game_radar").child("games").child(post.getId()).removeValue();
                        myFirebaseRef.child("game_radar").child("users").child(post.getAdmin().getRollno())
                                .child("posts").child(post.getId()).removeValue();
                        for(GameRadarUser user:post.getPlayers()){
                            myFirebaseRef.child("game_radar").child("users").child(user.getRollno())
                                    .child("games").child(post.getId()).removeValue();
                        }
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show();

    }
    void ShowDiloge(ArrayList<GameRadarUser> players){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gameradar_players_list_diloge, null);
        RecyclerView  recyclerView = (RecyclerView) view.findViewById(R.id.gameradar_players_lisr_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new GameRadarUserDialogeAdapter(players,context));

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);


        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
   public void BuildNotofication(int mId,String Tittle,String Content,long time){

       NotificationCompat.Builder mBuilder =
               new NotificationCompat.Builder(context)
                       .setSmallIcon(R.drawable.ic_wifi_tethering_black_24dp)
                       .setContentTitle(Tittle)
                       .setContentText(Content);
// Creates an explicit intent for an Activity in your app
       Intent resultIntent = new Intent(context, MainActivity.class);
       resultIntent.putExtra("gameradar",true);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
       TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
       stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
       stackBuilder.addNextIntent(resultIntent);
       PendingIntent resultPendingIntent =
               stackBuilder.getPendingIntent(
                       0,
                       PendingIntent.FLAG_UPDATE_CURRENT
               );
       mBuilder.setContentIntent(resultPendingIntent);
       NotificationManager mNotificationManager =
               (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
       scheduleNotification(mBuilder.build(),time);
       //mNotificationManager.notify(mId, mBuilder.build());
   }
    private void scheduleNotification(Notification notification,long time) {

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = time- Calendar.getInstance().getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
