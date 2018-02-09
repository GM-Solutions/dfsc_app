package ascent.com.dfsc.Activity;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ascent.com.dfsc.R;

public class CloseFreeService extends AppCompatActivity {

    Toolbar toolbar;
    Spinner sa_mobile;
    private ProgressDialog dialog;
    protected AppPreferences appPrefs;
    String filter_sel, menu_name,id;
    TextView spinnerError;
    TextInputLayout ucn_input_layout, custId_input_layout;
    EditText et_ucn, et_custId;
    Button submit;
    ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_free_service);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        filter_sel = "";
        menu_name = "";
        id = "";

        dialog = new ProgressDialog(CloseFreeService.this);
        appPrefs = new AppPreferences(CloseFreeService.this);

        flag=(ImageView)findViewById(R.id.flag);
        Picasso.with(CloseFreeService.this).load(appPrefs.getFlag()).into(flag);

        if (getIntent().getStringExtra("menu_name") != null) {
            menu_name = getIntent().getStringExtra("menu_name");
        } else {
            menu_name = "";
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Close Free Service");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ucn_input_layout = (TextInputLayout) findViewById(R.id.ucn_input_layout);
        custId_input_layout = (TextInputLayout) findViewById(R.id.custId_input_layout);
        et_ucn = (EditText) findViewById(R.id.et_ucn);
        et_custId = (EditText) findViewById(R.id.et_custId);
        submit = (Button) findViewById(R.id.submit);
        spinnerError = (TextView) findViewById(R.id.spinnerError);

        sa_mobile = (Spinner) findViewById(R.id.sa_mobile);
        sa_mobile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Mobile selected = (Mobile) arg0.getAdapter().getItem(arg2);
                filter_sel = selected.mobile_no;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        GradientDrawable bgShape = (GradientDrawable)submit.getBackground();
        bgShape.setColor(Color.parseColor("#003763"));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCred();
            }
        });

        if(appPrefs.getCountry().matches("india")){
            custId_input_layout.setVisibility(View.VISIBLE);
        }else{
            custId_input_layout.setVisibility(View.GONE);
            id = "";
        }

        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        getSAMobile();
    }

    private void checkCred() {
        if (!validateSpinner()) {
            return;
        }

        if (!validateUcn()) {
            return;
        }

        if (appPrefs.getCountry().matches("india")) {
            if (!validateId()) {
                return;
            }
        }

        if (Utilities.checkNetworkConnection(getApplicationContext())) {
            dialog.setMessage("Please Wait..");
            dialog.setCancelable(false);
            dialog.show();
            closeService();

        } else {
            Toast.makeText(CloseFreeService.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeService() {
        id=et_custId.getText().toString().trim();

        String JSON_URL = appPrefs.getURL() + "Transaction/close_coupon";
        RequestQueue queue = Volley.newRequestQueue(CloseFreeService.this);

        StringRequest req = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").matches("true")) {
                                //Toast.makeText(CheckFreeService.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(CloseFreeService.this, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(Html.fromHtml("<b>Close Free Service</b>"));
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
                                        new AlertDialog.Builder(CloseFreeService.this, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(Html.fromHtml("<b>Close Free Service</b>"));
                                builder.setMessage(obj.getString("message"));
                                builder.setPositiveButton("OK", null);
                                //builder.setNegativeButton("Cancel", null);
                                builder.show();
                                //Toast.makeText(CheckFreeService.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            //Toast.makeText(CheckFreeService.this, obj.getString("message"), Toast.LENGTH_SHORT).show();


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
                        Toast.makeText(CloseFreeService.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("country", appPrefs.getCountry());
                params.put("ucn", et_ucn.getText().toString().trim());
                params.put("mobile_no", filter_sel);
                params.put("customer_id",id);
                Log.e("params", new JSONObject(params).toString());
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

    private boolean validateUcn() {
        if (et_ucn.getText().toString().trim().isEmpty()) {
            ucn_input_layout.setError("Please enter UCN");
            return false;
        } else {
            ucn_input_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateId() {
        if (et_custId.getText().toString().trim().isEmpty()) {
            custId_input_layout.setError("Please enter Customer ID");
            return false;
        } else {
            custId_input_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateSpinner() {
        if (filter_sel.trim().isEmpty()) {
            spinnerError.setVisibility(View.VISIBLE);
            spinnerError.setText("Please select mobile number");
            return false;
        } else {
            spinnerError.setVisibility(View.GONE);
        }
        return true;
    }

    private void getSAMobile() {
        String JSON_URL = appPrefs.getURL() + "User/service_man_list";
        RequestQueue queue = Volley.newRequestQueue(CloseFreeService.this);

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

                            ArrayAdapter<Mobile> adapter = new ArrayAdapter<Mobile>(CloseFreeService.this,
                                    R.layout.list_item, R.id.name, list);
                            sa_mobile.setAdapter(adapter);
                            dialog.dismiss();

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
                        Toast.makeText(CloseFreeService.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
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
