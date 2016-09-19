package infotech.atom.com.creativejewel;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import infotech.atom.com.creativejewel.network.FireToast;
import infotech.atom.com.creativejewel.network.NetworkUtils;
import infotech.atom.com.creativejewel.network.NetworkUtilsReceiver;

public class How_to_use extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    private int mNotificationsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

            /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

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
                Intent intent = new Intent(How_to_use.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case R.id.menu_hot:
                Intent intent1 = new Intent(How_to_use.this,Checkout_Submit.class);
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
