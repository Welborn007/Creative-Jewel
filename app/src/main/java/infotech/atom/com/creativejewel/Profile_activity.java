package infotech.atom.com.creativejewel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import infotech.atom.com.creativejewel.app.AppController;
import infotech.atom.com.creativejewel.network.Constants;
import infotech.atom.com.creativejewel.network.FireToast;
import infotech.atom.com.creativejewel.network.NetworkUtils;
import infotech.atom.com.creativejewel.network.NetworkUtilsReceiver;

public class Profile_activity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private NetworkUtilsReceiver networkUtilsReceiver;
    private static final String TAG = "volley error";
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin" ;
    SharedPreferences sharedpreferencesLogin;
    Dialog dialog;
    AppController appcontroller;

    private int hot_number = 0;
    private TextView ui_hot = null;
    private int mNotificationsCount = 0;

    TextView username,fullname,comp_name,designion,address,mob_no,email_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

             /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        appcontroller = (AppController) getApplicationContext();

        updateNotificationsBadge(appcontroller.getProductsArraylist().size());

        username = (TextView) findViewById(R.id.UserName);
        fullname = (TextView) findViewById(R.id.Name);
        comp_name = (TextView) findViewById(R.id.CompanyName);
        designion = (TextView) findViewById(R.id.Designation);
        address = (TextView) findViewById(R.id.Address);
        mob_no = (TextView) findViewById(R.id.Mobile);
        email_id = (TextView) findViewById(R.id.Email);

    }


    @Override
    public void NetworkOpen() {
        uploadProfileData();
    }

    @Override
    public void NetworkClose() {
        if (!NetworkUtils.isNetworkConnectionOn(this)) {
            FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkUtilsReceiver);
        Runtime.getRuntime().gc();
    }

    private void uploadProfileData(){
        try{

            // custom dialog
            dialog = new Dialog(Profile_activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_profile.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i(TAG, s.toString());
                            dialog.dismiss();

                            try{

                                JSONObject resultArray = new JSONObject(s);

                                Log.d("My App", resultArray.toString());

                                JSONArray result = resultArray.getJSONArray("result");

                                // Parsing json
                                for (int i = 0; i < result.length() && result.length() != 0; i++)
                                {
                                    JSONObject resultObject = result.getJSONObject(i);
                                    username.setText(resultObject.getString("username"));
                                    fullname.setText(resultObject.getString("name"));
                                    comp_name.setText(resultObject.getString("company_name"));
                                    designion.setText(resultObject.getString("designation"));
                                    address.setText(resultObject.getString("address"));
                                    mob_no.setText(resultObject.getString("mobile_no"));
                                    email_id.setText(resultObject.getString("email_id"));
                                }

                                }catch (JSONException je)
                            {

                            }


                            if(s.contains("User not found in DB"))
                            {
                                Toast.makeText(Profile_activity.this, "Please Login", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Profile_activity.this,login_screen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            dialog.dismiss();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {



                    //Creating parameters
                    Map<String,String> params = new Hashtable<String, String>();

                    sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                    String username = sharedpreferencesLogin.getString("username",null);

                    //Adding parameters
                    params.put("username", username);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tocart, menu);

        MenuItem item = menu.findItem(R.id.menu_hot);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        infotech.atom.com.creativejewel.util.Utils.setBadgeCount(this, icon, mNotificationsCount);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);


        return true;
    }

    private void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
        switch (item.getItemId()) {

            case R.id.menu_hotlist:
                Intent intent = new Intent(Profile_activity.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case R.id.menu_hot:
                Intent intent1 = new Intent(Profile_activity.this,Checkout_Submit.class);
                startActivity(intent1);
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
