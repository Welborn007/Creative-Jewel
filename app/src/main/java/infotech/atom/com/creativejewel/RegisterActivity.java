package infotech.atom.com.creativejewel;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import infotech.atom.com.creativejewel.app.AppController;
import infotech.atom.com.creativejewel.network.Constants;
import infotech.atom.com.creativejewel.network.FireToast;
import infotech.atom.com.creativejewel.network.NetworkUtils;
import infotech.atom.com.creativejewel.network.NetworkUtilsReceiver;

public class RegisterActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    EditText Username,Password,Confirm_Password,Fullname,CompanyName,Designation,Address,MobileNo,Email;
    private static final String TAG = "volley error";
    Button Register;
    private NetworkUtilsReceiver networkUtilsReceiver;
    Dialog dialog;

    AppController appcontroller;
    private int hot_number = 0;
    private TextView ui_hot = null;
    private int mNotificationsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_register);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

              /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        appcontroller = (AppController) getApplicationContext();
        updateNotificationsBadge(appcontroller.getProductsArraylist().size());

        Username = (EditText) findViewById(R.id.edit_username);
        Password = (EditText) findViewById(R.id.edit_password);
        Confirm_Password = (EditText) findViewById(R.id.edit_confirm_password);
        Fullname = (EditText) findViewById(R.id.edit_fullname);
        CompanyName = (EditText) findViewById(R.id.edit_Company);
        Designation = (EditText) findViewById(R.id.edit_Designation);
        Address = (EditText) findViewById(R.id.edit_Address);
        MobileNo = (EditText) findViewById(R.id.edit_mobile);
        Email = (EditText) findViewById(R.id.edit_Email);

        Register = (Button) findViewById(R.id.btn_submit);

        Username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Username.getText().toString().isEmpty()) {
                        Username.setError("Enter Username");
                        return;
                    } else {
                        Username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Password.getText().toString().isEmpty()) {
                        Password.setError("Enter Password");
                        return;
                    } else {
                        Password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        Confirm_Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Confirm_Password.getText().toString().isEmpty()) {
                        Confirm_Password.setError("Confirm Password");
                        return;
                    }
                    else if(!Password.getText().toString().trim().equals(Confirm_Password.getText().toString().trim())){
                        Confirm_Password.setError("Confirm correct password");
                        return;
                    }
                    else {
                        Confirm_Password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        Fullname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Fullname.getText().toString().isEmpty()) {
                        Fullname.setError("Enter Fullname");
                        return;
                    } else {
                        Fullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        CompanyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (CompanyName.getText().toString().isEmpty()) {
                        CompanyName.setError("Enter Company Name");
                        return;
                    } else {
                        CompanyName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        Designation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Designation.getText().toString().isEmpty()) {
                        Designation.setError("Enter Designation");
                        return;
                    } else {
                        Designation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        Address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Address.getText().toString().isEmpty()) {
                        Address.setError("Enter Address");
                        return;
                    } else {
                        Address.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        MobileNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (MobileNo.getText().toString().isEmpty()) {
                        MobileNo.setError("Enter Mobile No.");
                        return;
                    } else {
                        MobileNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });

        Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Email.getText().toString().isEmpty()) {
                        Email.setError("Enter Email");
                        return;
                    } else {
                        Email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
                    }
                }
            }
        });


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //Username,Password,Confirm_Password,Fullname,CompanyName,Designation,Address,MobileNo;

                String username_text = Username.getText().toString();
                String password_text = Password.getText().toString();
                String confirm_password_text = Confirm_Password.getText().toString();
                String fullname_text = Fullname.getText().toString();
                String companyname_text = CompanyName.getText().toString();
                String designation_text = Designation.getText().toString();
                String address_text = Address.getText().toString();
                String mobileno_text = MobileNo.getText().toString();
                String email_text = Email.getText().toString();

                if(!username_text.isEmpty() && !password_text.isEmpty() && !confirm_password_text.isEmpty() && !fullname_text.isEmpty() && !companyname_text.isEmpty() && !designation_text.isEmpty() && !address_text.isEmpty() && !mobileno_text.isEmpty() && !email_text.isEmpty() && password_text.equals(confirm_password_text))
                {
                    uploadRegisterData(username_text, password_text, confirm_password_text, fullname_text, companyname_text, designation_text, address_text, mobileno_text,email_text);
                }
                else if(username_text.isEmpty())
                {
                    Username.setError("Enter Username");
                }
                else if(password_text.isEmpty())
                {
                    Password.setError("Enter Password");
                }
                else if(confirm_password_text.isEmpty())
                {
                    Confirm_Password.setError("Confirm Password");
                }
                else if(fullname_text.isEmpty())
                {
                    Fullname.setError("Enter Fullname");
                }
                else if(companyname_text.isEmpty())
                {
                    CompanyName.setError("Enter Company Name");
                }
                else if(designation_text.isEmpty())
                {
                    Designation.setError("Enter Designation");
                }
                else if(address_text.isEmpty())
                {
                    Address.setError("Enter Address");
                }
                else if(mobileno_text.isEmpty())
                {
                    MobileNo.setError("Enter Mobile No.");
                }
                else if(email_text.isEmpty())
                {
                    Email.setError("Enter Email");
                }
                else if (!password_text.equals(confirm_password_text))
                {
                    Toast.makeText(RegisterActivity.this, "Confirm correct Password", Toast.LENGTH_SHORT).show();
                }

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Username.getWindowToken(), 0);
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

    private void uploadRegisterData(final String username, final String password, final String cpass, final String fullname, final String companyname, final String designation, final String address, final String mobileno, final String emailid){
        try{

            // custom dialog
            dialog = new Dialog(RegisterActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "new_registration.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i("response", s.toString());
                            dialog.dismiss();

                            if(s.contains("Successfuly Registered and Message Successfuly Send"))
                            {
                                Intent intent = new Intent(RegisterActivity.this,login_screen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            else if(s.contains("Successfuly Registered but Message not Send"))
                            {

                            }
                            else if(s.contains("Passwords are not Same"))
                            {
                                Toast.makeText(RegisterActivity.this, "Passwords are not Same", Toast.LENGTH_SHORT).show();
                            }
                            else if(s.contains("Username is Already Registered"))
                            {
                                Toast.makeText(RegisterActivity.this, "Username is Already Registered", Toast.LENGTH_SHORT).show();
                            }

//                            if(s.contains("OTP is send"))
//                            {
//                                Intent intent = new Intent(RegisterActivity.this,OTPVerify.class);
//                                intent.putExtra("mobile",mobileno);
//                                intent.putExtra("username",username);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                finish();
//                            }
//                            else if(s.contains("Username is Already Registered"))
//                            {
//                                Toast.makeText(RegisterActivity.this, "Username Exists", Toast.LENGTH_SHORT).show();
//                            }

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
                    params.put("cpassword", cpass);
                    params.put("name", fullname);
                    params.put("company_name", companyname);
                    params.put("designation", designation);
                    params.put("address", address);
                    params.put("mobile_no", mobileno);
                    params.put("email_id", emailid);

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
                Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
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
