package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import in.ac.iitm.students.GameRadarProfileActivity;
import in.ac.iitm.students.Objects.GameRadarUser;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Views.CircleImageView;

/**
 * Created by arunp on 13-Mar-16.
 */
public class GameRadarUserDialogeAdapter extends RecyclerView.Adapter<GameRadarUserDialogeAdapter.ViewHolder> {
    ArrayList<GameRadarUser> players ;
    Context context;
    Gson gson =new Gson();
    public GameRadarUserDialogeAdapter(ArrayList<GameRadarUser> players, Context context) {
        this.players = players;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gameradar_user_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GameRadarUser gameRadarUser =players.get(position);
        Glide.with(context)
                .load(gameRadarUser.getDpurl())
                .centerCrop()
                .crossFade()
                .into(holder.circleImageView);
        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameRadarProfileActivity.class);
                intent.putExtra("data", gson.toJson(gameRadarUser));
                context.startActivity(intent);
            }
        });
        holder.hostal.setText(gameRadarUser.getHostal());
        holder.name.setText(gameRadarUser.getName());

    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,hostal;
        CircleImageView circleImageView;
        public ViewHolder(View itemView) {
            super(itemView);
        name = ((TextView) itemView.findViewById(R.id.gameradar_user_name));
        hostal =((TextView) itemView.findViewById(R.id.gameradar_user_hostal));
        circleImageView =(CircleImageView) itemView.findViewById(R.id.gameradar_user_dp);
        }
    }
}
