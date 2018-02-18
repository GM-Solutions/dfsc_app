package ascent.com.dfsc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.splunk.mint.Mint;

import ascent.com.dfsc.R;

public class Splash extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        appPreferences = new AppPreferences(Splash.this);
        Mint.initAndStartSession(this.getApplication(), "3e402768");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

                if (appPreferences.getShouldLogin().trim().isEmpty()) {
                    appPreferences.RemoveAllSharedPreference();
                    Intent mainIntent = new Intent(Splash.this, Login.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                } else if (appPreferences.getShouldLogin().trim().matches("true")) {
                    Intent mainIntent = new Intent(Splash.this, Drawer.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();

                } else {
                    Intent mainIntent = new Intent(Splash.this, Login.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
