package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.content.Intent;
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
import java.util.Date;

import in.ac.iitm.students.GameRadarProfileActivity;
import in.ac.iitm.students.Objects.GameRadarGame;
import in.ac.iitm.students.Objects.GameRadarUser;
import in.ac.iitm.students.R;
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

    public GameRadarAdapter(ArrayList<GameRadarGame> gameRadarGames, Context context) {
        this.gameRadarGames = gameRadarGames;
        this.context = context;
        myFirebaseRef = new Firebase(context.getString(R.string.firebaseurl));
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

    }

    @Override
    public int getItemCount() {
        return gameRadarGames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView adminDP, useradd;
        TextView game, time, location, capacity;
        LinearLayout userlistContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            adminDP = (CircleImageView) itemView.findViewById(R.id.gameradar_admin_dp);
            useradd = (CircleImageView) itemView.findViewById(R.id.gameradar_button_add);
            game = (TextView) itemView.findViewById(R.id.gameradar_game);
            location = (TextView) itemView.findViewById(R.id.gameradar_location);
            time = (TextView) itemView.findViewById(R.id.gameradar_time);
            capacity = (TextView) itemView.findViewById(R.id.gameradar_capacity);
            userlistContainer = (LinearLayout) itemView.findViewById(R.id.gameradar_players_container);

        }
    }

    private String getDateString(Long dateLong) {
        //long val = 1346524199000l;
        Date date = new Date(dateLong);
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM , KK:mm a");
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
}
