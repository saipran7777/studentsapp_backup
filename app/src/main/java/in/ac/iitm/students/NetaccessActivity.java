package in.ac.iitm.students;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.Adapters.UsageRecyclerAdapter;
import in.ac.iitm.students.Objects.HttpUtility;
import in.ac.iitm.students.Objects.Usage;

public class NetaccessActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    ProgressDialog pDialog;
    TextView test;
    EditText rollno, ldap;
    Context context;

    String regid;
    static CookieManager cm = new CookieManager();
    boolean requstGoing = true;
    RecyclerView UsageRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    // SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String TAG = NetaccessActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;

    private GoogleApiClient mGoogleApiClient;

    /**
     * @return Application's version code from the {@code PackageManager}.
     */

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getprefString(String key, Context cont) {
        SharedPreferences pref = cont.getSharedPreferences("MyPref", 1); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString(key, "");
    }

    public static void saveBool(String key, Boolean value) {
        MyApplication myApplication =new MyApplication();

        SharedPreferences pref = myApplication.getContext().getSharedPreferences("MyPref", 1); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

    public static Boolean getBool(String key) {
       // MyApplication myApplication =new MyApplication();
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("MyPref", 1); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        return pref.getBoolean(key, false);

    }

    public static void hideSoftKeyboard(ActionBarActivity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static void createNotification(Context cont) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) cont.getSystemService(ns);

        Notification notification = new Notification(R.drawable.ic_launcher_notif, null, System.currentTimeMillis());
        RemoteViews notificationView = new RemoteViews(cont.getPackageName(), R.layout.notification_layout);


        //the intent that is started when the notification is clicked (works)
        Intent notificationIntent = new Intent(cont, NetaccessActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(cont, 0, notificationIntent, 0);

        notification.contentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        // notification.flags = Notification.VISIBILITY_PUBLIC;
        //  notification.Builder.setVisibility(Notification.VISIBILITY_PUBLIC);

        //this is the intent that is supposed to be called when the button is clicked
        Intent switchIntent = new Intent(MyApplication.getContext(), switchButtonListener.class);
        switchIntent.putExtra("sdgfahg", notificationView);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 0, switchIntent, 0);

        notificationView.setOnClickPendingIntent(R.id.imageButton, pendingSwitchIntent);

        notificationManager.notify(1, notification);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netaccess);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CookieHandler.setDefault(cm);

        UsageRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        UsageRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        UsageRecyclerView.setLayoutManager(layoutManager);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("NETACCESS");
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);

        test = (TextView) findViewById(R.id.test);
        rollno = (EditText) findViewById(R.id.edit_text_rollno);
        ldap = (EditText) findViewById(R.id.edit_text__pass);
        Button approve = (Button) findViewById(R.id.button_login);

        rollno.setText(getprefString("rollnonetaccess", this));
        ldap.setText(getprefString("ldap", this));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (requstGoing) new Login().execute();               //LoginNet();
                // hideSoftKeyboard(NetaccessActivity.this, v);

            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (requstGoing) new Login().execute();
                //LoginNet();

            }
        });
        // Get tracker.

        // createNotification(MyApplication.getContext());

        context = getApplicationContext();
        // Check device for Play Services APK.

        // Updatecheck(this);
        //AdView mAdView = (AdView) findViewById(R.id.adView);
        // AdRequest adRequest = new AdRequest.Builder().build();
        // mAdView.loadAd(adRequest);
        CheckBox Notifi = (CheckBox) findViewById(R.id.notifiation);
        if(getBool("notifcation_login")){
            Notifi.setChecked(false);
        }

        Notifi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    saveBool("notifcation_login", false);
                } else {
                    saveBool("notifcation_login", true);
                }
                NotificationChecker();


            }
        });




    }
    public void NotificationChecker(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            if (!getBool("notifcation_login")&&getBool("have_name")){
                createNotification(MyApplication.getContext());
                Log.d("1","here");
            }
            else {
                String ns = Context.NOTIFICATION_SERVICE;

                NotificationManager nMgr = (NotificationManager) MyApplication.getContext().getSystemService(ns);
                nMgr.cancel(1);
            }
            Log.d("connected", "fucker");
            // Do your workateNotification();
        }
        else {
            Log.d("disconnected","fucker");
            String ns = Context.NOTIFICATION_SERVICE;

            NotificationManager nMgr = (NotificationManager) MyApplication.getContext().getSystemService(ns);
            nMgr.cancel(1);
        }



    }



    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(NetaccessActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(NetaccessActivity.this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    public void saveString(String key, String value) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 1); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static class switchButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "test");
            new LoginNotif().execute();
        }

    }



    private static class LoginNotif extends AsyncTask<String, String, String> {
        String responseBody;
        private String resp;

        @Override
        protected String doInBackground(String... paramso) {
           /* CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);*/
            try {
                URL url1 = new URL("https://netaccess.iitm.ac.in/account/login");
                URL url2 = new URL("https://netaccess.iitm.ac.in/account/approve");

                String requestURL1, requestURL2;
                Map<String, String> params = new HashMap<String, String>();
                requestURL1 = "https://netaccess.iitm.ac.in/account/login";
                requestURL2 = "https://netaccess.iitm.ac.in/account/approve";
                params.put("userPassword", getprefString("ldap", MyApplication.getContext()));
                params.put("userLogin", getprefString("rollnonetaccess", MyApplication.getContext()));
                params.put("duration", "1");
                params.put("approveBtn", "");

                try {
                    HttpUtility.sendPostRequest(requestURL1, params);
                    responseBody = HttpUtility.readMultipleLinesRespone();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    HttpUtility.sendPostRequest(requestURL2, params);
                    responseBody = HttpUtility.readMultipleLinesRespone();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                HttpUtility.disconnect();
            } catch (Exception e) {
                Log.d("", e.getLocalizedMessage());
            }
           /* HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://netaccess.iitm.ac.in/account/login");
            HttpPost httppostapp = new HttpPost("https://netaccess.iitm.ac.in/account/approve");


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userPassword", getprefString("ldap", MyApplication.getContext())));
                nameValuePairs.add(new BasicNameValuePair("userLogin", getprefString("rollnonetaccess", MyApplication.getContext())));
                Log.d("dfg", getprefString("ldap", MyApplication.getContext()));
                List<NameValuePair> nameValuePairsap = new ArrayList<NameValuePair>(2);
                nameValuePairsap.add(new BasicNameValuePair("duration", "1"));
                nameValuePairsap.add(new BasicNameValuePair("approveBtn", null));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httppostapp.setEntity(new UrlEncodedFormEntity(nameValuePairsap));


                // Execute HTTP Post Request
                HttpResponse responseLogin = httpclient.execute(httppost);
                HttpResponse response = httpclient.execute(httppostapp);
                try {
                    responseBody = EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    e.printStackTrace();

                    Log.i("Parse Exception", e + "");
                }
            } catch (ClientProtocolException e) {

                // TODO Auto-generated catch block
            } catch (IOException e) {

                // TODO Auto-generated catch block
            }
*/

            return responseBody;
        }

        @Override
        protected void onPostExecute(String result) {
            Element loginform = null;
            String toast;

            if (responseBody != null) {


                // Elements par = loginform.select("[p]");

                // String name = doc.attr(".alert-success");

                Document doc = Jsoup.parse(responseBody);
                loginform = doc.select("div.alert-success").first();
                if (loginform == null) {
                    loginform = doc.select("div.alert-info").first();
                }
            }
            if (loginform == null) {
                toast = "you are not connected to insti network";
              /*  t.send(new HitBuilders.EventBuilder()
                        .setCategory("Notification Login")
                        .setAction("Fail")
                        .build());*/
            }else if (300 <loginform.text().length()) {
                toast="wrong password ";
            }
            else {
                Vibrator v = (Vibrator) MyApplication.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(60);

                toast = loginform.text();
                /*t.send(new HitBuilders.EventBuilder()
                        .setCategory("Notification Login")
                        .setAction("Success")
                        .build());*/
            }
            Toast.makeText(MyApplication.getContext(), toast,
                    Toast.LENGTH_SHORT).show();

            //new Approveve().execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CookieHandler.setDefault(cm);
            CookieStore cookieStore = cm.getCookieStore();
            //cookieStore.removeAll();
            // Showing progress dialog
            ;
           /* t.send(new HitBuilders.EventBuilder()
                    .setCategory("Notification Login")
                    .setAction("Hit")
                    .build());*/
        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    public static class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("sad", "sad");

            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                if (info.isConnected()) {


                    if (!getBool("notifcation_login")) {
                        createNotification(context);

                    }
                    Log.d("connected", "fucker");
                    // Do your workateNotification();
                    // e.g. To check the Network Name or other info:
                    //WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    // String ssid = wifiInfo.getSSID();
                } else {
                    Log.d("disconnected", "fucker");
                    String ns = Context.NOTIFICATION_SERVICE;

                    NotificationManager nMgr = (NotificationManager) MyApplication.getContext().getSystemService(ns);
                    nMgr.cancel(1);
                }
            }
        }
    }

    private class Login extends AsyncTask<String, String, String> {
        String responseBody;

        @Override
        protected String doInBackground(String... paramso) {


            try {
                URL url1 = new URL("https://netaccess.iitm.ac.in/account/login");
                URL url2 = new URL("https://netaccess.iitm.ac.in/account/approve");

                String requestURL1, requestURL2;
                Map<String, String> params = new HashMap<String, String>();
                requestURL1 = "https://netaccess.iitm.ac.in/account/login";
                requestURL2 = "https://netaccess.iitm.ac.in/account/approve";
                params.put("userPassword", ldap.getText().toString());
                params.put("userLogin", rollno.getText().toString());
                params.put("duration", "1");
                params.put("approveBtn", "");

                try {
                    HttpUtility.sendPostRequest(requestURL1, params);
                    responseBody = HttpUtility.readMultipleLinesRespone();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    saveBool("Network_error", true);

                }
                try {
                    HttpUtility.sendPostRequest(requestURL2, params);
                    responseBody = HttpUtility.readMultipleLinesRespone();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    saveBool("Network_error", true);

                }
                HttpUtility.disconnect();
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            /*try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userPassword", ldap.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("userLogin", rollno.getText().toString()));

                List<NameValuePair> nameValuePairsap = new ArrayList<NameValuePair>(2);
                nameValuePairsap.add(new BasicNameValuePair("duration", "1"));
                nameValuePairsap.add(new BasicNameValuePair("approveBtn", null));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httppostapp.setEntity(new UrlEncodedFormEntity(nameValuePairsap));


                // Execute HTTP Post Request
                HttpResponse responseLogin = httpclient.execute(httppost);
                HttpResponse response = httpclient.execute(httppostapp);
                Log.d("Parse Exception", "" + "");


                try {
                    responseBody = EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    e.printStackTrace();

                    Log.d("Parse Exception", e + "");
                }
            } catch (ClientProtocolException e) {
                saveBool("Network_error", true);

                // TODO Auto-generated catch block
            } catch (IOException e) {
                saveBool("Network_error", true);

                // TODO Auto-generated catch block
            }*/


            return responseBody;
        }


        @Override
        protected void onPostExecute(String result) {
            final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
            requstGoing = true;

            mSwipeRefreshLayout.setRefreshing(false);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (responseBody != null) {
                CookieStore cookieStore = cm.getCookieStore();
                cookieStore.removeAll();
                //Log.d("here",responseBody.toString());
                Document doc = Jsoup.parse(responseBody);
                //Log.d("res",responseBody);
                Element loginform = doc.select("div.alert-success").first();
                test.setTextColor(Color.WHITE);

                if (loginform == null) {
                    loginform = doc.select("div.alert-info").first();
                    test.setTextColor(Color.RED);
                }
                if (loginform==null) {
                    test.setTextColor(Color.RED);
                    test.setText("wrong password ");
                } else {
                    test.setText(loginform.text());
                    saveString("rollno", rollno.getText().toString()); // Storing string
                    saveString("ldap", ldap.getText().toString());
                    saveBool("have_name", true);
                    if (!getBool("notifcation_login")) createNotification(NetaccessActivity.this);
                }
                if (300 <loginform.text().length()) {
                    test.setTextColor(Color.RED);
                    test.setText("wrong password ");
                } else {
                    test.setText(loginform.text());
                    saveString("rollno", rollno.getText().toString()); // Storing string
                    saveString("ldap", ldap.getText().toString());
                    saveBool("have_name", true);

                }
                // Elements par = loginform.select("[p]");

                // String name = doc.attr(".alert-success");
                String usage;
                String link;
                String ip;
                boolean active;
                String date;
                ArrayList<Usage> arrayList=new  ArrayList<Usage>();
                try {

                    Element table = doc.select("table.table").get(0);

                    if(table!=null){
                        Elements rows = table.select("tr");
                        for (int i = 1; i < rows.size(); i++) {
                            Elements data=  rows.get(i).select("td");
                            /*Log.d("---------------------------------------------------------",data.get(1).text());
                            Log.d("ip",data.get(1).text());
                            Log.d("date", data.get(2).text());
                            Log.d("usage",data.get(3).text());*/
                            usage=data.get(3).text();
                            ip=data.get(1).text();
                            date=data.get(2).text();
                            active =(rows.get(i).select("span.label-success").first()!=null);
                            if (active)
                                link="https://netaccess.iitm.ac.in" + rows.get(i).select("a[href]").first().attr("href");
                            else link="";
                            Usage test =new Usage(ip,usage,date,link,active);
                            arrayList.add(test);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UsageRecyclerAdapter adapter =new UsageRecyclerAdapter(NetaccessActivity.this,arrayList,mSwipeRefreshLayout);
                UsageRecyclerView.setAdapter(adapter);


            }
            if (getBool("Network_error")) {

                Toast.makeText(getBaseContext(), "You are not connected to insti network",
                        Toast.LENGTH_SHORT).show();
                saveBool("Network_error", false);
            }
            //new Approveve().execute();
        }

        @Override
        protected void onPreExecute() {
            requstGoing = false;
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(NetaccessActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            // pDialog.show();
            //

            final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
            }

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

}
