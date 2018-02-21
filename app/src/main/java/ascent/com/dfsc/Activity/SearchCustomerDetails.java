package ascent.com.dfsc.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

import ascent.com.dfsc.R;

public class SearchCustomerDetails extends AppCompatActivity {

    Toolbar toolbar;
    TextView chassis_number, cust_id, cust_name, cust_no, veh_no, colon, textVehNo, mobile_head, name_head, id_head, chassis_head;
    EditText search, cc;
    ImageButton search_bt;
    Spinner filter;
    private ProgressDialog dialog;
    protected AppPreferences appPrefs;
    String filter_sel;
    Button checkFreeService, custReg;
    LinearLayout ll1;
    CardView searchResult;
    TextView searchError,toolbar_text;
    String intent_name, intent_mobile, intent_veh_no,intent_chassis;
    ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_customer_details);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        filter_sel = "";
        dialog = new ProgressDialog(SearchCustomerDetails.this);
        appPrefs = new AppPreferences(SearchCustomerDetails.this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.search_page_head));

        toolbar_text=(TextView)findViewById(R.id.toolbar_text);
        toolbar_text.setText(getResources().getString(R.string.search_page_head));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        flag = (ImageView) findViewById(R.id.flag);
        Picasso.with(SearchCustomerDetails.this).load(appPrefs.getFlag()).into(flag);

        searchResult = (CardView) findViewById(R.id.searchResult);
        searchResult.setVisibility(View.GONE);

        chassis_number = (TextView) findViewById(R.id.chassis_number);
        cust_id = (TextView) findViewById(R.id.cust_id);
        cust_name = (TextView) findViewById(R.id.cust_name);
        cust_no = (TextView) findViewById(R.id.cust_no);
        veh_no = (TextView) findViewById(R.id.veh_no);
        colon = (TextView) findViewById(R.id.colon);
        textVehNo = (TextView) findViewById(R.id.textVehNo);
        mobile_head = (TextView) findViewById(R.id.mobile_head);
        name_head = (TextView) findViewById(R.id.name_head);
        id_head = (TextView) findViewById(R.id.id_head);
        chassis_head = (TextView) findViewById(R.id.chassis_head);

        chassis_head.setText(getResources().getString(R.string.chassis_head));
        id_head.setText(getResources().getString(R.string.id_head));
        name_head.setText(getResources().getString(R.string.name_head));
        mobile_head.setText(getResources().getString(R.string.mobile_head));
        textVehNo.setText(getResources().getString(R.string.textVehNo));

        searchError = (TextView) findViewById(R.id.searchError);
        searchError.setVisibility(View.GONE);

        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll1.setVisibility(View.GONE);

        search = (EditText) findViewById(R.id.search);
        search.setHint(getResources().getString(R.string.search));
        cc = (EditText) findViewById(R.id.cc);

        search_bt = (ImageButton) findViewById(R.id.search_bt);

        checkFreeService = (Button) findViewById(R.id.checkFreeService);
        custReg = (Button) findViewById(R.id.custReg);

        filter = (Spinner) findViewById(R.id.filter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Filters selected = (Filters) arg0.getAdapter().getItem(arg2);
                filter_sel = selected.id;
                if (filter_sel.matches("mobile_no")) {
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

                } else {
                    cc.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideSoftKeyboard(SearchCustomerDetails.this);
                if (search.getText().toString().isEmpty()) {
                    searchError.setText(getResources().getString(R.string.validate_search));
                    searchError.setVisibility(View.VISIBLE);
                } else {
                    searchError.setVisibility(View.GONE);
                    dialog.setMessage(getResources().getString(R.string.please_wait));
                    dialog.setCancelable(false);
                    dialog.show();
                    searchCustomer();
                }
            }
        });

        checkFreeService.setEnabled(false);
        custReg.setEnabled(false);

        checkFreeService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideSoftKeyboard(SearchCustomerDetails.this);
                if (checkFreeService.isEnabled()) {
                    Intent intent = new Intent(SearchCustomerDetails.this, CheckFreeService.class);
                    intent.putExtra("cust_id", cust_id.getText());
                    intent.putExtra("veh_reg_no", intent_veh_no);
                    startActivity(intent);
                } else {
                    Toast.makeText(SearchCustomerDetails.this, getResources().getString(R.string.not_available), Toast.LENGTH_SHORT).show();
                }
            }
        });

        custReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideSoftKeyboard(SearchCustomerDetails.this);
                if (custReg.isEnabled()) {
                    if (custReg.getText().toString().matches(getResources().getString(R.string.cust_reg))) {
                        Intent intent = new Intent(SearchCustomerDetails.this, CustomerRegistration.class);
                        //intent_name, intent_mobile, intent_veh_no,intent_chassis
                        intent.putExtra("intent_chassis", intent_chassis);
                        intent.putExtra("intent_name", intent_name);
                        intent.putExtra("intent_mobile", intent_mobile);
                        intent.putExtra("intent_veh_no", intent_veh_no);
                        intent.putExtra("from_search", "true");
                        startActivity(intent);
                    } else if (custReg.getText().toString().matches(getResources().getString(R.string.rider_reg))) {
                        Intent intent = new Intent(SearchCustomerDetails.this, RiderRegistration.class);
                        intent.putExtra("intent_veh_no", intent_veh_no);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(SearchCustomerDetails.this, getResources().getString(R.string.not_available), Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSearchFilter();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void searchCustomer() {
        String JSON_URL = appPrefs.getURL() + "Transaction/search_customer";
        RequestQueue queue = Volley.newRequestQueue(SearchCustomerDetails.this);

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
                                JSONArray arr = obj.getJSONArray("customer_details");

                                chassis_number.setText(arr.getJSONObject(0).getString("chassis"));
                                cust_id.setText(arr.getJSONObject(0).getString("customer_id"));
                                cust_name.setText(arr.getJSONObject(0).getString("customer_name"));
                                cust_no.setText(arr.getJSONObject(0).getString("mobile_no"));
                                veh_no.setText(arr.getJSONObject(0).getString("veh_reg_no"));

                                //intent_name,intent_mobile,intent_veh_no
                                intent_name = arr.getJSONObject(0).getString("customer_name");
                                intent_mobile = arr.getJSONObject(0).getString("mobile_no");
                                intent_veh_no = arr.getJSONObject(0).getString("veh_reg_no");
                                intent_chassis = arr.getJSONObject(0).getString("chassis");

                                ll1.setVisibility(View.VISIBLE);
                                searchResult.setVisibility(View.VISIBLE);

                                if (!(appPrefs.getCountry().matches("india"))) {
                                    if (arr.getJSONObject(0).getString("register_customer").matches("true")) {
                                        custReg.setText(getResources().getString(R.string.cust_reg));
                                        custReg.setEnabled(true);
                                        GradientDrawable bgShape = (GradientDrawable) custReg.getBackground();
                                        bgShape.setColor(Color.parseColor("#003763"));
                                    } else {
                                        custReg.setText(getResources().getString(R.string.rider_reg));
                                        custReg.setEnabled(true);
                                        GradientDrawable bgShape = (GradientDrawable) custReg.getBackground();
                                        bgShape.setColor(Color.parseColor("#003763"));
                                    }

                                    veh_no.setVisibility(View.VISIBLE);
                                    colon.setVisibility(View.VISIBLE);
                                    textVehNo.setVisibility(View.VISIBLE);

                                } else {
                                    if (arr.getJSONObject(0).getString("register_customer").matches("true")) {
                                        custReg.setText(getResources().getString(R.string.cust_reg));
                                        custReg.setEnabled(true);
                                        GradientDrawable bgShape = (GradientDrawable) custReg.getBackground();
                                        bgShape.setColor(Color.parseColor("#003763"));
                                    } else {
                                        custReg.setText(getResources().getString(R.string.cust_reg));
                                        custReg.setEnabled(false);
                                        GradientDrawable bgShape = (GradientDrawable) custReg.getBackground();
                                        bgShape.setColor(Color.parseColor("#717171"));
                                    }

                                    veh_no.setVisibility(View.GONE);
                                    colon.setVisibility(View.GONE);
                                    textVehNo.setVisibility(View.GONE);

                                }

                                checkFreeService.setText(arr.getJSONObject(0).getJSONObject("service_detail").getString("label"));

                                if (arr.getJSONObject(0).getJSONObject("service_detail").getString("service_status").matches("true")) {
                                    checkFreeService.setEnabled(true);
                                    GradientDrawable bgShape = (GradientDrawable) checkFreeService.getBackground();
                                    bgShape.setColor(Color.parseColor("#003763"));
                                } else {
                                    checkFreeService.setEnabled(false);
                                    GradientDrawable bgShape = (GradientDrawable) checkFreeService.getBackground();
                                    bgShape.setColor(Color.parseColor("#717171"));
                                }

                                if(appPrefs.getGroup().matches("sales_executive")){
                                    checkFreeService.setVisibility(View.GONE);
                                }else{
                                    checkFreeService.setVisibility(View.VISIBLE);
                                }


                            } else {
                                ll1.setVisibility(View.GONE);
                                searchResult.setVisibility(View.GONE);
                                Toast.makeText(SearchCustomerDetails.this, obj.getString("message"), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(SearchCustomerDetails.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
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
        list.add(new Filters("chassis", getResources().getString(R.string.chassis)));
        list.add(new Filters("veh_reg_no", getResources().getString(R.string.vehicle_no)));
        list.add(new Filters("customer_id", getResources().getString(R.string.cust_id)));
        list.add(new Filters("mobile_no", getResources().getString(R.string.cust_mobile)));
        ArrayAdapter<Filters> adapter = new ArrayAdapter<Filters>(SearchCustomerDetails.this,
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

    public class Customer {
        public String chassis;
        public String customer_id;
        public String mobile_no;
        public String register_customer;

        public Customer(String chassis, String customer_id, String mobile_no, String register_customer) {
            this.chassis = chassis;
            this.customer_id = customer_id;
            this.mobile_no = mobile_no;
            this.register_customer = register_customer;
        }

        @Override
        public String toString() {
            return chassis;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
