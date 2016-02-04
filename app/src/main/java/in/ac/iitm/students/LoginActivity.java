package in.ac.iitm.students;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.Utils.Strings;
import in.ac.iitm.students.Utils.Utils;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog progress;
    LinearLayout signin, ldaplogin;
    EditText username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ldaplogin = (LinearLayout) findViewById(R.id.ldaplogin);
        username = (EditText) this.findViewById(R.id.rollno);
        password = (EditText) this.findViewById(R.id.password);
        password.setTypeface(Typeface.DEFAULT_BOLD);
        username.setTypeface(Typeface.DEFAULT_BOLD);
        ldaplogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().trim().length() > 0&&password.getText().toString().trim().length()>0){
                    if(Utils.isNetworkAvailable(getBaseContext())){
                        progress = new ProgressDialog(LoginActivity.this);
                        progress.setCancelable(false);
                        progress.setMessage("Logging In...");
                        progress.show();
                        PlacementLdaplogin(getBaseContext());
                    }else {
                        MakeSnSnackbar("No internet connection");
                    }

                }else{
                    MakeSnSnackbar("Enter your username and password");
                }

            }
        });
    }
    private void PlacementLdaplogin(final Context context) {

        final String[] message = new String[1];
        final String[] DisplayName = new String[1];
        final String[] Hostel = new String[1];
        final int[] success = new int[1];
        final JSONObject[] responseJson = new JSONObject[1];

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getString(R.string.LoginURl),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String responseBody = response.toString();
                      //  Log.d("login", responseBody);

                        //responseBody = responseBody.replaceAll("\\s", "");
                        try {
                            responseJson[0] =new JSONObject(responseBody);
                            success[0] = responseJson[0].getInt("success");
                            message[0] = responseJson[0].getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if (success[0]==1) {
                            JSONObject jsonResultObjuct = null;
                            try {
                                jsonResultObjuct = responseJson[0].getJSONArray("result").getJSONObject(0);
                                DisplayName[0] =jsonResultObjuct.getString("fullname");
                                Hostel[0] =jsonResultObjuct.getString("hostel");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                           // Log.d("valid login", responseBody + "ok");
                            Intent downloadIntent;
                            downloadIntent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(downloadIntent);
                            Utils.saveprefString(Strings.NAME,DisplayName[0], getBaseContext());
                            Utils.saveprefString(Strings.HOSTEl,  Hostel[0], getBaseContext());
                            Utils.saveprefString(Strings.ROLLNO, username.getText().toString().toUpperCase(), getBaseContext());
                            Utils.saveprefBool(Strings.LOGEDIN, true, context);

                            finish();
                        } else if (success[0]==0) {
                            MakeSnSnackbar(message[0]);
                            Log.d("invalid login", responseBody + "Error connecting to server !!");
                            Utils.clearpref(context);
                        } else {
                            MakeSnSnackbar("Error connecting to server !!");
                            Log.d("invalid login", responseBody + "Error connecting to server !!");
                            Utils.clearpref(context);
                        }
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MakeSnSnackbar("Error connecting to server !!");
                Log.d("invalid login", error.toString() + "Error connecting to server !!");
                Utils.clearpref(context);
                progress.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("roll", username.getText().toString());
                params.put("pass", password.getText().toString());
                return params;
            }

        };
        queue.add(stringRequest);
    }

    public void MakeSnSnackbar(String text){
        hideKeyboard();
        Snackbar snack=Snackbar.make((CoordinatorLayout) findViewById(R.id.container), text, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snack.getView();
        group.setBackgroundColor(Color.WHITE);
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof TextView) {
                TextView t = (TextView) v;
                t.setTextColor(Color.RED);
                t.setTextSize(17);
            }
        }
        snack.show();
    }
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
