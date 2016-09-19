package infotech.atom.com.creativejewel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import infotech.atom.com.creativejewel.app.AppController;
import infotech.atom.com.creativejewel.network.Constants;
import infotech.atom.com.creativejewel.network.FireToast;
import infotech.atom.com.creativejewel.network.NetworkUtils;
import infotech.atom.com.creativejewel.network.NetworkUtilsReceiver;
import uk.co.senab.photoview.PhotoViewAttacher;

public class Order_listing extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    private int mNotificationsCount = 0;
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin";
    SharedPreferences sharedpreferencesLogin;
    Dialog dialog;
    ListView orders;
    private static final String TAG = "volley error";
    AppController appcontroller;
    private List<Orders_model> worldpopulationlist = new ArrayList<Orders_model>();;

    Listing_Adapter listing_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_listing);


        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

             /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        orders = (ListView) findViewById(R.id.listview_listing);

        listing_adapter = new Listing_Adapter(Order_listing.this,worldpopulationlist);
        orders.setAdapter(listing_adapter);

        appcontroller = (AppController) getApplicationContext();
        updateNotificationsBadge(appcontroller.getProductsArraylist().size());

    }

    @Override
    public void NetworkOpen() {

        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
        String username = sharedpreferencesLogin.getString("username", null);
        String status = sharedpreferencesLogin.getString("status", null);

        try {
            if (username != null && status != null) {
                uploadImage();
            } else {
                Toast.makeText(Order_listing.this, "Please Login", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException npe) {

        }

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


    private void uploadImage() {
        try {
            // custom dialog
            dialog = new Dialog(Order_listing.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_list.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i(TAG, s.toString());
                            dialog.dismiss();

                            setUserData(s);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            dialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                    String username = sharedpreferencesLogin.getString("username", null);

                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();

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

        } catch (NullPointerException npe) {
            Toast.makeText(getApplicationContext(), "Null pointer exception", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }


    private void setUserData(String s) {
        try {

                JSONObject resultArray = new JSONObject(s);

                Log.d("My App", resultArray.toString());

                JSONArray result = resultArray.getJSONArray("result");

            if (result.length() == 0 && result == null)
            {
                Toast.makeText(Order_listing.this, "No Order Scheduled", Toast.LENGTH_SHORT).show();

            }
            else{
                // Parsing json
                for (int i = 0; i < result.length() && result.length() != 0; i++) {

                    JSONObject resultObject = result.getJSONObject(i);

                    String Created_Date = resultObject.getString("created_date");
                    Created_Date = Created_Date.replace("N/A","");
                    String New_Orders = resultObject.getString("new_arrival");
                    New_Orders = New_Orders.replace("N/A","");
                    String Order_nos = resultObject.getString("order_no");
                    Order_nos = Order_nos.replace("N/A","");
                    String volume1 = resultObject.getString("volume1");
                    volume1 = volume1.replace("N/A","");
                    String volume2 = resultObject.getString("volume2");
                    volume2 = volume2.replace("N/A","");
                    String volume3 = resultObject.getString("volume3");
                    volume3 = volume3.replace("N/A","");


                    Orders_model ordersModel = new Orders_model();

                    ordersModel.setCreated_date(Created_Date);
                    ordersModel.setNew_arrivals(New_Orders);
                    ordersModel.setOrder_no(Order_nos);
                    ordersModel.setVolume1(volume1);
                    ordersModel.setVolume2(volume2);
                    ordersModel.setVolume3(volume3);

                    worldpopulationlist.add(ordersModel);

                }

                listing_adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
                Intent intent = new Intent(Order_listing.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.menu_hot:
                Intent intent1 = new Intent(Order_listing.this, Checkout_Submit.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }



}
