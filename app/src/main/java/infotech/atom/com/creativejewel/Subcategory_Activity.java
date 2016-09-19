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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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

public class Subcategory_Activity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    private int mNotificationsCount = 0;
    AppController appcontroller;
    AutoScrollViewPager viewPager;
    Dialog dialog;
    private static final String TAG = "volley error";
    private List<Data_Content> worldpopulationlist = null;
    ListView Subcategories;
    private SubCategoryAdapter subCategoryAdapter;
    private List<SubCategory_Pojo> subCategory_pojos = new ArrayList<SubCategory_Pojo>();
    PhotoViewAttacher mAttacher;
    String category,categorycode,category_id,volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory_);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

        appcontroller = (AppController) getApplicationContext();

          /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        final Intent intent = getIntent();
        volume = intent.getStringExtra("volume");
        category_id = intent.getStringExtra("category_id");
        categorycode = intent.getStringExtra("categorycode");
        category = intent.getStringExtra("category");

        updateNotificationsBadge(appcontroller.getProductsArraylist().size());

        Subcategories = (ListView) findViewById(R.id.sub_Listing);
        subCategoryAdapter = new SubCategoryAdapter(subCategory_pojos, Subcategory_Activity.this);
        Subcategories.setAdapter(subCategoryAdapter);
        subCategoryAdapter.notifyDataSetChanged();

    }

    @Override
    public void NetworkOpen() {
        fetchSubCategory(volume,categorycode);
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
                Intent intent = new Intent(Subcategory_Activity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.menu_hot:
                Intent intent1 = new Intent(Subcategory_Activity.this,Checkout_Submit.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void fetchSubCategory(final String volume, final String code){
        try{
            // custom dialog
            // custom dialog
            dialog = new Dialog(Subcategory_Activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_subcategory.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog
                            dialog.dismiss();

                            Log.i("Category_Type", s.toString());

                            try{

                                JSONObject resultArray = new JSONObject(s);

                                Log.d("My App", resultArray.toString());

                                JSONArray result = resultArray.getJSONArray("result");

                                // Parsing json
                                for (int i = 0; i < result.length() && result.length() != 0; i++)
                                {

                                    JSONObject resultObject = result.getJSONObject(i);
                                    SubCategory_Pojo subCategory_pojo = new SubCategory_Pojo();

                                    subCategory_pojo.setSub_category_id(resultObject.getString("sub_category_id"));
                                    subCategory_pojo.setSub_category_code(resultObject.getString("sub_category_code"));
                                    subCategory_pojo.setSub_category(resultObject.getString("sub_category"));
                                    subCategory_pojo.setSub_category_image(resultObject.getString("sub_category_image"));

                                    subCategory_pojos.add(subCategory_pojo);
                                }

                                subCategoryAdapter.notifyDataSetChanged();

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
                            dialog.dismiss();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    //Creating parameters
                    Map<String,String> params = new Hashtable<String, String>();

                    //Adding parameters
                    params.put("volume_type", volume);
                    params.put("category",code);

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

    class SubCategoryAdapter<T> extends BaseAdapter {
        List<SubCategory_Pojo> subCategory_pojos;
        private Activity activity;
        private LayoutInflater layoutInflater = null;

        public SubCategoryAdapter(List<SubCategory_Pojo> subCategory_pojos, Activity activity) {
            this.subCategory_pojos =  subCategory_pojos;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return subCategory_pojos.size();
        }

        @Override
        public Object getItem(int position) {
            return subCategory_pojos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.subcategory_listrow_layout, null);

                viewHolder.catItem = (Button) convertView.findViewById(R.id.catItem);
                viewHolder.thumb = (ImageView) convertView.findViewById(R.id.thumb);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final SubCategory_Pojo subCategory_pojo = subCategory_pojos.get(position);

            viewHolder.catItem.setText(subCategory_pojo.getSub_category());

            Picasso.with( activity )
                    .load(subCategory_pojo.getSub_category_image())
                    .error(R.drawable.logo_trans)
                    .placeholder(R.drawable.logo_trans)
                    .fit()
                    .into(viewHolder.thumb);

            viewHolder.thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SubCategory_Pojo subCategory_pojo = subCategory_pojos.get(position);

                    Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.image_dialog);

                    final ImageView img = (ImageView) dialog.findViewById(R.id.dialog_image);

                    Picasso.with(activity)
                            .load(subCategory_pojo.getSub_category_image())
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

            viewHolder.catItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(Subcategory_Activity.this,ViewPager_display.class) ;
                    intent1.putExtra("category",categorycode);
                    intent1.putExtra("sub_cat",subCategory_pojo.getSub_category_code());
                    intent1.putExtra("volume",volume);
                    startActivity(intent1);
                }
            });

            return convertView;
        }

        private class ViewHolder {
            Button catItem;
            ImageView thumb;
        }
    }
}
