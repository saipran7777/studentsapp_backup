package in.ac.iitm.students.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ac.iitm.students.Adapters.GameRadarAdapter;
import in.ac.iitm.students.GameRadarProfileEditActivity;
import in.ac.iitm.students.MainActivity;
import in.ac.iitm.students.Objects.GameRadarGame;
import in.ac.iitm.students.Objects.GameRadarUser;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameradarFragment extends Fragment {
    GameRadarUser gameRadarUser;
    RecyclerView recyclerView;


    public GameradarFragment() {
        // Required empty public constructor
    }
    View v;
    Context context;
    Firebase myFirebaseRef;
    final Gson gson = new Gson();
    ArrayList<GameRadarGame> gameRadarGames;
    GameRadarAdapter gameRadarAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =inflater.inflate(R.layout.fragment_gameradar, container, false);
        context = getActivity();
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase(context.getString(R.string.firebaseurl));
        if(Utils.getprefString(Strings.GAMERADARUSER,context)==""){
            Intent intent = new Intent(getActivity(), GameRadarProfileEditActivity.class);
            context.startActivity(intent);
            getActivity().finish();
        }
        gameRadarUser = gson.fromJson(Utils.getprefString(Strings.GAMERADARUSER,context),GameRadarUser.class);
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

        recyclerView = (RecyclerView) v.findViewById(R.id.gameradar_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        gameRadarGames = new ArrayList<GameRadarGame>();
        gameRadarAdapter =new GameRadarAdapter(gameRadarGames,context);
        recyclerView.setAdapter(gameRadarAdapter);

        Firebase gameRef =myFirebaseRef.child("game_radar").child("games");
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("Aqel", snapshot.getValue().toString());
                gameRadarGames.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    GameRadarGame game = postSnapshot.getValue(GameRadarGame.class);
                    game.setId(postSnapshot.getKey());
                    Log.d("key" ,postSnapshot.getKey());
                    gameRadarGames.add(game);
                }
              gameRadarAdapter.setGameList(gameRadarGames);
                gameRadarAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

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
}
