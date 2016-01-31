package in.ac.iitm.students.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import com.nostra13.universalimageloader.core.ImageLoader;

import in.ac.iitm.students.Adapters.GalleryAdapter;
import in.ac.iitm.students.Adapters.NewPauseOnScrollListener;
import in.ac.iitm.students.MainActivity;
import in.ac.iitm.students.Objects.GridImage;
import in.ac.iitm.students.Objects.IOUtil;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Utils;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    RecyclerView mRecyclerView;
    GalleryAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    View v;
    CardView myView;
    ImageView imageViewButton,imageView;
    private int PICK_IMAGE_REQUEST = 1;
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private Bitmap bitmap;
    Uri filePath;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       v = inflater.inflate(R.layout.fragment_gallery, container, false);
         mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.contentView);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImages(v);
            }
        });
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) v.findViewById(R.id.hot_fragment_recycler);
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new NewPauseOnScrollListener(ImageLoader.getInstance(), true, true));
        final FloatingActionButton fabaddImage =(FloatingActionButton) v.findViewById(R.id.fabadd);
        final FloatingActionButton fabtic =(FloatingActionButton) v.findViewById(R.id.fabtic);
        imageViewButton =(ImageView) v.findViewById(R.id.uploadB);
        imageView =(ImageView) v.findViewById(R.id.image);

        fabtic.hide();
        myView =(CardView) v.findViewById(R.id.cardView);


        final SupportAnimator[] animator = new SupportAnimator[1];
        fabaddImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fabaddImage.hide();
                fabtic.show();
                myView.setVisibility(View.VISIBLE);
                // get the center for the c   lipping circle
                int cx = (myView.getLeft() + myView.getRight()) / 2;
                int cy = (myView.getTop() + myView.getBottom()) / 2;

                // get the final radius for the clipping circle
                int dx = Math.max(cx, myView.getWidth() - cx);
                int dy = Math.max(cy, myView.getHeight() - cy);
                float finalRadius = (float) Math.hypot(dx, dy);
                animator[0] =
                        ViewAnimationUtils.createCircularReveal(myView, myView.getWidth(),myView.getHeight(), 0, myView.getHeight());
                animator[0].setInterpolator(new AccelerateDecelerateInterpolator());
                animator[0].setDuration(700);
                animator[0].start();            }
        });
        fabtic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                fabaddImage.show();
                fabtic.hide();
                animator[0] = ViewAnimationUtils.createCircularReveal(myView, myView.getWidth(),
                        myView.getHeight(), 2*myView.getHeight(), 0);
                animator[0].setDuration(700);
                animator[0].start();

                animator[0].addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                        animator[0] = null;
                        myView.setVisibility(myView.INVISIBLE);
                        if (filePath ==null){
                            Snackbar.make(v, "You haven't selected file to upload", Snackbar.LENGTH_LONG);

                        }else {
                            new  upload().execute();
                        }
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });


            }
        });
        imageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        new CountDownTimer(300, 1000) {
            public void onFinish() {
                getImages(v);
            }

            public void onTick(long millisUntilFinished) {
            }

        }.start();

        return v;
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageViewButton.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                 Glide.with(getActivity()).load(filePath).into(imageView);
               // imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public ArrayList<GridImage> getImages(final View view) {
        String url =getString(R.string.url_images);
        final ArrayList<GridImage> images =new ArrayList<GridImage>();
        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        if (!mSwipeRefreshLayout.isRefreshing()) {
            Log.d("refreshing", "refresh");
            mSwipeRefreshLayout.setRefreshing(true);
        }
        final JSONArray[] jsonArray = {null};


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

               if(response!=null){
                   try {
                       jsonArray[0] =response.getJSONArray("images");
                   } catch (JSONException e) {
                       e.printStackTrace();

                   }
                   Log.d("Log", response.toString());
                   Utils.saveprefString("imgrespons", response.toString(),getActivity());
               }else{
                   try {
                       JSONObject jsonObject=new JSONObject(Utils.getprefString("imgrespons",getActivity()));
                       jsonArray[0] =jsonObject.getJSONArray("images");
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

               }
                for (int i =0;i< jsonArray[0].length();i++){
                    JSONObject jo = null;
                    try {
                        jo = jsonArray[0].getJSONObject(i);
                        images.add(new GridImage(jo.getString("name"),jo.getString("rollno")));
                        Log.d("Log", jo.getString("flag"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mAdapter = new GalleryAdapter(getActivity(), images);
                mRecyclerView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // VolleyLog.d(TAG, "Error: " + error.getMessage());
                Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)
                        .show();

                    try {
                        JSONObject jsonObject=new JSONObject(Utils.getprefString("imgrespons",getActivity()));
                        jsonArray[0] =jsonObject.getJSONArray("images");
                        for (int i =0;i< jsonArray[0].length();i++) {
                            JSONObject jo = null;
                            jo = jsonArray[0].getJSONObject(i);
                            images.add(new GridImage(jo.getString("name"), jo.getString("rollno")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                mAdapter = new GalleryAdapter(getActivity(), images);
                mRecyclerView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);


            }
        });
        queue.add(jsonObjReq);


        return images;
    }
    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.ImageUploadUrl),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Log.d("response",s);
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                try {
                    bitmap = Glide.
                            with(getActivity()).
                            load(filePath).
                            asBitmap().
                            into(100, 100). // Width and height
                            get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String image = getStringImage(bitmap);
                File myFile = new File(String.valueOf(filePath));

                //Getting Image Name
               // String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("fileToUpload", "kk");
                params.put("submit", "hh");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void uploadFile(Uri filePath, String fileName) {
       InputStream inputStream;
        //  getActivity().getContentResolver().openInputStream(filePath);

        // inputStream = new FileInputStream(new File(filePath));
        //  Log.d("file path",filePath);
        // byte[] data=IOUtil.readFile(filePath);
        try {
          // data = IOUtils.toByteArray(inputStream);
            inputStream = getActivity().getContentResolver().openInputStream(filePath);;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead =inputStream.read(b);
            while ( bytesRead  != -1) {
                bos.write(b, 0, bytesRead);
                bytesRead= inputStream.read(b);
            }
            byte[] bytes = bos.toByteArray();


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(getString(R.string.ImageUploadUrl));

            InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(bytes), fileName);
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("fileToUpload", inputStreamBody);
            httpPost.setEntity(multipartEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);

            // Handle response back from script.
            if(httpResponse != null) {
                Log.d("response",httpResponse.toString());
             //   httpResponse.toString();
            } else { // Error, no response.

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  class upload extends AsyncTask<String, Void, String>{
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",	"Uploading Image ...", true);
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            imageViewButton.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            ringProgressDialog.dismiss();
            getImages(v);
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
            //  getActivity().getContentResolver().openInputStream(filePath);

            // inputStream = new FileInputStream(new File(filePath));
            //  Log.d("file path",filePath);
            // byte[] data=IOUtil.readFile(filePath);
            try {
                // data = IOUtils.toByteArray(inputStream);
                inputStream = getActivity().getContentResolver().openInputStream(filePath);;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int bytesRead =inputStream.read(b);
                while ( bytesRead  != -1) {
                    bos.write(b, 0, bytesRead);
                    bytesRead= inputStream.read(b);
                }
                byte[] bytes = bos.toByteArray();


                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(getString(R.string.ImageUploadUrl));

                InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(bytes), "salf");
                MultipartEntity multipartEntity = new MultipartEntity();
                multipartEntity.addPart("fileToUpload", inputStreamBody);
                httpPost.setEntity(multipartEntity);

                HttpResponse httpResponse = httpClient.execute(httpPost);

                // Handle response back from script.
                if(httpResponse != null) {
                    HttpEntity entity = httpResponse.getEntity();
                    String responseString = EntityUtils.toString(entity, "UTF-8");
                    Log.d("response",responseString);
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
