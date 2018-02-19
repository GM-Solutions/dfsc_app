package ascent.com.dfsc.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ascent.com.dfsc.Activity.CheckFreeService;
import ascent.com.dfsc.Activity.CloseFreeService;
import ascent.com.dfsc.Activity.CustomerRegistration;
import ascent.com.dfsc.Activity.RiderRegistration;
import ascent.com.dfsc.Activity.SearchCustomerDetails;
import ascent.com.dfsc.Activity.ServiceStatus;
import ascent.com.dfsc.Activity.Utilities;
import ascent.com.dfsc.Modal.GridData;
import ascent.com.dfsc.R;

/**
 * Created by Admin on 1/11/2018.
 */

public class GridAdapter extends BaseAdapter {

    Context c;
    ArrayList<GridData> list;
    private static LayoutInflater inflater = null;

    public GridAdapter(Context context, ArrayList<GridData> list1) {
        c = context;
        list = list1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        ImageView image;
        TextView name;

        inflater = (LayoutInflater) this.c.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.grid_item, null);

        name = (TextView) rowView.findViewById(R.id.name);
        image = (ImageView) rowView.findViewById(R.id.image);

        //name.setText(list.get(position).name);

        if (list.get(position).id.matches("search_customer_detail")) {
            image.setImageResource(R.drawable.search_customer_details);
            name.setText(c.getResources().getString(R.string.search_page_head));
        } else if (list.get(position).id.matches("service_status")) {
            image.setImageResource(R.drawable.service_status);
            name.setText(c.getResources().getString(R.string.service_status_head));
        } else if (list.get(position).id.matches("check_free_services")) {
            image.setImageResource(R.drawable.check_free_service);
            name.setText(c.getResources().getString(R.string.check_free_page_head));
        } else if (list.get(position).id.matches("close_free_services")) {
            image.setImageResource(R.drawable.close_free_service);
            name.setText(c.getResources().getString(R.string.close_free_page_head));
        } else if (list.get(position).id.matches("customer_registration")) {
            image.setImageResource(R.drawable.customer_registation);
            name.setText(c.getResources().getString(R.string.cust_reg_head));
        } else if (list.get(position).id.matches("rider_registration")) {
            image.setImageResource(R.drawable.rider_registration);
            name.setText(c.getResources().getString(R.string.rider_reg_head));
        }

        //image.setImageResource(list.get(position).image);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).id.matches("search_customer_detail")) {
                    if (Utilities.checkNetworkConnection(c)) {
                        c.startActivity(new Intent(c, SearchCustomerDetails.class));
                    } else {
                        Toast.makeText(c, "No Internet available!", Toast.LENGTH_SHORT).show();
                    }
                } else if (list.get(position).id.matches("service_status")) {
                    if (Utilities.checkNetworkConnection(c)) {
                        c.startActivity(new Intent(c, ServiceStatus.class));
                    } else {
                        Toast.makeText(c, "No Internet available!", Toast.LENGTH_SHORT).show();
                    }
                } else if (list.get(position).id.matches("check_free_services")) {
                    if (Utilities.checkNetworkConnection(c)) {
                        Intent intent=new Intent(c, CheckFreeService.class);
                        intent.putExtra("menu_name",list.get(position).id);
                        c.startActivity(intent);
                    } else {
                        Toast.makeText(c, "No Internet available!", Toast.LENGTH_SHORT).show();
                    }
                } else if (list.get(position).id.matches("close_free_services")) {
                    if (Utilities.checkNetworkConnection(c)) {
                        Intent intent=new Intent(c, CloseFreeService.class);
                        intent.putExtra("menu_name",list.get(position).id);
                        c.startActivity(intent);
                    } else {
                        Toast.makeText(c, "No Internet available!", Toast.LENGTH_SHORT).show();
                    }
                } else if (list.get(position).id.matches("customer_registration")) {
                    if (Utilities.checkNetworkConnection(c)) {
                        Intent intent=new Intent(c, CustomerRegistration.class);
                        intent.putExtra("menu_name",list.get(position).id);
                        c.startActivity(intent);
                    } else {
                        Toast.makeText(c, "No Internet available!", Toast.LENGTH_SHORT).show();
                    }
                }else if (list.get(position).id.matches("rider_registration")) {
                    if (Utilities.checkNetworkConnection(c)) {
                        Intent intent=new Intent(c, RiderRegistration.class);
                        intent.putExtra("menu_name",list.get(position).id);
                        c.startActivity(intent);
                    } else {
                        Toast.makeText(c, "No Internet available!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rowView;
    }
}
