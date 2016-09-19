package infotech.atom.com.creativejewel.FCM;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.Hashtable;
import java.util.Map;

import infotech.atom.com.creativejewel.network.Constants;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin";
    SharedPreferences sharedpreferencesLogin;
    SharedPreferences.Editor editorLogin;
    String token;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        token = FirebaseInstanceId.getInstance().getToken();

        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
        editorLogin = sharedpreferencesLogin.edit();
        editorLogin.putString("token", token);
        editorLogin.apply();

        uploadGCMData(token);

        // TODO: Implement this method to send any registration to your app's servers.
//        sendRegistrationToServer(refreshedToken);
    }


    private void uploadGCMData(final String GCM){
        try{


            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_gcm.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i("success", s.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog

                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    //Creating parameters
                    Map<String,String> params = new Hashtable<String, String>();


                    //Adding parameters
                    params.put("gcm", GCM);

                    Log.i(TAG, params.toString());

                    //returning parameters
                    return params;
                }
            };

            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Adding request to the queue
            requestQueue.add(stringRequest);

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        }catch (NullPointerException npe)
        {
            Toast.makeText(getApplicationContext(), "Null pointer exception", Toast.LENGTH_SHORT).show();
        }
    }
}