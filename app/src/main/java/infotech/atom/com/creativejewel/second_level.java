package infotech.atom.com.creativejewel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import infotech.atom.com.creativejewel.app.AppController;
import infotech.atom.com.creativejewel.network.FireToast;
import infotech.atom.com.creativejewel.network.NetworkUtils;
import infotech.atom.com.creativejewel.network.NetworkUtilsReceiver;
import uk.co.senab.photoview.PhotoViewAttacher;

public class second_level extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    PhotoViewAttacher mAttacher;
    AppController appcontroller;
    AddCart_model addCart_model;
    ArrayList<Country> countryList;
    MyAdapter dataAdapter = null;
    CheckBox name;
    String image;
    String page_no;
    String itemName;
    String Volume;
    HorizontalListView listView;
    ArrayAdapter<String> adapter;
    TextView zoom;

    private int hot_number = 0;
    private TextView ui_hot = null;
    private int mNotificationsCount = 0;

    //dialog instruction
    Dialog dialog;
    EditText Edit_Remarks,Edit_quantity,Edit_Size,Edit_Weight;
    Spinner melting_spinner;
    Button addQuantity,addSize,addWeight;
    Button deleteQuantity,deleteSize,deleteWeight;
    Button Cancel, Submit,Regular,Regular_Weight;
    String melting="";

    String Edit_instructions = "";

    private List<AddCart_model> addCartList = new ArrayList<AddCart_model>();

    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin";
    SharedPreferences sharedpreferencesLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_level);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.showOverflowMenu();

           /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Intent intent = getIntent();
        image = intent.getStringExtra("image");

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);

        appcontroller = (AppController) getApplicationContext();

        Picasso.with(second_level.this)
                .load(image)
                .error(R.drawable.back)
                .placeholder(R.drawable.back)
                .into(imageView);

        zoom = (TextView) findViewById(R.id.zoom);

        zoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zoom.setVisibility(View.GONE);
                return false;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAttacher = new PhotoViewAttacher(imageView);
                return false;
            }
        });

        //Generate list View from ArrayList
        displayListView();

        checkButtonClick();

        checkOutButtonClick();

        InstructionButtonClick();

        updateNotificationsBadge(appcontroller.getProductsArraylist().size());

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

    private void displayListView() {

        countryList = new ArrayList<Country>();

        Intent intent = getIntent();
        image = intent.getStringExtra("image");
        page_no = intent.getStringExtra("pageno");
        itemName = intent.getStringExtra("itemname");
        Volume = intent.getStringExtra("volume");

        String[] items = itemName.split(",");

        for (String item : items) {
            System.out.println("item = " + item);
            Country country = new Country(item, false);
            countryList.add(country);
        }

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, items);

        dataAdapter = new MyAdapter(this,
                R.layout.country_info, countryList);

        listView = (HorizontalListView) findViewById(R.id.listView1);
        //listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //listView.setItemsCanFocus(false);
        listView.setAdapter(dataAdapter);

        /*
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.country_info, countryList);

        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        */


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


            }
        });

    }


/*
    private void checkButtonClick() {

        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();

                SparseBooleanArray checked = listView.getCheckedItemPositions();
                try {
                    if (checked != null) {
                        if (checked.size() != 0) {
                            for (int i = 0; i < checked.size(); i++) {

                                int position = checked.keyAt(i);
                                if (checked.valueAt(i))
                                    responseText.append(adapter.getItem(position) + ",");
                            }

                            Intent intent = getIntent();
                            image = intent.getStringExtra("image");
                            page_no = intent.getStringExtra("pageno");
                            itemName = intent.getStringExtra("itemname");
                            Volume = intent.getStringExtra("volume");

                            addCart_model = new AddCart_model();

                            sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                            String username = sharedpreferencesLogin.getString("username", null);

                            String Items = responseText.toString();

                            Items = Items.substring(0, Items.length() - 1);

                            addCart_model.setItem_name(Items);
                            addCart_model.setPageno(page_no);
                            addCart_model.setVolume(Volume);
                            addCart_model.setUsername(username);
                            addCart_model.setInstructions(Edit_instructions);
                            appcontroller.setProducts(addCart_model);
                            addCartList.add(addCart_model);

                            updateNotificationsBadge(appcontroller.getProductsArraylist().size());

                            adapter.notifyDataSetChanged();

                            Toast.makeText(second_level.this, "Items Added!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(second_level.this, "Please Check Items", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(second_level.this, "Please Check Items", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NullPointerException npe)
                {

                }
                catch (StringIndexOutOfBoundsException sioobe)
                {
                    Toast.makeText(second_level.this, "Please Check Items", Toast.LENGTH_SHORT).show();
                }

                checked.clear();

            }
        });

    }
*/



    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(Edit_instructions.isEmpty())
                {
                    Toast.makeText(second_level.this, "Please enter instructions!!", Toast.LENGTH_SHORT).show();
                }else
                {
                    StringBuffer responseText = new StringBuffer();

                    try{

                        ArrayList<Country> countryList = dataAdapter.countryList;
                        for(int i=0;i<countryList.size();i++){
                            Country country = countryList.get(i);
                            if(country.isSelected()){
                                responseText.append(country.getName() + ",");
                            }
                        }

                        Intent intent = getIntent();
                        image = intent.getStringExtra("image");
                        page_no = intent.getStringExtra("pageno");
                        itemName = intent.getStringExtra("itemname");
                        Volume = intent.getStringExtra("volume");

                        addCart_model = new AddCart_model();

                        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                        String username = sharedpreferencesLogin.getString("username", null);

                        String Items = responseText.toString();

                        Items = Items.substring(0, Items.length() - 1);

                        addCart_model.setItem_name(Items);
                        addCart_model.setPageno(page_no);
                        addCart_model.setVolume(Volume);
                        addCart_model.setThumbnail(image);
                        addCart_model.setUsername(username);
                        addCart_model.setInstructions(Edit_instructions);
                        appcontroller.setProducts(addCart_model);
                        addCartList.add(addCart_model);

                        updateNotificationsBadge(appcontroller.getProductsArraylist().size());

                        adapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(),
                                "Items Added to Cart!!", Toast.LENGTH_LONG).show();



                    }catch (NullPointerException npe)
                    {

                    }
                    catch (StringIndexOutOfBoundsException sioobe)
                    {
                        Toast.makeText(second_level.this, "Please Check Items", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }

    private void checkOutButtonClick() {


        Button myButton = (Button) findViewById(R.id.checkout);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
                String username = sharedpreferencesLogin.getString("username", null);
                String status = sharedpreferencesLogin.getString("status", null);

                if (username != null && status != null) {

                    if (!appcontroller.getProductsArraylist().isEmpty()) {
                        Intent i = new Intent(second_level.this, Checkout_Submit.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } else {
                        Toast.makeText(second_level.this, "Please add items to cart", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (!appcontroller.getProductsArraylist().isEmpty()) {
                        Intent i = new Intent(second_level.this, login_screen.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("checkout", "checkout");
                        startActivity(i);
                    } else {
                        Toast.makeText(second_level.this, "Please add items to cart", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

//    private void InstructionButtonClick() {
//
//
//        Button myButton = (Button) findViewById(R.id.instruction);
//        myButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                dialog = new Dialog(second_level.this);
//                dialog.setContentView(R.layout.instruction_dialog_layout);
//                dialog.setTitle("Instructions...");
//
//                Edit_Remarks = (EditText) dialog.findViewById(R.id.edit_remark);
//                Edit_quantity = (EditText) dialog.findViewById(R.id.edit_quantity);
//                Edit_Melting = (EditText) dialog.findViewById(R.id.edit_melting);
//                Edit_Size = (EditText) dialog.findViewById(R.id.edit_size);
//                Edit_Weight = (EditText) dialog.findViewById(R.id.edit_weight);
//
//                Cancel = (Button) dialog.findViewById(R.id.cancel);
//                Submit = (Button) dialog.findViewById(R.id.Submit);
//
//                Cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                Submit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String remarks = Edit_Remarks.getText().toString().trim();
//                        String quantity = Edit_quantity.getText().toString().trim();
//                        String melting = Edit_Melting.getText().toString().trim();
//                        String size = Edit_Size.getText().toString().trim();
//                        String weight = Edit_Weight.getText().toString().trim();
//
//                        if (size.isEmpty()) {
//                            Edit_Size.setError("Enter Size");
//                        } else if (quantity.isEmpty()) {
//                            Edit_quantity.setError("Enter Quantity");
//                        } else if (melting.isEmpty()) {
//                            Edit_Melting.setError("Enter Melting");
//                        } else if (weight.isEmpty()) {
//                            Edit_Weight.setError("Enter Weight");
//                        } else {
//                            if (remarks.isEmpty()) {
//                                remarks = "N/A";
//                            }
//                            Edit_instructions = "Remarks : " + remarks + ",\n" + "Quantity : " + quantity + ",\n" + "Size : " + size + ",\n" + "Melting : " + melting + ",\n" + "Weight : " + weight;
//                            dialog.dismiss();
//                        }
//                    }
//                });
//
//                dialog.show();
//
//            }
//        });
//
//    }



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
                Intent intent = new Intent(second_level.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.menu_hot:
                Intent intent1 = new Intent(second_level.this,Checkout_Submit.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private class MyAdapter extends ArrayAdapter<Country> {

        private ArrayList<Country> countryList;

        public MyAdapter(Context context, int textViewResourceId,
                         ArrayList<Country> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<Country>();
            this.countryList.addAll(countryList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.country_info, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Country country = (Country) cb.getTag();
                        country.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Country country = countryList.get(position);
            holder.name.setText(country.getName());
            holder.name.setChecked(country.isSelected());
            holder.name.setTag(country);

            return convertView;

        }


    }

    private void InstructionButtonClick() {

        Button myButton = (Button) findViewById(R.id.instruction);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new Dialog(second_level.this);
                dialog.setContentView(R.layout.instruction_dialog_layout);
                dialog.setTitle("Instructions...");

                Edit_Remarks = (EditText) dialog.findViewById(R.id.edit_remark);
                Edit_quantity = (EditText) dialog.findViewById(R.id.edit_quantity);
                Edit_Size = (EditText) dialog.findViewById(R.id.edit_size);
                Edit_Weight = (EditText) dialog.findViewById(R.id.edit_weight);

                melting_spinner = (Spinner) dialog.findViewById(R.id.melting_spinner);

                // Spinner Drop down elements
                List<String> categories = new ArrayList<String>();
                categories.add("Select Melting");
                categories.add("92");
                categories.add("84");
                categories.add("75.5");

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(second_level.this, android.R.layout.simple_spinner_item, categories);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                melting_spinner.setAdapter(dataAdapter);


                addQuantity = (Button) dialog.findViewById(R.id.addQuantity);
                addSize = (Button) dialog.findViewById(R.id.addSize);
                addWeight = (Button) dialog.findViewById(R.id.addWeight);

                deleteQuantity = (Button) dialog.findViewById(R.id.delQuantity);
                deleteSize = (Button) dialog.findViewById(R.id.delSize);
                deleteWeight = (Button) dialog.findViewById(R.id.delWeight);

                Cancel = (Button) dialog.findViewById(R.id.cancel);
                Submit = (Button) dialog.findViewById(R.id.Submit);
                Regular = (Button) dialog.findViewById(R.id.Regular);
                Regular_Weight = (Button) dialog.findViewById(R.id.Regular_weight);

                addQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String value = Edit_quantity.getText().toString();
                        if(!value.matches("[a-zA-Z]+") && !value.isEmpty())
                        {
                            int value_int = Integer.parseInt(value);
                            int incremento = value_int + 1 ;

                            if(incremento > 0)
                            {
                                Edit_quantity.setText(String.valueOf(incremento));
                            }
                        }
                    }
                });

                addSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String value = Edit_Size.getText().toString();
                        if(!value.matches("[a-zA-Z]+") && !value.isEmpty())
                        {
                            int value_int = Integer.parseInt(value);
                            int incremento = value_int + 1 ;

                            if(incremento > 0)
                            {
                                Edit_Size.setText(String.valueOf(incremento));
                            }
                        }
                    }
                });

                addWeight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String value = Edit_Weight.getText().toString();
                        if(!value.matches("[a-zA-Z]+") && !value.isEmpty())
                        {
                            int value_int = Integer.parseInt(value);
                            int incremento = value_int + 1 ;

                            if(incremento > 0)
                            {
                                Edit_Weight.setText(String.valueOf(incremento));
                            }
                        }
                    }
                });

                deleteQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String value = Edit_quantity.getText().toString();
                        if(!value.matches("[a-zA-Z]+") && !value.isEmpty())
                        {
                            int value_int = Integer.parseInt(value);
                            int decremento = value_int - 1 ;

                            if(decremento > 0)
                            {
                                Edit_quantity.setText(String.valueOf(decremento));
                            }
                        }
                    }
                });

                deleteSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String value = Edit_Size.getText().toString();
                        if(!value.matches("[a-zA-Z]+") && !value.isEmpty())
                        {
                            int value_int = Integer.parseInt(value);
                            int decremento = value_int - 1 ;

                            if(decremento > 0)
                            {
                                Edit_Size.setText(String.valueOf(decremento));
                            }
                        }
                    }
                });

                deleteWeight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String value = Edit_Weight.getText().toString();
                        if(!value.matches("[a-zA-Z]+") && !value.isEmpty())
                        {
                            int value_int = Integer.parseInt(value);
                            int decremento = value_int - 1 ;

                            if(decremento > 0)
                            {
                                Edit_Weight.setText(String.valueOf(decremento));
                            }
                        }
                    }
                });

                Regular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Edit_Size.setText("Regular");
                    }
                });

                Regular_Weight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Edit_Weight.setText("Regular");
                    }
                });

                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String remarks = Edit_Remarks.getText().toString().trim();
                        String quantity = Edit_quantity.getText().toString().trim();
                        String size = Edit_Size.getText().toString().trim();
                        String weight = Edit_Weight.getText().toString().trim();

                        if (size.isEmpty() || size.contains("0")) {
                            Toast.makeText(second_level.this, "Enter Size", Toast.LENGTH_SHORT).show();
                        } else if (quantity.isEmpty() || quantity.contains("0")) {
                            Toast.makeText(second_level.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                        } else if (weight.isEmpty() || weight.contains("0")) {
                            Toast.makeText(second_level.this, "Enter Weight", Toast.LENGTH_SHORT).show();
                        } else {
                            if (remarks.isEmpty()) {
                                remarks = "N/A";
                            }

                            melting = melting_spinner.getSelectedItem().toString();

                            if(!melting.contains("Select Melting") && !melting.isEmpty())
                            {
                                Edit_instructions = "Remarks : " + remarks + ",\n" + "Quantity : " + quantity + ",\n" + "Size : " + size + ",\n" + "Melting : " + melting + ",\n" + "Weight : " + weight;
                                Log.i("ins",Edit_instructions);
                                dialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(second_level.this, "Select Melting", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                dialog.show();

            }
        });

    }
}

