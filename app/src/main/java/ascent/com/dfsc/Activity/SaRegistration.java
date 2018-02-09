package ascent.com.dfsc.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ascent.com.dfsc.R;

public class SaRegistration extends AppCompatActivity {

    Toolbar toolbar;
    ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sa_registration);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SA Registration");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        flag=(ImageView)findViewById(R.id.flag);
        //Picasso.with(MyProfile.this).load(appPrefs.getFlag()).into(flag);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
