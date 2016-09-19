package infotech.atom.com.creativejewel;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import infotech.atom.com.creativejewel.app.AppController;
import infotech.atom.com.creativejewel.network.Constants;
import infotech.atom.com.creativejewel.network.FireToast;
import infotech.atom.com.creativejewel.network.NetworkUtils;
import infotech.atom.com.creativejewel.network.NetworkUtilsReceiver;

public class HomeActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    private boolean _doubleBackToExitPressedOnce = false;
    AppController appcontroller;
    String username;
    String status;
    private int mNotificationsCount = 0;
    Button v1, v2, v3, New;
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin";
    SharedPreferences sharedpreferencesLogin;
    SharedPreferences.Editor editorLogin;
    private static final String TAG = "UserStatus";
    MenuItem itemNew,item_Stock;

    String User_Status = "";
    RelativeLayout Cust_Orders, Book_Shelf, Login, Register, Profile, HowToUse, Call_us, Contact_Us, Logout;

    // Define objects
    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    boolean mSlideState = false;
    ImageView ic_image;

    private List<Integer> imageIdList;
    AutoScrollViewPager viewpager_default;
    private List<Data_Content> worldpopulationlist = null;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    List<String> permissionsNeeded,permissionsList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBar.showOverflowMenu();

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            permissionCheck();
        } else {
            // Pre-Marshmallow
        }

         /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        try {

            appcontroller = (AppController) getApplicationContext();

            updateNotificationsBadge(appcontroller.getProductsArraylist().size());

            //Binding drawer items with objects
            mDrawerList = (LinearLayout) findViewById(R.id.left_drawer);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            ic_image = (ImageView) findViewById(R.id.ic);

            Login = (RelativeLayout) findViewById(R.id.cust_login);
            Register = (RelativeLayout) findViewById(R.id.cust_Register);
            Profile = (RelativeLayout) findViewById(R.id.cust_Profile);
            Contact_Us = (RelativeLayout) findViewById(R.id.contactus);
            Logout = (RelativeLayout) findViewById(R.id.cust_Logout);
            Book_Shelf = (RelativeLayout) findViewById(R.id.shelf);
            Cust_Orders = (RelativeLayout) findViewById(R.id.cust_listing);
            HowToUse = (RelativeLayout) findViewById(R.id.howtouse);
            Call_us = (RelativeLayout) findViewById(R.id.Callus);

            mTitle = mDrawerTitle = getTitle();

            mDrawerToggle = new ActionBarDrawerToggle(this,
                    mDrawerLayout,
                    actionBar,
                    0,
                    0) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    mSlideState = false;//is Closed
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    mSlideState = true;//is Opened
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);

            ic_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSlideState) {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                }
            });

            try {

                sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                username = sharedpreferencesLogin.getString("username", null);
                status = sharedpreferencesLogin.getString("status", null);

                if (username != null && status != null) {

                    if (!username.isEmpty() && status.contains("Login Successful")) {
                        Login.setVisibility(View.GONE);
                        Register.setVisibility(View.GONE);
                        getStatusData();
                    }
                } else {
                    Logout.setVisibility(View.GONE);
                    Profile.setVisibility(View.GONE);
                }

            } catch (NullPointerException npe) {

            }

            Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, login_screen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            HowToUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, How_to_use.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            Contact_Us.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, Contact_Us.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            Call_us.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setAction("android.intent.action.DIAL");
                    callIntent.setData(Uri.parse("tel:" + "9819118868"));
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        return;
                    }
                    startActivity(callIntent);
                }
            });

            Cust_Orders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this,Order_listing.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            Profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this,Profile_activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            Logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HomeActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                    sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                    editorLogin = sharedpreferencesLogin.edit();
                    editorLogin.clear();
                    editorLogin.apply();

                    appcontroller.removeProductsItems();

                    Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

            imageIdList = new ArrayList<Integer>();
            imageIdList.add(R.drawable.banner1);
            imageIdList.add(R.drawable.banner2);
//            imageIdList.add(R.drawable.banner3);
            imageIdList.add(R.drawable.banner4);
            imageIdList.add(R.drawable.banner5);
            //imageIdList.add(R.drawable.banner6);
            //imageIdList.add(R.drawable.banner7);
            imageIdList.add(R.drawable.banner8);
            //imageIdList.add(R.drawable.banner9);
            //imageIdList.add(R.drawable.banner10);
            //imageIdList.add(R.drawable.banner11);
            //imageIdList.add(R.drawable.banner12);
            //imageIdList.add(R.drawable.banner13);
//            imageIdList.add(R.drawable.banner14);
            //imageIdList.add(R.drawable.banner15);
            //imageIdList.add(R.drawable.banner16);

            viewpager_default = (AutoScrollViewPager) findViewById(R.id.viewpager1);
            viewpager_default.setAdapter(new ImagePagerAdapter(HomeActivity.this, imageIdList).setInfiniteLoop(true));
            viewpager_default.startAutoScroll();
            viewpager_default.setInterval(2000);
            viewpager_default.getAdapter().notifyDataSetChanged();
            viewpager_default.setCycle(false);

            worldpopulationlist = new ArrayList<Data_Content>();

            v1 = (Button) findViewById(R.id.volume1);
            v2 = (Button) findViewById(R.id.volume2);
            v3 = (Button) findViewById(R.id.volume3);

            v1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Book_Shelf.setVisibility(View.GONE);

//                    getNotificationData();

                    Intent intent = new Intent(HomeActivity.this, Category_Listing.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("volume","volume1");
                    startActivity(intent);



                }
            });

            v2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    getNotificationDataone();

                    try {

                        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                        String username = sharedpreferencesLogin.getString("username", null);
                        String status = sharedpreferencesLogin.getString("status", null);

                        if (username != null && status != null) {
                            Intent intent = new Intent(HomeActivity.this, Category_Listing.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("volume","volume2");
                            startActivity(intent);

                        } else if (username == null && status == null) {
                            Toast.makeText(HomeActivity.this, "Please Login to view products", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(HomeActivity.this, login_screen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    }catch (NullPointerException npe)
                    {

                    }
                }
            });

            v3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                        String username = sharedpreferencesLogin.getString("username", null);
                        String status = sharedpreferencesLogin.getString("status", null);

                        if (username != null && status != null) {

                            Intent intent = new Intent(HomeActivity.this, Category_Listing.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("volume","volume3");
                            startActivity(intent);

                        } else if (username == null && status == null) {
                            Toast.makeText(HomeActivity.this, "Please Login to view products", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(HomeActivity.this, login_screen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    }catch (NullPointerException npe)
                    {

                    }
                }
            });

//            New.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
//                        String username = sharedpreferencesLogin.getString("username", null);
//                        String status = sharedpreferencesLogin.getString("status", null);
//
//                        if (username != null && status != null) {
//
//                            Intent intent1 = new Intent(HomeActivity.this, Category_Listing.class);
//                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent1.putExtra("volume","new_arrival");
//                            startActivity(intent1);
//
//                        } else if (username == null && status == null) {
//                            Toast.makeText(HomeActivity.this, "Please Login to view products", Toast.LENGTH_SHORT).show();
//                            Intent intent1 = new Intent(HomeActivity.this, login_screen.class);
//                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent1);
//                        }
//
//                    }catch (NullPointerException npe)
//                    {
//
//                    }
//
//                }
//            });

        }catch (Exception e)
        {

        }



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
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {


        if (_doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this._doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to quit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                _doubleBackToExitPressedOnce = false;

            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.menu_hot);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        itemNew = menu.findItem(R.id.new_arrival);
        /*item_Stock = menu.findItem(R.id.ready_stock);*/
//        if(User_Status.contains("Active"))
//        {
//            itemNew.setEnabled(true);
//        }
//
//        if(User_Status.contains("InActive"))
//        {
//            itemNew.setEnabled(false);
//        }

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

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.new_arrival:
                try {
                    sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                    String username = sharedpreferencesLogin.getString("username", null);
                    String status = sharedpreferencesLogin.getString("status", null);
                    String user_status = sharedpreferencesLogin.getString("user_status", null);

                    if (username != null && status != null) {

                        if(user_status.contains("Active"))
                        {
                            Intent intent1 = new Intent(HomeActivity.this, Category_Listing.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent1.putExtra("volume","new_arrival");
                            startActivity(intent1);
                        }

                    } else if (username == null && status == null) {

                            Toast.makeText(HomeActivity.this, "Please Login to view products", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(HomeActivity.this, login_screen.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                    }

                }catch (NullPointerException npe)
                {

                }
                return true;

            /*case R.id.ready_stock:
                try {
                    sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                    String username = sharedpreferencesLogin.getString("username", null);
                    String status = sharedpreferencesLogin.getString("status", null);
                    String user_status = sharedpreferencesLogin.getString("user_status", null);

                    if (username != null && status != null) {

                        if(user_status.contains("Active"))
                        {
                            Intent intent1 = new Intent(HomeActivity.this, Category_Listing.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent1.putExtra("volume","ready_stock");
                            startActivity(intent1);
                        }

                    } else if (username == null && status == null) {

                        Toast.makeText(HomeActivity.this, "Please Login to view products", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(HomeActivity.this, login_screen.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                    }

                }catch (NullPointerException npe)
                {

                }
                return true;*/

            case R.id.menu_hot:
                Intent intent1 = new Intent(HomeActivity.this,Checkout_Submit.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public class ImagePagerAdapter extends RecyclingPagerAdapter {

        private Context       context;
        private List<Integer> imageIdList;

        private int           size;
        private boolean       isInfiniteLoop;

        public ImagePagerAdapter(Context context, List<Integer> imageIdList) {
            this.context = context;
            this.imageIdList = imageIdList;
            this.size = ListUtils.getSize(imageIdList);
            isInfiniteLoop = false;
        }

        @Override
        public int getCount() {
            // Infinite loop
            return isInfiniteLoop ? Integer.MAX_VALUE : ListUtils.getSize(imageIdList);
        }

        /**
         * get really position
         *
         * @param position
         * @return
         */
        private int getPosition(int position) {
            return isInfiniteLoop ? position % size : position;
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = holder.imageView = new ImageView(context);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }

//            Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
//                    imageIdList.get(getPosition(position)));
//            holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 100, false));
//
//            Picasso.with( context )
//                    .load( worldpopulationlist.get(position).getFlag() )
//                    .error( R.drawable.ic_error )
//                    .placeholder( R.drawable.progress_animation)
//                    .into(holder.imageView);

            Picasso.with(context).load(imageIdList.get(getPosition(position))).fit().into(holder.imageView);

            return view;
        }

        private class ViewHolder {

            ImageView imageView;
        }

        /**
         * @return the isInfiniteLoop
         */
        public boolean isInfiniteLoop() {
            return isInfiniteLoop;
        }

        /**
         * @param isInfiniteLoop the isInfiniteLoop to set
         */
        public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
            this.isInfiniteLoop = isInfiniteLoop;
            return this;
        }
    }

    private void permissionCheck() {
        permissionsNeeded = new ArrayList<String>();

        permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("Call Phone");
        if (!addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("Send Sms");
        if (!addPermission(permissionsList, Manifest.permission.GET_ACCOUNTS))
            permissionsNeeded.add("Account Info");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Phone Data");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Save Info");

        if (permissionsList.size() > 0) {
//            if (permissionsNeeded.size() > 0) {
//                // Need Rationale
////                String message = "You need to grant access to " + permissionsNeeded.get(0);
////                for (int i = 1; i < permissionsNeeded.size(); i++)
////                    message = message + ", " + permissionsNeeded.get(i);
////                showMessageOKCancel(message,
////                        new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
//                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
////                                }
////                            }
////                        });
//                return;
//            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            return;
        }


    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkUtilsReceiver);
        Runtime.getRuntime().gc();
    }


    private void getStatusData(){
        try{

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_status.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i(TAG, s.toString());

                            if(s.contains("User status is deactive"))
                            {
                                User_Status = "InActive";
                                v2.setEnabled(false);
                                v3.setEnabled(false);
                                itemNew.setEnabled(false);
                               /* item_Stock.setEnabled(false);*/

                                sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                                editorLogin = sharedpreferencesLogin.edit();
                                editorLogin.putString("user_status", User_Status);
                                editorLogin.apply();
                            }
                            else if (s.contains("User status is active"))
                            {
                                User_Status = "Active";
                                v2.setEnabled(true);
                                v3.setEnabled(true);
                                itemNew.setEnabled(true);
                                /*item_Stock.setEnabled(true);*/

                                sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                                editorLogin = sharedpreferencesLogin.edit();
                                editorLogin.putString("user_status", User_Status);
                                editorLogin.apply();
                            }

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

    private void getNotificationDataone(){
        try{

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "push2.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i(TAG, s.toString());


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

                    sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                    String token = sharedpreferencesLogin.getString("token",null);

                    //Adding parameters
                    params.put("gcm", token);

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

    private void getNotificationData(){
        try{

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "push3.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i(TAG, s.toString());


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

                    sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                    String token = sharedpreferencesLogin.getString("token",null);

                    //Adding parameters
                    params.put("gcm", token);

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
