package ascent.com.dfsc.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ascent.com.dfsc.R;

public class CustomerRegistration extends AppCompatActivity {

    Toolbar toolbar;
    EditText search, et_id, et_name, et_phone, et_date, et_cc;
    ImageButton search_bt;
    TextInputLayout id_input_layout, name_input_layout, cc_input_layout, phone_input_layout, date_input_layout;
    Button submit;
    private ProgressDialog dialog;
    protected AppPreferences appPrefs;
    TextView searchError, spinnerError;
    Spinner sa_mobile;
    String mobile_sel, search_filter, menu_name;
    int month, year, day;
    ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mobile_sel = "";
        search_filter = "";
        menu_name = "";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Customer Registeration");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().getStringExtra("menu_name") != null) {
            menu_name = getIntent().getStringExtra("menu_name");
        } else {
            menu_name = "customer_registration";
        }

        dialog = new ProgressDialog(CustomerRegistration.this);
        appPrefs = new AppPreferences(CustomerRegistration.this);

        flag = (ImageView) findViewById(R.id.flag);
        Picasso.with(CustomerRegistration.this).load(appPrefs.getFlag()).into(flag);

        search = (EditText) findViewById(R.id.search);

        searchError = (TextView) findViewById(R.id.searchError);
        searchError.setVisibility(View.GONE);
        spinnerError = (TextView) findViewById(R.id.spinnerError);
        spinnerError.setVisibility(View.GONE);

        et_id = (EditText) findViewById(R.id.et_id);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_cc = (EditText) findViewById(R.id.et_cc);
        et_date = (EditText) findViewById(R.id.et_date);

        search_bt = (ImageButton) findViewById(R.id.search_bt);

        id_input_layout = (TextInputLayout) findViewById(R.id.id_input_layout);
        name_input_layout = (TextInputLayout) findViewById(R.id.name_input_layout);
        phone_input_layout = (TextInputLayout) findViewById(R.id.phone_input_layout);
        cc_input_layout = (TextInputLayout) findViewById(R.id.cc_input_layout);
        date_input_layout = (TextInputLayout) findViewById(R.id.date_input_layout);

        submit = (Button) findViewById(R.id.submit);
        sa_mobile = (Spinner) findViewById(R.id.sa_mobile);

        if (appPrefs.getCountry().matches("india")) {
            search.setHint("Search Chassis..");
            id_input_layout.setHint("Customer ID");
            search_filter = "chassis";
            sa_mobile.setVisibility(View.GONE);
            mobile_sel = "";
            id_input_layout.setHint("Chassis Number");
            et_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

            //et_cc.setText("+91");


        } else {

            search.setHint("Search Vehicle No..");
            id_input_layout.setHint("Vehicle Registration Number");
            search_filter = "veh_reg_no";
            sa_mobile.setVisibility(View.VISIBLE);
            id_input_layout.setHint("Vehicle Registration Number");
            et_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            dialog.setMessage("Please Wait..");
            dialog.setCancelable(false);
            dialog.show();
            getSAMobile();
        }

        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getText().toString().isEmpty()) {
                    searchError.setText("Please enter search string");
                    searchError.setVisibility(View.VISIBLE);
                } else {
                    searchError.setVisibility(View.GONE);
                    dialog.setMessage("Please Wait..");
                    dialog.setCancelable(false);
                    dialog.show();
                    searchCustomer();
                }
            }
        });


        sa_mobile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Mobile selected = (Mobile) arg0.getAdapter().getItem(arg2);
                mobile_sel = selected.mobile_no;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCred();
            }
        });
/*
 et_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dpd = new DatePickerDialog(CustomerRegistration.this, date, year, month, day);
                    dpd.getDatePicker().setMinDate(c.getTimeInMillis());
                    dpd.setTitle("Select Product Purchase Date");
                    dpd.show();
                }
            }
        });
 */


        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(CustomerRegistration.this, date, year, month, day);
                dpd.getDatePicker().setMinDate(c.getTimeInMillis());
                dpd.setTitle("Select Product Purchase Date");
                dpd.show();
            }
        });

        et_id.setEnabled(false);
        et_name.setEnabled(false);
        et_phone.setEnabled(false);
        et_date.setEnabled(false);
        sa_mobile.setEnabled(true);
        submit.setEnabled(false);
        GradientDrawable bgShape = (GradientDrawable) submit.getBackground();
        bgShape.setColor(Color.parseColor("#717171"));
    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            year = arg1;
            month = arg2 + 1;
            day = arg3;

            String date = day + "/" + month + "/" + year;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date d = dateFormat.parse(date);
                System.out.println("DATE" + d);
                System.out.println("Formated" + dateFormat.format(d));
                et_date.setText(dateFormat.format(d));
            } catch (Exception e) {
                //java.text.ParseException: Unparseable date: Geting error
                System.out.println("Excep" + e);
            }
            //start = day + "/" + month + "/" + year;
        }
    };

    private void checkCred() {

        if (!(appPrefs.getCountry().matches("india"))) {
            if (!validateMobile()) {
                return;
            }
        }

        if (!validateId()) {
            return;
        }

        if (!validateName()) {
            return;
        }

        if (!validateOMobile()) {
            return;
        }

        if (!validateDate()) {
            return;
        }


        if (Utilities.checkNetworkConnection(CustomerRegistration.this)) {
            dialog.setMessage("Please Wait..");
            dialog.setCancelable(false);
            dialog.show();
            cust_reg();
        } else {
            Toast.makeText(CustomerRegistration.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validateMobile() {
        if (mobile_sel.trim().isEmpty()) {
            spinnerError.setVisibility(View.VISIBLE);
            spinnerError.setText("Select Mobile Number");
            return false;
        } else {
            spinnerError.setVisibility(View.GONE);
        }

        return true;
    }

    private boolean validateId() {
        if (et_id.getText().toString().trim().isEmpty()) {
            if (appPrefs.getCountry().matches("india")) {
                id_input_layout.setError("Enter Chassis number");
            } else {
                id_input_layout.setError("Enter Vehicle Registration number");
            }
            return false;
        } else {
            id_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName() {
        if (et_name.getText().toString().trim().isEmpty()) {
            name_input_layout.setError("Enter name");
            return false;
        } else {
            name_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOMobile() {
        if (appPrefs.getCountry().matches("india")) {
            if (et_phone.getText().toString().trim().isEmpty()) {
                phone_input_layout.setError("Enter mobile number");
                return false;
            } else if (et_phone.getText().length() != 10) {
                phone_input_layout.setError("Please enter valid 10-digit mobile number");
                return false;
            } else {
                phone_input_layout.setErrorEnabled(false);
            }
        } else {
            if (et_phone.getText().toString().trim().isEmpty()) {
                phone_input_layout.setError("Enter mobile number");
                return false;
            } else if (et_phone.getText().length() != 9) {
                phone_input_layout.setError("Please enter valid 9-digit mobile number");
                return false;
            } else {
                phone_input_layout.setErrorEnabled(false);
            }
        }


        return true;
    }

    private boolean validateDate() {
        if (et_date.getText().toString().trim().isEmpty()) {
            date_input_layout.setError("Enter product purchase date");
            return false;
        } else {
            date_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private void searchCustomer() {
        String JSON_URL = appPrefs.getURL() + "Transaction/search_customer";
        RequestQueue queue = Volley.newRequestQueue(CustomerRegistration.this);

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

                                if (appPrefs.getCountry().matches("india")) {
                                    et_id.setText(arr.getJSONObject(0).getString("chassis"));
                                } else {
                                    et_id.setText(arr.getJSONObject(0).getString("veh_reg_no"));
                                }


                                et_name.setText(arr.getJSONObject(0).getString("customer_name"));
                                et_phone.setText(arr.getJSONObject(0).getString("mobile_no"));

                                if (arr.getJSONObject(0).getString("register_customer").matches("true")) {
                                    et_id.setEnabled(true);
                                    et_name.setEnabled(true);
                                    et_phone.setEnabled(true);
                                    et_date.setEnabled(true);
                                    sa_mobile.setEnabled(true);
                                    submit.setEnabled(true);

                                    date_input_layout.setVisibility(View.VISIBLE);

                                    /*
                                    if (appPrefs.getCountry().matches("uganda")) {
                                        et_cc.setText("255");
                                    } else if (appPrefs.getCountry().matches("kenya")) {
                                        et_cc.setText("254");
                                    } else if (appPrefs.getCountry().matches("india")) {
                                        et_cc.setText("91");
                                    }
                                     */

                                    et_cc.setText(appPrefs.getCode());

                                    GradientDrawable bgShape = (GradientDrawable) submit.getBackground();
                                    bgShape.setColor(Color.parseColor("#003763"));

                                } else {
                                    date_input_layout.setVisibility(View.GONE);

                                    et_id.setEnabled(false);
                                    et_name.setEnabled(false);
                                    et_phone.setEnabled(false);
                                    et_date.setEnabled(false);
                                    sa_mobile.setEnabled(true);
                                    et_cc.setEnabled(false);
                                    submit.setEnabled(false);
                                    GradientDrawable bgShape = (GradientDrawable) submit.getBackground();
                                    bgShape.setColor(Color.parseColor("#717171"));
                                }

                            } else {
                                search.setText("");
                                et_id.setText("");
                                et_name.setText("");
                                et_phone.setText("");
                                et_date.setText("");

                                date_input_layout.setVisibility(View.VISIBLE);

                                et_id.setEnabled(true);
                                et_name.setEnabled(true);
                                et_phone.setEnabled(true);
                                et_date.setEnabled(true);
                                sa_mobile.setEnabled(true);
                                et_cc.setEnabled(false);
                                submit.setEnabled(true);
                                GradientDrawable bgShape = (GradientDrawable) submit.getBackground();
                                bgShape.setColor(Color.parseColor("#003763"));

                                /*
                                    if (appPrefs.getCountry().matches("uganda")) {
                                        et_cc.setText("255");
                                    } else if (appPrefs.getCountry().matches("kenya")) {
                                        et_cc.setText("254");
                                    } else if (appPrefs.getCountry().matches("india")) {
                                        et_cc.setText("91");
                                    }
                                     */

                                et_cc.setText(appPrefs.getCode());

                                Toast.makeText(CustomerRegistration.this, obj.getString("message"), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CustomerRegistration.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                String filter;
                params.put("country", appPrefs.getCountry());
                params.put("filter", search_filter);
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

    private void getSAMobile() {
        String JSON_URL = appPrefs.getURL() + "User/service_man_list";
        RequestQueue queue = Volley.newRequestQueue(CustomerRegistration.this);

        StringRequest req = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("employee_dtl");

                            final ArrayList<Mobile> list = new ArrayList<Mobile>();
                            list.add(new Mobile("Select mobile number", "", "Select mobile number"));
                            for (int i = 0; i < arr.length(); i++) {
                                list.add(new Mobile(arr.getJSONObject(i).getString("firstname") + " " + arr.getJSONObject(i).getString("lastname")
                                        , arr.getJSONObject(i).getString("mobile_no"),
                                        arr.getJSONObject(i).getString("firstname") + " " + arr.getJSONObject(i).getString("lastname") + " (" + arr.getJSONObject(i).getString("mobile_no") + ")"));
                            }

                            ArrayAdapter<Mobile> adapter = new ArrayAdapter<Mobile>(CustomerRegistration.this,
                                    R.layout.list_item, R.id.name, list);
                            sa_mobile.setAdapter(adapter);
                            dialog.dismiss();

                            //Toast.makeText(CheckFreeService.this, obj.getString("message"), Toast.LENGTH_SHORT).show();


                        } catch (Throwable t) {
                            dialog.dismiss();
                            Log.e("REsponse", "Could not parse malformed JSON: \"" + response + "\"");
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(Login.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Toast.makeText(CustomerRegistration.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("country", appPrefs.getCountry());
                params.put("group", appPrefs.getGroup());
                params.put("mobile_no", appPrefs.getMobile());
                params.put("user_id", appPrefs.getUserId());
                params.put("menu_name", menu_name);
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

    private void cust_reg() {
        String JSON_URL = appPrefs.getURL() + "Transaction/customer_registration";
        RequestQueue queue = Volley.newRequestQueue(CustomerRegistration.this);

        StringRequest req = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").matches("true")) {

                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(CustomerRegistration.this, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(Html.fromHtml("<b>Customer Registration Successfull</b>"));
                                builder.setMessage(obj.getString("message"));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                //builder.setNegativeButton("Cancel", null);
                                builder.show();

                            } else {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(CustomerRegistration.this, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(Html.fromHtml("<b>Customer Registration Failed</b>"));
                                builder.setMessage(obj.getString("message"));
                                builder.setPositiveButton("OK", null);
                                //builder.setNegativeButton("Cancel", null);
                                builder.show();
                                //Toast.makeText(CustomerRegistration.this, obj.getString("message"), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CustomerRegistration.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("country", appPrefs.getCountry());
                params.put("veh_reg_no", et_id.getText().toString().trim());
                params.put("owner_name", et_name.getText().toString().trim());
                params.put("purchase_date", et_date.getText().toString().trim());
                params.put("owner_mobile_no", et_phone.getText().toString().trim());
                params.put("mobile_no", mobile_sel);

                params.put("customer-id", "");
                params.put("group", appPrefs.getGroup());
                params.put("user_id", appPrefs.getUserId());
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

    public class Mobile {
        public String name;
        public String mobile_no;
        public String comb;

        public Mobile(String name, String mobile_no, String comb) {
            this.name = name;
            this.mobile_no = mobile_no;
            this.comb = comb;
        }

        @Override
        public String toString() {
            return comb;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
