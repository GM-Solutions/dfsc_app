package ascent.com.dfsc.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import ascent.com.dfsc.Fragment.HomeFragment;
import ascent.com.dfsc.R;

/**
 * Created by Admin on 1/10/2018.
 */

public class Drawer extends AppCompatActivity {

    private static final String TAG = Drawer.class.getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    protected AppPreferences appPrefs;
    Menu menu;
    NavigationView navigationView;
    private ProgressDialog dialog;
    JSONArray sideMenu;
    ImageView flag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appPrefs = new AppPreferences(Drawer.this);
        flag=(ImageView)findViewById(R.id.flag);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }

        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        if (Utilities.checkNetworkConnection(Drawer.this)) {
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new HomeFragment();
            fragmentTransaction.replace(R.id.main_container, fragment);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(Drawer.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            finish();
        }

        Picasso.with(Drawer.this).load(appPrefs.getFlag()).into(flag);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView name=(TextView)header.findViewById(R.id.name);
        name.setText(appPrefs.getUsername());
        TextView email=(TextView)header.findViewById(R.id.email);
        email.setText(appPrefs.getEmail());

        getMenu();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                navigationView.clearFocus();
                navigationView.requestLayout();


                if (id == 0) {
                    drawer.closeDrawers();
                    if (Utilities.checkNetworkConnection(Drawer.this)) {
                        startActivity(new Intent(Drawer.this, MyProfile.class));
                    } else {
                        Toast.makeText(Drawer.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (id == 101) {
                    if (Utilities.checkNetworkConnection(Drawer.this)) {
                        startActivity(new Intent(Drawer.this, AscRegistration.class));
                    } else {
                        Toast.makeText(Drawer.this, "No Internet available!", Toast.LENGTH_SHORT).show();
                    }

                } else if (id == 100) {
                    if (Utilities.checkNetworkConnection(Drawer.this)) {
                        startActivity(new Intent(Drawer.this, SaRegistration.class));
                    } else {
                        Toast.makeText(Drawer.this, "No Internet available!", Toast.LENGTH_SHORT).show();
                    }

                } else if (id == sideMenu.length() + 1) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(Drawer.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(Html.fromHtml("<b>Logout</b>"));
                    builder.setMessage("Are you sure you want to logout?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            appPrefs.RemoveAllSharedPreference();
                            Toast.makeText(Drawer.this, "Logout successfully", Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(Drawer.this, Login.class);
                            startActivity(mainIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }


                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void getMenu() {
        menu = navigationView.getMenu();
        try {
            sideMenu = new JSONArray(appPrefs.getSideMenu());

            menu.add(0, 0, Menu.FIRST, "My Profile").setIcon(R.drawable.ic_user);

            for (int i = 0; i < sideMenu.length(); i++) {

                if (sideMenu.getJSONObject(i).getString("key").matches("service_adv_registration")) {
                    menu.add(0, 100,
                            Menu.FIRST + i + 1, sideMenu.getJSONObject(i).getString("value"))
                            .setIcon(R.drawable.ic_sa);
                } else if (sideMenu.getJSONObject(i).getString("key").matches("asc_registration")) {
                    menu.add(0, 101,
                            Menu.FIRST + i + 1, sideMenu.getJSONObject(i).getString("value"))
                            .setIcon(R.drawable.ic_asc);
                }else{
                    menu.add(0, 500,
                            Menu.FIRST + i + 1, sideMenu.getJSONObject(i).getString("value"))
                            .setIcon(R.drawable.ic_default);
                }


                //menu.add(0, 2, Menu.FIRST + 2, "Sercice Advisor Registration").setIcon(R.drawable.ic_sa);
                //menu.add(0, 3, Menu.FIRST + 3, "Logout").setIcon(R.drawable.ic_logout);
            }

            menu.add(0, sideMenu.length() + 1, sideMenu.length() + 1, "Logout").setIcon(R.drawable.ic_logout);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        /*
         if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

         */

        AlertDialog.Builder builder =
                new AlertDialog.Builder(Drawer.this, R.style.AppCompatAlertDialogStyle);
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
