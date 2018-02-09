package ascent.com.dfsc.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities extends Activity
{
	//public static String url1 = "http://192.168.1.169/Webservices/SalesForce/";
	//public static String url = "http://testsite4me.com/development/salescalls/webservices/";
	public static String URL = "http://124.153.104.69:8063/api/";

	public static void goToPage(Context paramContext, Class paramClass, Bundle paramBundle)
	{
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

	public static boolean isTablet(Context paramContext, String tab)
	{	
		//String tab = paramContext.getResources().getString(R.string.isTablet);		
		if(tab.equals("0"))
			return false;
		else
			return true;			 
	}
	
	public static String convertGMTtoDate(Date date)
	{
		SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy");
	    TimeZone tz = TimeZone.getDefault(); //Will return your device current time zone
	    dateformat.setTimeZone(tz); //Set the time zone to your simple date formatter	    
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        Date currenTimeZone = (Date) calendar.getTime();
        return dateformat.format(currenTimeZone);
	}

    public static String convertGMTtoTime(Date date)
    {
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


}
