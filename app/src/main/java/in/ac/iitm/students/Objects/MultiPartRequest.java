package in.ac.iitm.students.Objects;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arunp on 31-Jan-16.
 */
public class MultiPartRequest extends JsonRequest<JSONObject> {

    /* To hold the parameter name and the File to upload */
    private Map<String,File> fileUploads = new HashMap<String,File>();

    /* To hold the parameter name and the string content to upload */
    private Map<String,String> stringUploads = new HashMap<String,String>();

    public MultiPartRequest(String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, requestBody, listener, errorListener);
    }

    public void addFileUpload(String param,File file) {
        fileUploads.put(param,file);
    }

    public void addStringUpload(String param,String content) {
        stringUploads.put(param,content);
    }

    public Map<String,File> getFileUploads() {
        return fileUploads;
    }

    public Map<String,String> getStringUploads() {
        return stringUploads;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        return null;
    }
}