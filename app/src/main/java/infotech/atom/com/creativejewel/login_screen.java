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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import infotech.atom.com.creativejewel.app.AppController;
import infotech.atom.com.creativejewel.network.Constants;
import infotech.atom.com.creativejewel.network.FireToast;
import infotech.atom.com.creativejewel.network.NetworkUtils;
import infotech.atom.com.creativejewel.network.NetworkUtilsReceiver;

public class login_screen extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    EditText User_name,Pass_word;
    TextView forgot,Register_new;
    Button sign_in;
    Dialog dialog;
    CheckBox mCbShowPwd;

    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin" ;
    SharedPreferences sharedpreferencesLogin;
    SharedPreferences.Editor editorLogin;

    AppController appcontroller;
    private int hot_number = 0;
    private TextView ui_hot = null;
    private int mNotificationsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login_screen);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

        appcontroller = (AppController) getApplicationContext();
        updateNotificationsBadge(appcontroller.getProductsArraylist().size());

             /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        User_name = (EditText) findViewById(R.id.edit_username);
        Pass_word = (EditText) findViewById(R.id.edit_password);
        sign_in = (Button) findViewById(R.id.btn_login);
        forgot = (TextView) findViewById(R.id.forgot_pass);
        Register_new = (TextView) findViewById(R.id.register_now);

        mCbShowPwd = (CheckBox) findViewById(R.id.cbShowPwd);

        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    Pass_word.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    Pass_word.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_screen.this, forgot_pass.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Register_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_screen.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        User_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (User_name.getText().toString().isEmpty()) {
                        User_name.setError("Empty Username");
                    } else {
                        User_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        Pass_word.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!Pass_word.getText().toString().isEmpty()) {
                        Pass_word.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    } else {
                        Pass_word.setError("Enter Password");
                    }
                }
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Username = User_name.getText().toString();
                final String Password = Pass_word.getText().toString();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(User_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(Pass_word.getWindowToken(), 0);

                if (!Username.isEmpty() && !Password.isEmpty()) {
                    uploadLoginData(Username,Password);
                    return;
                } else if (Username.isEmpty()) {
                    User_name.setError("Empty Username");
                } else if (Password.isEmpty()) {
                    Pass_word.setError("Enter Password");
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

    private void uploadLoginData(final String username, final String password){
        try{

            // custom dialog
            dialog = new Dialog(login_screen.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_login.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i("response", s.toString());
                            dialog.dismiss();

                            if(s.contains("Login Successful"))
                            {
                                sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                                editorLogin = sharedpreferencesLogin.edit();
                                editorLogin.putString("status", s);
                                editorLogin.putString("username",username);
                                editorLogin.commit();


                                Intent intent2 = getIntent();
                                String checkout = intent2.getStringExtra("checkout");

                                try{

                                    if(checkout == null)
                                        {
                                            Intent intent = new Intent(login_screen.this,HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    else if(checkout.contains("checkout") )
                                    {
                                        Intent intent = new Intent(login_screen.this,Checkout_Submit.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }

                                }catch (NullPointerException npe)
                                {

                                }

                            }
                            else if(s.contains("Username or Password are wrong"))
                            {
                                Toast.makeText(login_screen.this, "Wrong Login Details", Toast.LENGTH_SHORT).show();
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
                    params.put("username", username);
                    params.put("password", password);


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
                Intent intent = new Intent(login_screen.this,HomeActivity.class);
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
