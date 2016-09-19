package infotech.atom.com.creativejewel.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetworkUtils {

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnectionOn(Context context) {

		boolean mobileFlag = false, wifiFlag = false;
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		State mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

		State wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

		if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
			mobileFlag = true;
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			wifiFlag = true;
		}

		if (wifiFlag == true || mobileFlag == true) {
			return true;
		}

		return false;
	}

}

