package ascent.com.dfsc.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ascent.com.dfsc.Adapters.LanguageAdapter;
import ascent.com.dfsc.Database.DBHelper;
import ascent.com.dfsc.Modal.LanguageGetSet;
import ascent.com.dfsc.R;

public class Utilities extends Activity {

    public static String URL = "http://124.153.104.69:8063/api/";
    static int flag = 0;
    static AppPreferences appPrefs;
    static ProgressDialog Adialog;
    static DBHelper mydb;

    public static void goToPage(Context paramContext, Class paramClass, Bundle paramBundle) {
        Intent localIntent = new Intent(paramContext, paramClass);
        if (paramBundle != null)
            localIntent.putExtra("android.intent.extra.INTENT", paramBundle);
        paramContext.startActivity(localIntent);
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            System.out.println("Exc=" + e);
            return null;
        }
    }

    public static boolean checkNetworkConnection(Context paramContext) {
        int i = 1;
        boolean flag = true;
        ConnectivityManager connectivity = (ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo localNetworkInfo1 = connectivity.getNetworkInfo(i);
            NetworkInfo localNetworkInfo2 = connectivity.getActiveNetworkInfo();
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            System.out.println("wifi" + localNetworkInfo1.isAvailable());
            System.out.println("info" + localNetworkInfo2);

            if (((localNetworkInfo2 == null) || (!localNetworkInfo2
                    .isConnected())) && (!localNetworkInfo1.isAvailable()))
                i = 0;
            if (info != null) {
                for (int j = 0; j < info.length; j++)
                    if (info[j].getState() == NetworkInfo.State.CONNECTED) {
                        i = 1;
                        break;
                    } else
                        i = 0;
            }

        } else
            i = 0;

        if (i == 0)
            flag = false;
        if (i == 1)
            flag = true;

        return flag;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isTablet(Context paramContext, String tab) {
        //String tab = paramContext.getResources().getString(R.string.isTablet);
        if (tab.equals("0"))
            return false;
        else
            return true;
    }

    public static String convertGMTtoDate(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy");
        TimeZone tz = TimeZone.getDefault(); //Will return your device current time zone
        dateformat.setTimeZone(tz); //Set the time zone to your simple date formatter
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        Date currenTimeZone = (Date) calendar.getTime();
        return dateformat.format(currenTimeZone);
    }

    public static String convertGMTtoTime(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm a");
        TimeZone tz = TimeZone.getDefault(); //Will return your device current time zone
        dateformat.setTimeZone(tz); //Set the time zone to your simple date formatter
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        Date currenTimeZone = (Date) calendar.getTime();
        return dateformat.format(currenTimeZone);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void showLanguageDialog(final Activity activity) {
        try {

            mydb = new DBHelper(activity);
            JSONArray languages;
            ArrayList<LanguageGetSet> list;
            appPrefs = new AppPreferences(activity);
            //languages=new JSONArray(appPrefs.getLanguage());
            list = new ArrayList<>();

            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.language_dialog);

            Spinner language = (Spinner) dialog.findViewById(R.id.language);
            list.add(new LanguageGetSet("123", "Please Select Language"));
            for (int i = 0; i < 10; i++) {
                list.add(new LanguageGetSet("" + i, "India"));
            }
            LanguageAdapter adapter = new LanguageAdapter(activity, list);
            language.setAdapter(adapter);

            language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    LanguageGetSet selected = (LanguageGetSet) arg0.getAdapter().getItem(arg2);
                    if (selected.id.equals("123")) {
                        flag = 0;
                        appPrefs.setLanguageSelected("");
                    } else {
                        flag = 1;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            dialog.show();

            Button submit = (Button) dialog.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag == 1) {
                        dialog.dismiss();
                        appPrefs.setLanguageSelected("true");

                        //Adialog.setMessage("Please Wait..");
                        //Adialog.setCancelable(false);
                        //Adialog.show();

                        Toast.makeText(activity, "Selected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "Not Selected", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void getResouces(final Activity activity) {
        String JSON_URL = appPrefs.getURL() + "";
        RequestQueue queue = Volley.newRequestQueue(activity);

        StringRequest req = new StringRequest(Request.Method.POST, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Adialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("status").matches("true")) {

                            } else {
                                Toast.makeText(activity, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Adialog.dismiss();
                        Toast.makeText(activity, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("language", "");
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


}
