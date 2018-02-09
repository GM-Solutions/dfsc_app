package ascent.com.dfsc.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ascent.com.dfsc.R;

public class ServiceStatus extends AppCompatActivity {

    Toolbar toolbar;
    EditText search,cc;
    ImageButton search_bt;
    Spinner filter;
    private ProgressDialog dialog;
    protected AppPreferences appPrefs;
    String filter_sel;
    RecyclerView statusList;
    LinearLayoutManager LayoutManager;
    RecyclerVerticalAdapter verticalAdapter;
    TextView searchError;
    TextView chassis_number, cust_id,veh_reg_no;
    CardView searchResult;
    ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_status);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        filter_sel = "";

        dialog = new ProgressDialog(ServiceStatus.this);
        appPrefs = new AppPreferences(ServiceStatus.this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Service Status");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        flag=(ImageView)findViewById(R.id.flag);
        Picasso.with(ServiceStatus.this).load(appPrefs.getFlag()).into(flag);

        searchResult = (CardView) findViewById(R.id.searchResult);
        searchResult.setVisibility(View.GONE);

        chassis_number = (TextView) findViewById(R.id.chassis_number);
        cust_id = (TextView) findViewById(R.id.cust_id);
        veh_reg_no = (TextView) findViewById(R.id.veh_reg_no);

        statusList = (RecyclerView) findViewById(R.id.statusList);
        statusList.setVisibility(View.GONE);
        LayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        search = (EditText) findViewById(R.id.search);
        cc = (EditText) findViewById(R.id.cc);

        searchError=(TextView)findViewById(R.id.searchError);
        searchError.setVisibility(View.GONE);
        
        search_bt = (ImageButton) findViewById(R.id.search_bt);
        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search.getText().toString().isEmpty()){
                    searchError.setText("Please enter search string");
                    searchError.setVisibility(View.VISIBLE);
                }else {
                    searchError.setVisibility(View.GONE);
                    dialog.setMessage("Please Wait..");
                    dialog.setCancelable(false);
                    dialog.show();
                    search();
                }
            }
        });

        filter = (Spinner) findViewById(R.id.filter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Filters selected = (Filters) arg0.getAdapter().getItem(arg2);
                filter_sel = selected.id;
                if(filter_sel.matches("mobile_no")){
                    cc.setVisibility(View.VISIBLE);
                    cc.setEnabled(false);

                    /*
                    if(appPrefs.getCountry().matches("uganda")){
                        cc.setText("255");
                    }else if(appPrefs.getCountry().matches("kenya")){
                        cc.setText("254");
                    }else if(appPrefs.getCountry().matches("india")){
                        cc.setText("91");
                    }
                     */
                    cc.setText(appPrefs.getCode());

                }else{
                    cc.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getSearchFilter();
    }

    private void search() {
        String JSON_URL = appPrefs.getURL() + "Transaction/service_status";
        RequestQueue queue = Volley.newRequestQueue(ServiceStatus.this);

        StringRequest req = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            //Log.d("REsponse", obj.getString("message"));
                            //Toast.makeText(Login.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            if (obj.getString("status").matches("true")) {

                                //Toast.makeText(SearchCustomerDetails.this, "Reading OTP, Please wait..", Toast.LENGTH_LONG).show();
                                JSONArray arr = obj.getJSONObject("service").getJSONArray("service_status");

                                searchResult.setVisibility(View.VISIBLE);
                                statusList.setVisibility(View.VISIBLE);

                                chassis_number.setText(arr.getJSONObject(0).getString("chassis"));
                                cust_id.setText(arr.getJSONObject(0).getString("customer_id"));
                                veh_reg_no.setText(arr.getJSONObject(0).getString("veh_reg_no"));

                                JSONArray arr1=arr.getJSONObject(0).getJSONArray("coupon");
                                List<Servicestatus> data = new ArrayList<>();
                                for (int i = 0; i < arr1.length(); i++) {
                                    //job_id, title,status,customer,description,start_date,end_date,services,alloted
                                    data.add(new Servicestatus(arr1.getJSONObject(i).getString("service_type"),
                                            arr1.getJSONObject(i).getString("status")));
                                }

                                statusList.setLayoutManager(LayoutManager);
                                verticalAdapter = new RecyclerVerticalAdapter(data, getApplicationContext());
                                statusList.setAdapter(verticalAdapter);
                                verticalAdapter.notifyDataSetChanged();


                            } else {
                                searchResult.setVisibility(View.GONE);
                                statusList.setVisibility(View.GONE);
                                Toast.makeText(ServiceStatus.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (Throwable t) {
                            Log.e("REsponse", "Could not parse malformed JSON: \"" + response + "\"");
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(Login.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Toast.makeText(ServiceStatus.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("country", appPrefs.getCountry());
                params.put("filter", filter_sel);
                params.put("search", search.getText().toString().trim());
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "bajaj:indian@1361";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(req);

        req.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getSearchFilter() {
        final ArrayList<Filters> list = new ArrayList<Filters>();
        list.add(new Filters("chassis", "Chassis"));
        list.add(new Filters("veh_reg_no", "Vehicle-No"));
        list.add(new Filters("customer_id", "Customer-ID"));
        list.add(new Filters("mobile_no", "Customer-Mobile"));
        ArrayAdapter<Filters> adapter = new ArrayAdapter<Filters>(ServiceStatus.this,
                R.layout.list_item, R.id.name, list);
        filter.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class Filters {
        public String id;
        public String value;

        public Filters(String id, String value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public class Servicestatus {

        public String type, status;

        Servicestatus(String type, String status) {

            this.type = type;
            this.status = status;

        }
    }

    public class RecyclerVerticalAdapter extends RecyclerView.Adapter<RecyclerVerticalAdapter.MyViewHolder> {

        List<Servicestatus> verticalList = Collections.emptyList();
        Context context;

        public RecyclerVerticalAdapter(List<Servicestatus> verticalList, Context context) {
            this.verticalList = verticalList;
            this.context = context;

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView type, status;
            LinearLayout one;

            public MyViewHolder(View view) {
                super(view);

                type = (TextView) view.findViewById(R.id.type);
                status = (TextView) view.findViewById(R.id.status);

            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_status_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.type.setText(verticalList.get(position).type);
            holder.status.setText(verticalList.get(position).status);

        }

        @Override
        public int getItemCount() {
            return verticalList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

}
