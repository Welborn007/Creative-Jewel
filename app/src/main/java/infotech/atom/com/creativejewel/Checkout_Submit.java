package infotech.atom.com.creativejewel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class Checkout_Submit extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    ListView addCart;
    private List<AddCart_model> addCartList = new ArrayList<AddCart_model>();
    AddCart_Adapter addCart_adapter;
    AddCart_model addCart_model;
    AppController appcontroller;
    private static final String TAG = "volley error";
    Button send;
    PhotoViewAttacher mAttacher;
    Dialog dialog;
    String username;
    String status;

    private int hot_number = 0;
    private TextView ui_hot = null;
    private int mNotificationsCount = 0;

    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin" ;
    SharedPreferences sharedpreferencesLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout__submit);

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

        addCart = (ListView) findViewById(R.id.listview_cart);
        send = (Button) findViewById(R.id.sendEnquiry);

        for (int i = 0; i < appcontroller.getProductsArraylist().size(); i++) {
            AddCart_model addCart_model = new AddCart_model();
            addCart_model.setItem_name(appcontroller.getProductsArraylist().get(i).getItem_name());
            addCart_model.setVolume(appcontroller.getProductsArraylist().get(i).getVolume());
            addCart_model.setPageno(appcontroller.getProductsArraylist().get(i).getPageno());
            addCart_model.setThumbnail(appcontroller.getProductsArraylist().get(i).getThumbnail());
            addCart_model.setInstructions(appcontroller.getProductsArraylist().get(i).getInstructions());
            addCartList.add(addCart_model);
        }

        addCart_adapter = new AddCart_Adapter(Checkout_Submit.this, addCartList);
        addCart.setAdapter(addCart_adapter);
        addCart_adapter.notifyDataSetChanged();

        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
        username = sharedpreferencesLogin.getString("username", null);
        status = sharedpreferencesLogin.getString("status", null);

        try {

            if (username == null && status == null) {
                Intent intent = new Intent(Checkout_Submit.this, login_screen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Toast.makeText(Checkout_Submit.this, "Please Login", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException npe)
        {

        }

        if (appcontroller.getProductsArraylist().isEmpty())
        {
            Intent intent = new Intent(Checkout_Submit.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            Toast.makeText(Checkout_Submit.this, "Add Items to Cart", Toast.LENGTH_SHORT).show();
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, getItems());

                if(!appcontroller.getProductsArraylist().isEmpty())
                {
                    uploadCartData();
                }
                else {
                    Toast.makeText(Checkout_Submit.this, "Cart is Empty!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void NetworkOpen() {

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

    private String getItems()
    {
        JSONObject dataObj = new JSONObject();
        try
        {
            sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
            String username = sharedpreferencesLogin.getString("username",null);

            dataObj.putOpt("username", username);

            JSONArray cartItemsArray = new JSONArray();
            JSONObject cartItemsObjedct;
            for (int i = 0; i < addCartList.size(); i++)
            {
                cartItemsObjedct = new JSONObject();
                cartItemsObjedct.putOpt("Volume", addCartList.get(i).getVolume());
                cartItemsObjedct.putOpt("Pageno",addCartList.get(i).getPageno());
                cartItemsObjedct.putOpt("Items",addCartList.get(i).getItem_name());
                cartItemsObjedct.putOpt("Instructions",addCartList.get(i).getInstructions());
                cartItemsArray.put(cartItemsObjedct);
            }

            dataObj.put("cartItems", cartItemsArray);

        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dataObj.toString();
    }

    private void uploadCartData(){
        try{

            // custom dialog
            dialog = new Dialog(Checkout_Submit.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_enquiry.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i(TAG, s.toString());
                            dialog.dismiss();

                            if(s.contains("Enquiry successfuly entered in DB"))
                            {
                                Toast.makeText(Checkout_Submit.this, "Enquiry Sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Checkout_Submit.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                                appcontroller.removeProductsItems();
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

                    //Adding parameters
                    params.put("enquiry", getItems());

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
                Intent intent = new Intent(Checkout_Submit.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public class AddCart_Adapter extends BaseAdapter {
        private Activity activity;
        private LayoutInflater inflater;
        List<AddCart_model> AddCart_models;
        AppController appcontroller;

        public AddCart_Adapter(Activity activity, List<AddCart_model> AddCart_models) {
            this.activity = activity;
            this.AddCart_models = AddCart_models;
        }

        @Override
        public int getCount() {
            return AddCart_models.size();
        }

        @Override
        public Object getItem(int location) {
            return AddCart_models.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView,
                            final ViewGroup parent) {
            TextView textView_volume, textView_page,textView_item,textView_instructions;
            Button textView_remove;
            ImageView thumb_Image;

            try {

                if (inflater == null)
                    inflater = (LayoutInflater) activity
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (convertView == null)
                    convertView = inflater.inflate(R.layout.activity_cart_item, null);

                textView_volume = (TextView) convertView.findViewById(R.id.textView_volume);
                textView_page = (TextView) convertView.findViewById(R.id.textView_page);
                textView_item = (TextView) convertView.findViewById(R.id.textView_items);
                thumb_Image = (ImageView) convertView.findViewById(R.id.thumb);
                textView_instructions = (TextView) convertView.findViewById(R.id.textView_instructions);
                textView_remove = (Button) convertView.findViewById(R.id.textView_remove);
                textView_remove.setTag(R.id.textView_remove, position);



                AddCart_model addCart_model = AddCart_models.get(position);
                textView_volume.setText(String.valueOf(addCart_model.getVolume()));
                textView_page.setText(String.valueOf(addCart_model.getPageno()));
                textView_item.setText(String.valueOf(addCart_model.getItem_name()));
                textView_instructions.setText(String.valueOf(addCart_model.getInstructions()));

                Picasso.with(activity).load(addCart_model.getThumbnail()).into(thumb_Image);

                thumb_Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AddCart_model addCart_model = AddCart_models.get(position);

                        Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.image_dialog);

                        final ImageView img = (ImageView) dialog.findViewById(R.id.dialog_image);

                        Picasso.with(activity)
                                .load(addCart_model.getThumbnail())
//                                .error(R.drawable.back)
//                                .placeholder(R.drawable.progress_animation)
                                .into(img);

                        img.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                mAttacher = new PhotoViewAttacher(img);
                                return false;
                            }
                        });

                        dialog.show();
                    }
                });

                textView_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag(R.id.textView_remove);

                        showDialogDelete(position);
                    }
                });

            }catch (NullPointerException npe)
            {
                Toast.makeText(activity, "Null pointer exception", Toast.LENGTH_SHORT).show();
            }

            return convertView;
        }

        private void showDialogDelete(final int position) {

            try {

                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);
                builder.setTitle("Remove Item");
                builder.setMessage("Are you sure want to remove this item?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                appcontroller = (AppController) activity.getApplicationContext();
                                appcontroller.removeProducts(position);
                                AddCart_models.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(activity, "Item Removed", Toast.LENGTH_LONG).show();

                                updateNotificationsBadge(appcontroller.getProductsArraylist().size());

                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }catch (NullPointerException npe)
            {
                Toast.makeText(activity, "Null pointer exception", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
