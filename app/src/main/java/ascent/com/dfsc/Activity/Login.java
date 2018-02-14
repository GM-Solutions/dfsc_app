package ascent.com.dfsc.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.splunk.mint.Mint;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ascent.com.dfsc.Adapters.CountryAdapter;
import ascent.com.dfsc.R;

public class Login extends AppCompatActivity {

    TextInputLayout mobile_input_layout, otp_input_layout, userName_input_layout;
    EditText et_mobile, et_otp, et_userName;
    Spinner country, role;
    Button getOTP, login;
    String country_sel, base_url, otp, role_str,showHideUsername,selectedFlag,mobile_validation;
    int mobile_length;
    TextView country_error, role_error;
    private ProgressDialog dialog, catchOtpDialog;
    protected AppPreferences appPrefs;
    private SmsVerifyCatcher smsVerifyCatcher;
    JSONObject jsonObject, roleObject;
    CountryAdapter adapter;
    JSONArray roleArray;
    ArrayList<Role> list1;
    ArrayAdapter<Role> roleadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        country_sel = "";
        base_url = "";
        otp = "";
        role_str = "";
        showHideUsername = "";
        selectedFlag = "";
        mobile_validation = "";

        dialog = new ProgressDialog(Login.this);
        catchOtpDialog = new ProgressDialog(Login.this);
        appPrefs = new AppPreferences(Login.this);

        mobile_input_layout = (TextInputLayout) findViewById(R.id.mobile_input_layout);
        userName_input_layout = (TextInputLayout) findViewById(R.id.userName_input_layout);
        otp_input_layout = (TextInputLayout) findViewById(R.id.otp_input_layout);

        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_userName = (EditText) findViewById(R.id.et_userName);
        et_otp = (EditText) findViewById(R.id.et_otp);

        Mint.initAndStartSession(this.getApplication(), "3e402768");

        country = (Spinner) findViewById(R.id.country);
        role = (Spinner) findViewById(R.id.role);

        role.setVisibility(View.GONE);
        userName_input_layout.setVisibility(View.GONE);

        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Countries selected = (Countries) arg0.getAdapter().getItem(arg2);
                country_sel = selected.id;
                base_url = selected.base_url;
                selectedFlag=selected.flag;
                appPrefs.setURL(base_url);
                Log.e("URL",appPrefs.getURL());
                appPrefs.setCountry(country_sel);
                appPrefs.setCode(selected.code);
                mobile_length = Integer.parseInt(selected.mobile_validation);
                appPrefs.setMobileVad(mobile_length);
                et_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mobile_length)});

                if (!(arg2 == 0)) {

                    role.setVisibility(View.VISIBLE);
                    setRoleAdapter();

                } else {
                    role.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Role selected = (Role) arg0.getAdapter().getItem(arg2);
                role_str = selected.id;
                showHideUsername=selected.username;

                if(showHideUsername.matches("true")){
                    userName_input_layout.setVisibility(View.VISIBLE);
                    et_userName.setText("");
                }else{
                    userName_input_layout.setVisibility(View.GONE);
                    et_userName.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        country_error = (TextView) findViewById(R.id.country_error);
        role_error = (TextView) findViewById(R.id.role_error);

        getOTP = (Button) findViewById(R.id.getOTP);
        login = (Button) findViewById(R.id.login);

        GradientDrawable bgShape = (GradientDrawable)getOTP.getBackground();
        bgShape.setColor(Color.parseColor("#003763"));

        GradientDrawable bgShape1 = (GradientDrawable)login.getBackground();
        bgShape1.setColor(Color.parseColor("#003763"));

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(Login.this, Drawer.class));
                submitForm();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOtp();
            }
        });

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                et_otp.setText(code);//set code in edit text
                if (et_otp.getText().toString().trim() != null) {
                    //otprogressbar.setVisibility(View.INVISIBLE);
                    //verifyOtp.performClick();
                    catchOtpDialog.dismiss();
                    if (login.getVisibility() == View.VISIBLE) {
                        login.performClick();
                    }
                    //login.performClick();
                }

            }
        });

        dialog.setMessage("Loading Countries..");
        dialog.setCancelable(false);
        dialog.show();
        getCountries();

        et_mobile.setEnabled(true);
        country.setEnabled(true);
        otp_input_layout.setVisibility(View.GONE);
        getOTP.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);

    }

    private void getCountries() {
        String JSON_URL = Utilities.URL + "Settings/countries";
        RequestQueue queue = Volley.newRequestQueue(Login.this);

        StringRequest req = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").matches("true")) {
                                JSONArray arr = obj.getJSONArray("countries");
                                final ArrayList<Countries> list = new ArrayList<Countries>();
                                list.add(new Countries("0", "Select Country", "0", "0", "URL","0"));

                                for (int i = 0; i < arr.length(); i++) {
                                    jsonObject = arr.getJSONObject(i);
                                    Countries c = new Countries(jsonObject.getString("id"),
                                            jsonObject.getString("value"),
                                            jsonObject.getString("mobile_validation"),
                                            jsonObject.getString("flag"),
                                            jsonObject.getString("base_url"),
                                            jsonObject.getString("code"));
                                    list.add(c);

                                }

                                adapter = new CountryAdapter(Login.this, list);
                                country.setAdapter(adapter);


                                roleObject = obj.getJSONObject("role_menu");
                                Log.e("ROLE", String.valueOf(roleObject));


                            } else {
                                dialog.dismiss();
                                Toast.makeText(Login.this, "No countries found", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Throwable t) {
                            dialog.dismiss();
                            Log.e("REsponse", "Could not parse malformed JSON: \"" + response + "\"");
                        }
                        //Toast.makeText(SearchSchoolBefore.this, "Login Successfull", Toast.LENGTH_SHORT).show();

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(SearchSchoolBefore.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Toast.makeText(Login.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {


         /*   @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("country", "");
                params.put("city_id", cityId);
                return new JSONObject(params).toString().getBytes();
            }*/

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

    private void setRoleAdapter() {
        try {
            list1 = new ArrayList<Role>();
            list1.clear();
            JSONArray arr1 = null;
            if (country_sel.matches("india")) {
                arr1 = roleObject.getJSONArray("india");
            } else if (country_sel.matches("uganda")) {
                arr1 = roleObject.getJSONArray("uganda");
            } else if (country_sel.matches("kenya")) {
                arr1 = roleObject.getJSONArray("kenya");
            }

            Log.e("ROLE", String.valueOf(roleArray));

            list1.add(new Role("0", "Select Role", "0"));
            for (int i = 0; i < arr1.length(); i++) {

                JSONObject obj1 = arr1.getJSONObject(i);
                Role r = new Role(obj1.getString("key"), obj1.getString("value"),
                        obj1.getString("username"));
                list1.add(r);


            }
            roleadapter = new ArrayAdapter<Role>(Login.this,
                    R.layout.list_item, R.id.name, list1);
            roleadapter.notifyDataSetChanged();
            role.setAdapter(roleadapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    private void checkOtp() {
        if (!validateOtp()) {
            return;
        }

        if (Utilities.checkNetworkConnection(getApplicationContext())) {
            if (et_otp.getText().toString().trim().matches(otp)) {
                appPrefs.setShouldLogin("true");
                appPrefs.setMobile(et_mobile.getText().toString().trim());
                startActivity(new Intent(Login.this, Drawer.class));
                finish();
            } else {
                Toast.makeText(Login.this, "Incorrect OTP! Please enter correct OTP to login.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(Login.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateOtp() {
        if (et_otp.getText().toString().trim().isEmpty()) {
            otp_input_layout.setError("Enter valid OTP");
            return false;
        } else {
            otp_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private void submitForm() {
        if (!validateCountry()) {
            return;
        }

        if(userName_input_layout.getVisibility()==View.VISIBLE){
            if (!validateUsername()) {
                return;
            }
        }

        if (!validateMobile()) {
            return;
        }

        if (!validateRole()) {
            return;
        }

        if (Utilities.checkNetworkConnection(getApplicationContext())) {
            dialog.setMessage("Please Wait..");
            dialog.setCancelable(false);
            dialog.show();
            loginApi();
            //startActivity(new Intent(Login.this, Drawer.class));
            //finish();

        } else {
            Toast.makeText(Login.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validateUsername() {
        if (et_userName.getText().toString().trim().isEmpty()) {
            userName_input_layout.setError("Please enter username");
            return false;
        } else {
            userName_input_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateCountry() {
        if (country_sel.matches("0")) {
            country_error.setVisibility(View.VISIBLE);
            return false;
        } else {
            country_error.setVisibility(View.GONE);
        }
        return true;
    }

    private boolean validateRole() {
        if (role_str.matches("0")) {
            role_error.setVisibility(View.VISIBLE);
            return false;
        } else {
            role_error.setVisibility(View.GONE);
        }
        return true;
    }

    private boolean validateMobile() {
        if (et_mobile.getText().toString().trim().isEmpty()) {
            mobile_input_layout.setError("Please enter mobile number");
            return false;
        } else if (et_mobile.getText().length() < mobile_length) {
            mobile_input_layout.setError("Please enter valid mobile number");
            return false;
        } else if (et_mobile.getText().length() > mobile_length) {
            mobile_input_layout.setError("Please enter valid mobile number");
            return false;
        } else {
            mobile_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private void loginApi() {

        String JSON_URL = appPrefs.getURL() + "User/login/";
        RequestQueue queue = Volley.newRequestQueue(Login.this);

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

                                et_mobile.setEnabled(false);
                                et_userName.setEnabled(false);
                                country.setEnabled(false);
                                role.setEnabled(false);
                                otp_input_layout.setVisibility(View.VISIBLE);
                                getOTP.setVisibility(View.GONE);
                                login.setVisibility(View.VISIBLE);

                                appPrefs.setUserId(obj.getString("user_id"));
                                appPrefs.setUsername(obj.getString("firstname") + " " + obj.getString("lastname"));
                                appPrefs.setEmail(obj.getString("email"));
                                appPrefs.setGroup(obj.getString("group"));
                                otp = obj.getString("otp");
                                appPrefs.setFlag(selectedFlag);

                                appPrefs.setSideMenu(obj.getJSONObject("menu").getJSONArray("side_menu"));
                                appPrefs.setDashboard(obj.getJSONObject("menu").getJSONArray("dashboard"));
                                //appPrefs.setLanguage(obj.getJSONObject("menu").getJSONArray("languages"));

                                Toast.makeText(Login.this, "Reading OTP, Please wait..", Toast.LENGTH_LONG).show();

                                Log.e("menu", appPrefs.getSideMenu());
                                Log.e("menu", appPrefs.getDashboard());
                                Log.e("group", appPrefs.getGroup());
                                Log.e("languages", appPrefs.getGroup());

                                Mint.initAndStartSession(getApplication(), "3e402768");
                                Mint.setUserIdentifier(obj.getString("user_id"));

                            } else {
                                Toast.makeText(Login.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Login.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("country", country_sel);
                params.put("username", et_userName.getText().toString().trim());
                params.put("mobile_no", et_mobile.getText().toString().trim());
                params.put("role", role_str);
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

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    /*
     public class Country {
        private String id;
        private String value;
        private String mobile_validation;
        private String flag;

        public Country(String id, String value, String mobile_validation, String flag) {
            this.id = id;
            this.value = value;
            this.mobile_validation = mobile_validation;
            this.flag = flag;

        }

        @Override
        public String toString() {
            return value;
        }
    }
     */

    public class Countries {
        public String id;
        public String value;
        public String mobile_validation;
        public String flag;
        public String base_url;
        public String code;

        public Countries(String id, String value, String mobile_validation, String flag, String base_url,String code) {
            this.id = id;
            this.value = value;
            this.mobile_validation = mobile_validation;
            this.flag = flag;
            this.base_url = base_url;
            this.code = code;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public class Role {
        public String id;
        public String value;
        public String username;

        public Role(String id, String value, String username) {
            this.id = id;
            this.value = value;
            this.username = username;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(Login.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(Html.fromHtml("<b>Exit DFSC</b>"));
        builder.setMessage("Are you sure you want to Exit?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();

    }

}
