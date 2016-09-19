package infotech.atom.com.creativejewel;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2/20/2016.
 */
public class Listing_Adapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    List<Orders_model> Orders_model;

    public Listing_Adapter(Activity activity, List<Orders_model> Orders_model) {
        this.activity = activity;
        this.Orders_model = Orders_model;
    }

    @Override
    public int getCount() {
        return Orders_model.size();
    }

    @Override
    public Object getItem(int location) {
        return Orders_model.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {
        TextView order_no,order_created,details;

        try {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.order_row, null);

            order_no = (TextView) convertView.findViewById(R.id.Order_no);
            order_created = (TextView) convertView.findViewById(R.id.created);
            details = (TextView) convertView.findViewById(R.id.order_details);

            Orders_model ordersModel = Orders_model.get(position);

            order_no.setText(String.valueOf(ordersModel.getOrder_no()));
            order_created.setText(String.valueOf(ordersModel.getCreated_date()));

            String Created_Date = String.valueOf(ordersModel.getCreated_date());
            String Order_nos = String.valueOf(ordersModel.getOrder_no());
            String volume1 = String.valueOf(ordersModel.getVolume1());
            String volume2 = String.valueOf(ordersModel.getVolume2());
            String volume3 = String.valueOf(ordersModel.getVolume3());
            String newArr = String.valueOf(ordersModel.getNew_arrivals());

            details.setText("Volume1 : " + volume1 + "\n" + "Volume2 : " + volume2 + "\n" + "Volume3 : " + volume3 + "\n" + "New Arrivals : " + newArr);

        } catch (NullPointerException npe) {
            Toast.makeText(activity, "Null pointer exception adapter", Toast.LENGTH_SHORT).show();
        }

        return convertView;
    }

}