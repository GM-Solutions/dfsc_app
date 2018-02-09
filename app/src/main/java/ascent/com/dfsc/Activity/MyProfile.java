package ascent.com.dfsc.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ascent.com.dfsc.R;

public class MyProfile extends AppCompatActivity {

    Toolbar toolbar;
    private ProgressDialog dialog;
    protected AppPreferences appPrefs;
    TextView name,username,designation,email,mobile;
    ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(MyProfile.this);
        appPrefs = new AppPreferences(MyProfile.this);

        name=(TextView)findViewById(R.id.name);
        username=(TextView)findViewById(R.id.username);
        designation=(TextView)findViewById(R.id.designation);
        email=(TextView)findViewById(R.id.email);
        mobile=(TextView)findViewById(R.id.mobile);

        flag=(ImageView)findViewById(R.id.flag);
        Picasso.with(MyProfile.this).load(appPrefs.getFlag()).into(flag);

        if (Utilities.checkNetworkConnection(getApplicationContext())) {
            dialog.setMessage("Please Wait..");
            dialog.setCancelable(false);
            dialog.show();
            getProfile();

        } else {
            Toast.makeText(MyProfile.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
        }

    }

    private void getProfile() {
        String JSON_URL = appPrefs.getURL() + "User/user_profile";
        RequestQueue queue = Volley.newRequestQueue(MyProfile.this);

        StringRequest req = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("status").matches("true")) {

                                //name,username,email,mobile
                                name.setText(obj.getString("first_name"));
                                username.setText(obj.getString("username"));
                                email.setText(obj.getString("email"));
                                mobile.setText(obj.getString("phone_number"));
                                designation.setText(obj.getString("designation"));

                            } else {
                                Toast.makeText(MyProfile.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MyProfile.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("country", appPrefs.getCountry());
                params.put("user_id",appPrefs.getUserId());
                params.put("group", appPrefs.getGroup());
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
