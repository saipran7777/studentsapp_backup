package in.ac.iitm.students;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import in.ac.iitm.students.Objects.GameRadarGame;
import in.ac.iitm.students.Objects.GameRadarUser;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

public class GameRadarProfileActivity extends AppCompatActivity {
    Gson gson =new Gson();
    GameRadarUser gameRadarUser ;
    EditText  rollno, phonenum, hostal, roomno;
    TextView name;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_radar_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent i= getIntent();
        String data;

        imageView = (ImageView) findViewById(R.id.gameradar_upload);
        name = (TextView) findViewById(R.id.gameradar_name);
        rollno = (EditText) findViewById(R.id.gameradar_rollno);
        phonenum = (EditText) findViewById(R.id.gameradar_phoneno);
        hostal = (EditText) findViewById(R.id.gameradar_hostal);
        roomno = (EditText) findViewById(R.id.gameradar_roomno);
        if (i.getExtras() != null) {
            data = i.getExtras().getString("data");
            gameRadarUser =gson.fromJson(data,GameRadarUser.class);
            name.setText(gameRadarUser.getName());
            rollno.setText(gameRadarUser.getRollno());
            phonenum.setText(gameRadarUser.getPhoneno());
            hostal.setText(gameRadarUser.getHostal());
            roomno.setText(gameRadarUser.getRoomno());
            Glide.with(this)
                    .load(gameRadarUser.getDpurl())
                    .centerCrop()
                    .into(imageView);

        }


    }
}
