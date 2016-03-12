package in.ac.iitm.students.Utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import in.ac.iitm.students.R;

/**
 * Created by arunp on 12-Mar-16.
 */
public class GcmUtils {
    public static void requestWithSomeHttpHeaders(final Context context, final String[] gcmid, final String type, final String massage) {
        final Gson gson = new Gson();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://gcm-http.googleapis.com/gcm/send";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key="+context.getString(R.string.gcmserverkey));
                params.put("Content-Type", "application/json");

                return params;
            }
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {

                GCM gcm =new GCM(new data(massage,type),gcmid);
                String str = gson.toJson(gcm);
                Log.d("gcm sent json",str);
                return str.getBytes();
            };
        };
        queue.add(postRequest);

    }
    private static class GCM {
        public GCM(data data, String[] to) {
            this.data = data;
            this.registration_ids = to;
        }

        data data;
        String[] registration_ids;

    }
    private static class data{
        public data(String message,String type) {
            this.message = message;
            this.type =type;
        }

        String type,message;

    }
}
