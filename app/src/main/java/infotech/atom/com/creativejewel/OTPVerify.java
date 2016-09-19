package infotech.atom.com.creativejewel;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import infotech.atom.com.creativejewel.network.Constants;
import infotech.atom.com.creativejewel.network.FireToast;
import infotech.atom.com.creativejewel.network.NetworkUtils;
import infotech.atom.com.creativejewel.network.NetworkUtilsReceiver;

public class OTPVerify extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    Button send, skip, resend;
    EditText Otp;

    Dialog dialog;

    TextView counter,number;
    String mobile,username;

    IntentFilter filter1;
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);

        send = (Button) findViewById(R.id.sendOTP);
        skip = (Button) findViewById(R.id.skip);
        Otp = (EditText) findViewById(R.id.otp);
        resend = (Button) findViewById(R.id.resendOTP);
        counter = (TextView) findViewById(R.id.counter);
        number = (TextView) findViewById(R.id.mobinumber);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBar.showOverflowMenu();

             /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        username = intent.getStringExtra("username");

        number.setText(mobile);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!Otp.getText().toString().trim().isEmpty())
                {
                    String otp = Otp.getText().toString().trim();
                    uploadOTPData(username,otp);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
                else
                {
                    Toast.makeText(OTPVerify.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new CountDownTimer(61000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                counter.setText("OTP will be received within : " + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                counter.setText("click on 'Resend OTP'");
            }
        }.start();

        filter1 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(myReceiver, filter1);

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

        unregisterReceiver(myReceiver);
        unregisterReceiver(networkUtilsReceiver);
        Runtime.getRuntime().gc();
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                        String otp = message.replaceAll("[^0-9]", "");

                        Otp.setText(otp);

                        if(!otp.isEmpty())
                        {
                            uploadOTPData(username,otp);
                        }

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);

            }

        }
    };


    private void uploadOTPData(final String Username, final String OTP){
        try{

            // custom dialog
            dialog = new Dialog(OTPVerify.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "registration.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i("response", s.toString());
                            dialog.dismiss();

                            if(s.contains("Successfuly Registered"))
                            {
                                Intent intent = new Intent(OTPVerify.this,login_screen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            else if(s.contains("All Fields are Mandatory"))
                            {
                                Toast.makeText(OTPVerify.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                            }
                            else if(s.contains("You have entered wrong OTP"))
                            {
                                Toast.makeText(OTPVerify.this, "Please enter correct OTP", Toast.LENGTH_SHORT).show();
                            }
                            else if(s.contains("OTP time period is over"))
                            {
                                Toast.makeText(OTPVerify.this, "Re-Send OTP", Toast.LENGTH_SHORT).show();
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
                    params.put("username", Username);
                    params.put("otp", OTP);


                    Log.i("sending", params.toString());

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
        getMenuInflater().inflate(R.menu.simple_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
        switch (item.getItemId()) {

            case R.id.menu_hotlist:
                Intent intent = new Intent(OTPVerify.this,HomeActivity.class);
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
}
