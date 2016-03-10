package in.ac.iitm.students;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import in.ac.iitm.students.Utils.GameRadarUser;
import in.ac.iitm.students.Utils.ScalingUtilities;
import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

public class GameRadarProfileEditActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    Uri filePath;
    private Bitmap bitmap;
    ImageView imageView;
    Button button;
    int DESIREDWIDTH = 350;
    int DESIREDHEIGHT = 350;
    Firebase myFirebaseRef;
    EditText name, rollno, phonenum, hostal, roomno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_radar_profile_edit);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase(this.getString(R.string.firebaseurl));
        // myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");

        imageView = (ImageView) findViewById(R.id.gameradar_upload);
        button = (Button) findViewById(R.id.gameradar_button);


        name = (EditText) findViewById(R.id.gameradar_name);
        rollno = (EditText) findViewById(R.id.gameradar_rollno);
        phonenum = (EditText) findViewById(R.id.gameradar_phoneno);
        hostal = (EditText) findViewById(R.id.gameradar_hostal);
        roomno = (EditText) findViewById(R.id.gameradar_roomno);

        rollno.setText(Utils.getprefString(Strings.ROLLNO, this));
        rollno.setEnabled(false);
        name.setText(Utils.getprefString(Strings.NAME, this));

        String dpurl = getString(R.string.gameradar_user_dp) +
                Utils.getprefString(Strings.ROLLNO, GameRadarProfileEditActivity.this).toLowerCase() + ".jpg";
        Log.d("dp url",dpurl);
        Glide.with(this).load(dpurl)
                .placeholder(R.drawable.upload_image)
                .into(imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isEmpty(name) || Utils.isEmpty(rollno) || Utils.isEmpty(phonenum) || Utils.isEmpty(hostal) || Utils.isEmpty(roomno)) {
                    Toast.makeText(GameRadarProfileEditActivity.this, "you haven't enterd everything", Toast.LENGTH_LONG).show();
                } else {
                    new upload().execute();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // imageViewButton.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                if (!(bitmap.getWidth() <= DESIREDWIDTH && bitmap.getHeight() <= DESIREDHEIGHT)) {
                    // Part 2: Scale image
                    bitmap = ScalingUtilities.createScaledBitmap(bitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
                }
                //Setting the Bitmap to ImageView
                Glide.with(this).load(filePath).into(imageView);
                // imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class upload extends AsyncTask<String, Void, String> {
        String responseString;
        final ProgressDialog ringProgressDialog = ProgressDialog.show(GameRadarProfileEditActivity.this, "Please wait ...", "Uploading Image ...", true);

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //   imageViewButton.setVisibility(View.VISIBLE);
            //   imageView.setVisibility(View.INVISIBLE);
            String dpimgurl;
            if (responseString != null) {
                try {
                    dpimgurl = (new JSONObject(responseString)).getString("url");
                    String rollnostring = rollno.getText().toString().toLowerCase();
                    Firebase userRef = myFirebaseRef.child("game_radar").child("users").child(rollnostring);
                    GameRadarUser gameRadarUser = new GameRadarUser(name.getText().toString(),
                            rollnostring, phonenum.getText().toString(),
                            hostal.getText().toString(), roomno.getText().toString(),
                            Utils.getprefString(Strings.GCMTOKEN, GameRadarProfileEditActivity.this), dpimgurl);

                    userRef.setValue(gameRadarUser, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            ringProgressDialog.dismiss();
                            Toast.makeText(GameRadarProfileEditActivity.this, "you have successfully created account", Toast.LENGTH_LONG).show();


                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // getImages(v);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();


        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream;

            try {

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bytes = bos.toByteArray();

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(getString(R.string.gameradar_user_dp_upload));


                InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(bytes), "salf");
                MultipartEntity multipartEntity = new MultipartEntity();
                multipartEntity.addPart("fileToUpload", inputStreamBody);
                multipartEntity.addPart("rollno", new StringBody(Utils.getprefString(Strings.ROLLNO, GameRadarProfileEditActivity.this).toLowerCase()));

                httpPost.setEntity(multipartEntity);

                HttpResponse httpResponse = httpClient.execute(httpPost);

                // Handle response back from script.
                if (httpResponse != null) {
                    HttpEntity entity = httpResponse.getEntity();
                    responseString = EntityUtils.toString(entity, "UTF-8");
                    Log.d("response", responseString);
                    //   httpResponse.toString();
                } else { // Error, no response.

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
