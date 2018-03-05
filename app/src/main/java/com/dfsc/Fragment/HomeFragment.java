package com.dfsc.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import com.dfsc.Activity.AppPreferences;
import com.dfsc.Adapters.GridAdapter;
import com.dfsc.Modal.GridData;
import com.dfsc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    Context context;
    GridView grid;
    protected AppPreferences appPrefs;
    private ProgressDialog dialog;
    JSONArray dashboard;

    //LinearLayout searchCustomerDetails, serviceStatus, checkFreeService, closeFreeService, customerRegistration;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /*
        searchCustomerDetails = (LinearLayout) view.findViewById(R.id.searchCustomerDetails);
        serviceStatus = (LinearLayout) view.findViewById(R.id.serviceStatus);
        checkFreeService = (LinearLayout) view.findViewById(R.id.checkFreeService);
        closeFreeService = (LinearLayout) view.findViewById(R.id.closeFreeService);
        customerRegistration = (LinearLayout) view.findViewById(R.id.customerRegistration);

        searchCustomerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkNetworkConnection(context)) {
                    startActivity(new Intent(context, SearchCustomerDetails.class));
                } else {
                    Toast.makeText(context, "No Internet available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        serviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkNetworkConnection(context)) {
                    startActivity(new Intent(context, ServiceStatus.class));
                } else {
                    Toast.makeText(context, "No Internet available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkFreeService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkNetworkConnection(context)) {
                    startActivity(new Intent(context, CheckFreeService.class));
                } else {
                    Toast.makeText(context, "No Internet available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        closeFreeService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkNetworkConnection(context)) {
                    startActivity(new Intent(context, CloseFreeService.class));
                } else {
                    Toast.makeText(context, "No Internet available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        customerRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkNetworkConnection(context)) {
                    startActivity(new Intent(context, CustomerRegistration.class));
                } else {
                    Toast.makeText(context, "No Internet available!", Toast.LENGTH_SHORT).show();
                }
            }
        });
         */

        context = getActivity();
        grid = (GridView) view.findViewById(R.id.grid);

        dialog = new ProgressDialog(context);
        appPrefs = new AppPreferences(context);

        dialog.setMessage("Loading Menus..");
        dialog.setCancelable(false);
        dialog.show();
        getProducts();
        return view;
    }

    private void getProducts() {
        final ArrayList<GridData> list = new ArrayList<GridData>();

        try {
            dashboard = new JSONArray(appPrefs.getDashboard());
            for (int i = 0; i < dashboard.length(); i++) {

                list.add(new GridData(dashboard.getJSONObject(i).getString("key"),
                        dashboard.getJSONObject(i).getString("value")));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        grid.setAdapter(new GridAdapter(context, list));
        dialog.dismiss();

        //list.add(new GridData("searchCustomerDetails", R.drawable.search_customer_details, "Search Customer"));
        //list.add(new GridData("serviceStatus", R.drawable.service_status, "Service Details"));
        //list.add(new GridData("checkFreeService", R.drawable.check_free_service, "Check Free Service"));
        //list.add(new GridData("closeFreeService", R.drawable.close_free_service, "Close Free Service"));
        //list.add(new GridData("customerRegistration", R.drawable.customer_registation, "Customer Registration"));


    }

}
