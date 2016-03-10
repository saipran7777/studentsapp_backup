package in.ac.iitm.students.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by arunp on 11-Mar-16.
 */
public class GameRadarAdapter extends RecyclerView.Adapter<GameRadarAdapter.ViewHoldr>{
    

    @Override
    public ViewHoldr onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHoldr holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHoldr extends RecyclerView.ViewHolder{

        public ViewHoldr(View itemView) {
            super(itemView);
        }
    }
}
