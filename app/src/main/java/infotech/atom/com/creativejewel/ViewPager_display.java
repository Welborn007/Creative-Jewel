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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ViewPager_display extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    private int mNotificationsCount = 0;
    AutoScrollViewPager viewPager;
    Dialog dialog;
    private static final String TAG = "volley error";

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private List<Data_Content> worldpopulationlist = null;

    PagerAdapter adapter;
    TextView paging;
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin" ;
    SharedPreferences sharedpreferencesLogin;
    SharedPreferences.Editor editorLogin;
    AppController appcontroller;
    String Volume_name,Category_code,Subcategory_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_display);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

           /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Intent intent = getIntent();
        Volume_name = intent.getStringExtra("volume");

        worldpopulationlist = new ArrayList<Data_Content>();
        appcontroller = (AppController) getApplicationContext();
        updateNotificationsBadge(appcontroller.getProductsArraylist().size());
    }


    @Override
    public void NetworkOpen() {

        Intent intent = getIntent();
        Volume_name = intent.getStringExtra("volume");

        Category_code = intent.getStringExtra("category");
        Subcategory_code = intent.getStringExtra("sub_cat");
//
        uploadImage(Volume_name, Category_code,Subcategory_code);
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

    private void uploadImage(final String volume, final String category_code, final String subcate_code){
        try{
            // custom dialog
            dialog = new Dialog(ViewPager_display.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_image.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i(TAG, s.toString());
                            dialog.dismiss();

                            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            editor = sharedpreferences.edit();
                            editor.putString(volume, s);
                            editor.commit();

                            try{

                                JSONObject resultArray = new JSONObject(s);

                                Log.d("My App", resultArray.toString());

                                JSONArray result = resultArray.getJSONArray("result");

                                // Parsing json
                                for (int i = 0; i < result.length() && result.length() != 0; i++)
                                {

                                    JSONObject resultObject = result.getJSONObject(i);
                                    Data_Content map = new Data_Content();

                                    map.setFlag(resultObject.getString("image"));
                                    map.setPage_no(resultObject.getString("page_no"));
                                    map.setItem_name(resultObject.getString("item_name"));
                                    map.setWeight(resultObject.getString("weight"));

                                    worldpopulationlist.add(map);
                                }

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }catch (NullPointerException npe)
                            {
                                Toast.makeText(getApplicationContext(), "Null pointer exception", Toast.LENGTH_SHORT).show();
                            }


                            viewPager = (AutoScrollViewPager) findViewById(R.id.viewpager);
                            adapter = new PagerAdapter(ViewPager_display.this,worldpopulationlist);
                            viewPager.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
//                            viewPager.startAutoScroll();
//                            viewPager.setInterval(2000);
//                            viewPager.setCycle(false);
//                            viewPager.setStopScrollWhenTouch(true);

                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                                    viewPager.startAutoScroll();
                                }
                                @Override
                                public void onPageSelected(int position) {
                                }
                                @Override
                                public void onPageScrollStateChanged(int state) {
                                }
                            });

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
                    params.put("volume_type", volume);
                    params.put("category",category_code);
                    params.put("sub_cat",subcate_code);

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
        catch (Exception e)
        {

        }
    }



    public class PagerAdapter extends android.support.v4.view.PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        private List<Data_Content> worldpopulationlist = null;



        public PagerAdapter(Context context,List<Data_Content> worldpopulationlist) {
            mContext = context;
            this.worldpopulationlist = worldpopulationlist;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return worldpopulationlist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Button button = (Button) itemView.findViewById(R.id.addcart);
            paging = (TextView) itemView.findViewById(R.id.paging);
            TextView weight = (TextView) itemView.findViewById(R.id.weight);
            RelativeLayout bar = (RelativeLayout) itemView.findViewById(R.id.bar);

            weight.setText("Wgt- " + worldpopulationlist.get(position).getWeight() + " gms");

            int position12 = position + 1;
            String countabc = String.valueOf(position12);
            paging.setText(countabc);

//            Picasso.with(mContext).load(worldpopulationlist.get(position).getFlag()).into(imageView);

            if(Volume_name.contains("volume1") || Volume_name.contains("volume2") || Volume_name.contains("volume3"))
            {
                bar.setVisibility(View.GONE);
            }

            Picasso.with( mContext )
                    .load(worldpopulationlist.get(position).getFlag())
                    .error(R.drawable.back)
                            .placeholder(R.drawable.back)
                    .fit()
                            .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.stopAutoScroll();
                    Intent intent = new Intent(ViewPager_display.this, second_level.class);
                    intent.putExtra("image", worldpopulationlist.get(position).getFlag());
                    intent.putExtra("pageno", worldpopulationlist.get(position).getPage_no());
                    intent.putExtra("itemname", worldpopulationlist.get(position).getItem_name());
                    intent.putExtra("weight",worldpopulationlist.get(position).getWeight());
                    intent.putExtra("volume", Volume_name);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "added to cart", Toast.LENGTH_SHORT).show();
                }
            });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
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
                Intent intent = new Intent(ViewPager_display.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.menu_hot:
                Intent intent1 = new Intent(ViewPager_display.this,Checkout_Submit.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
