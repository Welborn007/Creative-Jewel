package infotech.atom.com.creativejewel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
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
import com.android.volley.toolbox.ImageLoader;
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

public class Category_Listing extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    private int mNotificationsCount = 0;
    Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b10;
    AppController appcontroller;
    AutoScrollViewPager viewPager;
    PagerAdapter adapter;
    Dialog dialog;
    private static final String TAG = "volley error";
    private List<Data_Content> worldpopulationlist = null;
    String volume;
    GridView categories;
    private CategoryAdapter categoryAdapter;
    private List<CategoryPOJO> categoryPOJOs = new ArrayList<CategoryPOJO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category__listing);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

          /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        final Intent intent = getIntent();
        volume = intent.getStringExtra("volume");

        worldpopulationlist = new ArrayList<Data_Content>();


        appcontroller = (AppController) getApplicationContext();

        b1 = (Button) findViewById(R.id.ring);
        b2 = (Button) findViewById(R.id.necklace);
        b3 = (Button) findViewById(R.id.mangalsutras);
        b4 = (Button) findViewById(R.id.bangle);
        b5 = (Button) findViewById(R.id.tops);
        b6 = (Button) findViewById(R.id.chain);
        b7 = (Button) findViewById(R.id.pendants);
        b8 = (Button) findViewById(R.id.watchbelt);
        b9 = (Button) findViewById(R.id.hathpan);
        b10 = (Button) findViewById(R.id.chain_sets);

        categories = (GridView) findViewById(R.id.categories);
        categoryAdapter = new CategoryAdapter(categoryPOJOs, Category_Listing.this);
        categories.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();

        if(volume.contains("volume1"))
        {
            b8.setVisibility(View.GONE);
            b9.setVisibility(View.GONE);
            b10.setVisibility(View.GONE);
        }
        else if(volume.contains("volume2"))
        {
            b1.setVisibility(View.GONE);
            b2.setVisibility(View.GONE);
            b3.setVisibility(View.GONE);
            b5.setVisibility(View.GONE);
            b7.setVisibility(View.GONE);
            b9.setVisibility(View.GONE);
            b10.setVisibility(View.GONE);
        }
        else if(volume.contains("volume3"))
        {
            b3.setVisibility(View.GONE);
            b8.setVisibility(View.GONE);
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                    intent1.putExtra("category","ring");
                    intent1.putExtra("volume",volume);
                Log.i("volume", volume);
                    startActivity(intent1);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","necklace");
                intent1.putExtra("volume", volume);
                startActivity(intent1);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","mangalsutras");
                intent1.putExtra("volume",volume);
                startActivity(intent1);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","bangle");
                intent1.putExtra("volume",volume);
                startActivity(intent1);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","tops");
                intent1.putExtra("volume",volume);
                startActivity(intent1);
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","chain");
                intent1.putExtra("volume",volume);
                startActivity(intent1);
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","pendants");
                intent1.putExtra("volume",volume);
                startActivity(intent1);
            }
        });

        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","watchbelt");
                intent1.putExtra("volume",volume);
                startActivity(intent1);
            }
        });

        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","hathpan");
                intent1.putExtra("volume",volume);
                startActivity(intent1);
            }
        });

        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Category_Listing.this,ViewPager_display.class) ;
                intent1.putExtra("category","chainset");
                intent1.putExtra("volume",volume);
                startActivity(intent1);
            }
        });

        updateNotificationsBadge(appcontroller.getProductsArraylist().size());


    }

    @Override
    public void NetworkOpen() {
        uploadImage(volume);
        fetchCategory(volume);
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
                Intent intent = new Intent(Category_Listing.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.menu_hot:
                Intent intent1 = new Intent(Category_Listing.this,Checkout_Submit.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void uploadImage(final String volume){
        try{
            // custom dialog
            dialog = new Dialog(Category_Listing.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_banner_image.php";

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
                                    Data_Content map = new Data_Content();

                                    map.setFlag(resultObject.getString("image"));

                                    worldpopulationlist.add(map);
                                }

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }catch (NullPointerException npe)
                            {
                                Toast.makeText(getApplicationContext(), "Null pointer exception", Toast.LENGTH_SHORT).show();
                            }


                            viewPager = (AutoScrollViewPager) findViewById(R.id.viewpager);
                            adapter = new PagerAdapter(Category_Listing.this,worldpopulationlist);
                            viewPager.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            viewPager.startAutoScroll();
                            viewPager.setInterval(2000);
                            viewPager.setCycle(true);
                            viewPager.setStopScrollWhenTouch(true);

                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    viewPager.startAutoScroll();
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

//    private void uploadBanner(final String volume){
//        try{
//            // custom dialog
//            dialog = new Dialog(Category_Listing.this);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.progressdialog);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//            dialog.show();
//
//            String serverUrl = "http://creativejewel.in/app_banner_image.php";
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String s) {
//                            //Disimissing the progress dialog
//
//                            Log.i(TAG, s.toString());
//                            dialog.dismiss();
//
//                            try{
//
//                                JSONObject resultArray = new JSONObject(s);
//
//                                Log.d("My App", resultArray.toString());
//
//                                JSONArray result = resultArray.getJSONArray("result");
//
//                                // Parsing json
//                                for (int i = 0; i < result.length() && result.length() != 0; i++)
//                                {
//
//                                    JSONObject resultObject = result.getJSONObject(i);
//
//                                    Picasso.with(Category_Listing.this)
//                                            .load( resultObject.getString("image") )
//                                            .error( R.drawable.ic_error )
//                                            .placeholder( R.drawable.progress_animation)
//                                            .into(banner);
//
//                                }
//
//                            }catch (JSONException e) {
//                                e.printStackTrace();
//                            }catch (NullPointerException npe)
//                            {
//                                Toast.makeText(getApplicationContext(), "Null pointer exception", Toast.LENGTH_SHORT).show();
//                            }
//
//
//
//
//
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError volleyError) {
//                            //Dismissing the progress dialog
//                            dialog.dismiss();
//                        }
//                    }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//
//                    //Creating parameters
//                    Map<String,String> params = new Hashtable<String, String>();
//
//                    //Adding parameters
//                    params.put("volume_type", volume);
//
//                    Log.i(TAG, params.toString());
//
//                    //returning parameters
//                    return params;
//                }
//            };
//
//            //Creating a Request Queue
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//            //Adding request to the queue
//            requestQueue.add(stringRequest);
//
//        }catch (NullPointerException npe)
//        {
//            Toast.makeText(getApplicationContext(), "Null pointer exception", Toast.LENGTH_SHORT).show();
//        }
//        catch (Exception e)
//        {
//
//        }
//    }

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
            View itemView = mLayoutInflater.inflate(R.layout.paging_item_category, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Button button = (Button) itemView.findViewById(R.id.addcart);


            int position12 = position + 1;
            String countabc = String.valueOf(position12);
            button.setText("Page No. " + countabc);

            //imageLoader.DisplayImage(worldpopulationlist.get(position).getFlag(), imageView);

            Picasso.with(Category_Listing.this)
                                            .load(worldpopulationlist.get(position).getFlag())
//                                            .error( R.drawable.loading1 )
                                            .placeholder(R.drawable.progress_animation)
                                            .into(imageView);


            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    private void fetchCategory(final String volume){
        try{
            // custom dialog
            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_category.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i("Category_Type", s.toString());

                            try{

                                JSONObject resultArray = new JSONObject(s);

                                Log.d("My App", resultArray.toString());

                                JSONArray result = resultArray.getJSONArray("result");

                                // Parsing json
                                for (int i = 0; i < result.length() && result.length() != 0; i++)
                                {

                                    JSONObject resultObject = result.getJSONObject(i);
                                    CategoryPOJO categoryPOJO = new CategoryPOJO();

                                    categoryPOJO.setCategory(resultObject.getString("category"));
                                    categoryPOJO.setCategory_code(resultObject.getString("category_code"));
                                    categoryPOJO.setCategory_id(resultObject.getString("category_id"));

                                    categoryPOJOs.add(categoryPOJO);
                                }

                                categoryAdapter.notifyDataSetChanged();

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }catch (NullPointerException npe)
                            {
                                Toast.makeText(getApplicationContext(), "Null pointer exception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    //Creating parameters
                    Map<String,String> params = new Hashtable<String, String>();

                    //Adding parameters
                    params.put("volume_type", volume);

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

    class CategoryAdapter<T> extends BaseAdapter {
        List<CategoryPOJO> categoryPOJOs;
        private Activity activity;
        private LayoutInflater layoutInflater = null;

        public CategoryAdapter(List<CategoryPOJO> categoryPOJOs, Activity activity) {
            this.categoryPOJOs =  categoryPOJOs;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return categoryPOJOs.size();
        }

        @Override
        public Object getItem(int position) {
            return categoryPOJOs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.category_listrow_layout, null);

                viewHolder.catItem = (Button) convertView.findViewById(R.id.catItem);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final CategoryPOJO newsPOJO = categoryPOJOs.get(position);

            viewHolder.catItem.setText(newsPOJO.getCategory());

            viewHolder.catItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(Category_Listing.this,Subcategory_Activity.class) ;
                    intent1.putExtra("categorycode",newsPOJO.getCategory_code());
                    intent1.putExtra("category_id",newsPOJO.getCategory_id());
                    intent1.putExtra("category",newsPOJO.getCategory());
                    intent1.putExtra("volume",volume);
                    startActivity(intent1);
                }
            });

            return convertView;
        }

        private class ViewHolder {
            Button catItem;
        }
    }
}
