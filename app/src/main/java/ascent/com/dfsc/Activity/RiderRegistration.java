package ascent.com.dfsc.Activity;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

public class RiderRegistration extends AppCompatActivity {

    Toolbar toolbar;
    EditText search, et_id, et_name, et_phone,et_cc;
    TextInputLayout id_input_layout, name_input_layout, phone_input_layout,cc_input_layout;
    private ProgressDialog dialog;
    protected AppPreferences appPrefs;
    Spinner sa_mobile;
    TextView spinnerError,toolbar_text;
    String mobile_sel;
    Button submit;
    String intent_veh_no, menu_name;
    ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_registration);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mobile_sel = "";
        intent_veh_no = "";
        menu_name = "";

        if (getIntent().getStringExtra("menu_name") != null) {
            menu_name = getIntent().getStringExtra("menu_name");
        } else {
            menu_name = "";
        }

        if (getIntent().getStringExtra("intent_veh_no") != null) {
            intent_veh_no = getIntent().getStringExtra("intent_veh_no");
        } else {
            intent_veh_no = "";
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rider Registeration");

        toolbar_text=(TextView)findViewById(R.id.toolbar_text);
        toolbar_text.setText(getResources().getString(R.string.rider_reg_head));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(RiderRegistration.this);
        appPrefs = new AppPreferences(RiderRegistration.this);

        flag=(ImageView)findViewById(R.id.flag);
        Picasso.with(RiderRegistration.this).load(appPrefs.getFlag()).into(flag);

        et_id = (EditText) findViewById(R.id.et_id);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_cc = (EditText) findViewById(R.id.et_cc);

        id_input_layout = (TextInputLayout) findViewById(R.id.id_input_layout);
        name_input_layout = (TextInputLayout) findViewById(R.id.name_input_layout);
        phone_input_layout = (TextInputLayout) findViewById(R.id.phone_input_layout);
        cc_input_layout = (TextInputLayout) findViewById(R.id.cc_input_layout);

        name_input_layout.setHint(getResources().getString(R.string.rider_name));
        phone_input_layout.setHint(getResources().getString(R.string.rider_mobile));
        cc_input_layout.setHint(getResources().getString(R.string.code));

        et_id.setText(intent_veh_no);

        submit = (Button) findViewById(R.id.submit);
        submit.setText(getResources().getString(R.string.submit));
        spinnerError = (TextView) findViewById(R.id.spinnerError);

        GradientDrawable bgShape = (GradientDrawable)submit.getBackground();
        bgShape.setColor(Color.parseColor("#003763"));

        if (appPrefs.getCountry().matches("india")) {
            id_input_layout.setHint(getResources().getString(R.string.id_head));
            et_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        } else {
            id_input_layout.setHint(getResources().getString(R.string.textVehNo));
            et_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }

        sa_mobile = (Spinner) findViewById(R.id.sa_mobile);
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
                Utilities.hideSoftKeyboard(RiderRegistration.this);
                checkCred();
            }
        });

        if(appPrefs.getCountry().matches("uganda")){
            et_cc.setText("255");
        }else if(appPrefs.getCountry().matches("kenya")){
            et_cc.setText("254");
        }else if(appPrefs.getCountry().matches("india")){
            et_cc.setText("91");
        }

        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        getSAMobile();

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

    private void checkCred() {
        if (!validateMobile()) {
            return;
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


        if (Utilities.checkNetworkConnection(RiderRegistration.this)) {
            dialog.setMessage(getResources().getString(R.string.please_wait));
            dialog.setCancelable(false);
            dialog.show();
            rider_reg();

        } else {
            Toast.makeText(RiderRegistration.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validateMobile() {
        if (mobile_sel.trim().isEmpty()) {
            spinnerError.setVisibility(View.VISIBLE);
            spinnerError.setText(getResources().getString(R.string.select_mobile_number));
            return false;
        } else {
            spinnerError.setVisibility(View.GONE);
        }

        return true;
    }

    private boolean validateId() {
        if (et_id.getText().toString().trim().isEmpty()) {
            if (appPrefs.getCountry().matches("india")) {
                id_input_layout.setError(getResources().getString(R.string.enter_chassis));
            } else {
                id_input_layout.setError(getResources().getString(R.string.enter_vrn));
            }
            return false;
        } else {
            id_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName() {
        if (et_name.getText().toString().trim().isEmpty()) {
            name_input_layout.setError(getResources().getString(R.string.enter_name));
            return false;
        } else {
            name_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOMobile() {
        if (appPrefs.getCountry().matches("india")) {
            if (et_phone.getText().toString().trim().isEmpty()) {
                phone_input_layout.setError(getResources().getString(R.string.enter_mob_no));
                return false;
            } else if (et_phone.getText().length() != 10) {
                phone_input_layout.setError(getResources().getString(R.string.validate_mob_no));
                return false;
            } else {
                phone_input_layout.setErrorEnabled(false);
            }
        } else {
            if (et_phone.getText().toString().trim().isEmpty()) {
                phone_input_layout.setError(getResources().getString(R.string.enter_mob_no));
                return false;
            } else if (et_phone.getText().length() != 9) {
                phone_input_layout.setError(getResources().getString(R.string.validate_mob_no1));
                return false;
            } else {
                phone_input_layout.setErrorEnabled(false);
            }
        }

        return true;
    }

    private void getSAMobile() {
        String JSON_URL = appPrefs.getURL() + "User/service_man_list";
        RequestQueue queue = Volley.newRequestQueue(RiderRegistration.this);

        StringRequest req = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("employee_dtl");

                            final ArrayList<Mobile> list = new ArrayList<Mobile>();
                            list.add(new Mobile("Select mobile number", "", getResources().getString(R.string.select_mobile_number)));
                            for (int i = 0; i < arr.length(); i++) {
                                list.add(new Mobile(arr.getJSONObject(i).getString("firstname") + " " + arr.getJSONObject(i).getString("lastname")
                                        , arr.getJSONObject(i).getString("mobile_no"),
                                        arr.getJSONObject(i).getString("firstname") + " " + arr.getJSONObject(i).getString("lastname") + " (" + arr.getJSONObject(i).getString("mobile_no") + ")"));
                            }

                            ArrayAdapter<Mobile> adapter = new ArrayAdapter<Mobile>(RiderRegistration.this,
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
                        Toast.makeText(RiderRegistration.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
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

    private void rider_reg() {
        String JSON_URL = appPrefs.getURL() + "Transaction/rider_registration";
        RequestQueue queue = Volley.newRequestQueue(RiderRegistration.this);

        StringRequest req = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").matches("true")) {

                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(RiderRegistration.this, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(Html.fromHtml(getResources().getString(R.string.rider_success_dialog)));
                                builder.setMessage(obj.getString("message"));
                                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                //builder.setNegativeButton("Cancel", null);
                                builder.show();

                            } else {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(RiderRegistration.this, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(Html.fromHtml(getResources().getString(R.string.rider_failure_dialog)));
                                builder.setMessage(obj.getString("message"));
                                builder.setPositiveButton(getResources().getString(R.string.ok), null);
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
                        Toast.makeText(RiderRegistration.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("country", appPrefs.getCountry());
                params.put("veh_reg_no", et_id.getText().toString().trim());
                params.put("rider_name", et_name.getText().toString().trim());
                params.put("rider_mobile_no", et_phone.getText().toString().trim());
                params.put("mobile_no", mobile_sel);
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
