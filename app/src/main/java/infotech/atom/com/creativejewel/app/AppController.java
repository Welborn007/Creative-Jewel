package infotech.atom.com.creativejewel.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


import java.util.ArrayList;

import infotech.atom.com.creativejewel.AddCart_model;
import infotech.atom.com.creativejewel.util.LruBitmapCache;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private static AppController mInstance;

    public ArrayList<AddCart_model> myProducts = new ArrayList<AddCart_model>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null)
            imageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
        return imageLoader;
    }

    public <T> void addRequestToQueue(Request<T> requestQueue, String tag) {
        requestQueue.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        Log.i("Sending Request", requestQueue.toString());
        getRequestQueue().add(requestQueue);


    }


    public <T> void addRequestToQueue(Request<T> requestQueue) {
        requestQueue.setTag(TAG);
        getRequestQueue().add(requestQueue);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }


    public static boolean isNetworkConnectionOn(Context context) {

        boolean mobileFlag = false, wifiFlag = false;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo.State mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

        NetworkInfo.State wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
            mobileFlag = true;
        }
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            wifiFlag = true;
        }

        if (wifiFlag == true || mobileFlag == true) {
            return true;
        }

        return false;
    }

    public AddCart_model getProducts(int pPosition) {

        return myProducts.get(pPosition);
    }

    public void setProducts(AddCart_model Products) {

        myProducts.add(Products);

    }

    public void removeProducts(int Position) {

        myProducts.remove(Position);

    }

    public void removeProductsItems() {

        myProducts.clear();

    }

    public ArrayList<AddCart_model> getProductsArraylist() {

        return myProducts;
    }

}
