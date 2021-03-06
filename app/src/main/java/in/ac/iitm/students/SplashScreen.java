package in.ac.iitm.students;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        new CountDownTimer(1000, 1000) {
            public void onFinish() {
                Intent startActivity =  new Intent(SplashScreen.this,LoginActivity.class);
                if (Utils.getprefBool(Strings.LOGEDIN,SplashScreen.this)){
                    startActivity = new Intent(SplashScreen.this,MainActivity.class);
                }
                startActivity(startActivity);
                finish();
            }

            public void onTick(long millisUntilFinished) {
            }

        }.start();

    }

}
