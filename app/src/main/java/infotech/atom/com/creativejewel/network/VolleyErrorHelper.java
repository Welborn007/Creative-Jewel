package infotech.atom.com.creativejewel.network;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import infotech.atom.com.creativejewel.R;

public class VolleyErrorHelper {
    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     * @param error
     * @param context
     * @return
     */
    public static void getMessage(Object error, final Context context) {
        if(error!=null){
        if (error instanceof TimeoutError) {
            FireToast.customSnackbar(context, context.getResources().getString(R.string.generic_server_down), "Ok");
           // return context.getResources().getString(R.string.generic_server_down);
        }
        else if (isServerProblem(error)) {
            handleServerError(error, context);
        }
        else if (error instanceof ParseError) {
            FireToast.customSnackbar(context, context.getResources().getString(R.string.generic_parse_error), "Ok");
           //return context.getResources().getString(R.string.generic_parse_error);
        }
        else if (isNetworkProblem(error)) {
            FireToast.customSnackbarWithListner(context,context.getResources().getString(R.string.no_internet_error),"Settings", new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            //return context.getResources().getString(R.string.no_internet_error);
        }
        //return context.getResources().getString(R.string.generic_error);
        }else{
            FireToast.customSnackbar(context, context.getResources().getString(R.string.generic_server_down), "Ok");
        }
    }

    /**
     * Determines whether the error is related to network
     * @param error
     * @return
     */
    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }
    /**
     * Determines whether the error is related to server
     * @param error
     * @return
     */
    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }
    /**
     * Handles the server error, tries to determine whether to show a stock message or to
     * show a message retrieved from the server.
     *
     * @param err
     * @param context
     * @return
     */
    private static void handleServerError(Object err, Context context) {
        VolleyError error = (VolleyError) err;

        NetworkResponse response = error.networkResponse;

        if (response != null) {
            switch (response.statusCode) {
                case 404:
                case 422:
                case 401:

                    // invalid request
                    FireToast.customSnackbar(context, context.getResources().getString(R.string.generic_server_down), "Ok");
                    //return context.getResources().getString(R.string.generic_server_down);

                default:
                    FireToast.customSnackbar(context, context.getResources().getString(R.string.generic_server_down), "Ok");
                    //return context.getResources().getString(R.string.generic_server_down);
            }
        }
        FireToast.customSnackbar(context, context.getResources().getString(R.string.generic_error), "Ok");
        //return context.getResources().getString(R.string.generic_error);
    }
}