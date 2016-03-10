package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.ac.iitm.students.Objects.GameRadarGame;
import in.ac.iitm.students.R;

/**
 * Created by arunp on 11-Mar-16.
 */
public class GameRadarAdapter extends RecyclerView.Adapter<GameRadarAdapter.ViewHolder>{
    ArrayList<GameRadarGame> gameRadarGames;
    Context context;

    public GameRadarAdapter(ArrayList<GameRadarGame> gameRadarGames, Context context) {
        this.gameRadarGames = gameRadarGames;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gameradar_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return gameRadarGames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
