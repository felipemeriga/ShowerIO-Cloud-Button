package com.felipe.showeriocloud.Activities.ShowerIO;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.felipe.showeriocloud.Activities.Fragments.HelpFragment;
import com.felipe.showeriocloud.Activities.Fragments.ProfileFragment;
import com.felipe.showeriocloud.Activities.Fragments.SearchForDevicesFragment;
import com.felipe.showeriocloud.Activities.Fragments.ShowerDetailFragment;
import com.felipe.showeriocloud.Activities.Fragments.ShowerListFragment;
import com.felipe.showeriocloud.Activities.Fragments.SignOutFragment;
import com.felipe.showeriocloud.Activities.Fragments.StatisticsDetailDailyFragment;
import com.felipe.showeriocloud.Activities.Fragments.StatisticsDetailFragment;
import com.felipe.showeriocloud.Aws.AuthorizationHandle;
import com.felipe.showeriocloud.Aws.CognitoIdentityPoolManager;
import com.felipe.showeriocloud.Model.DeviceDO;
import com.felipe.showeriocloud.Model.DevicePersistance;
import com.felipe.showeriocloud.R;
import com.felipe.showeriocloud.Utils.FacebookInformationSeeker;
import com.felipe.showeriocloud.Utils.ServerCallback;
import java.util.HashMap;
import java.util.Map;

public class ShowerNavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HelpFragment.OnFragmentInteractionListener, ShowerListFragment.OnFragmentInteractionListener, ShowerDetailFragment.OnFragmentInteractionListener, SearchForDevicesFragment.OnFragmentInteractionListener, StatisticsDetailFragment.OnFragmentInteractionListener, StatisticsDetailDailyFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, SignOutFragment.OnFragmentInteractionListener {

    protected NavigationView navigationView;
    private ImageView imageView;
    private TextView usernameTitle;
    private TextView textViewEmail;
    private LinearLayout linearLayout;
    private ProgressDialog listDevicesProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View hView = navigationView.getHeaderView(0);


        // TODO - FACEBOOK AND COGNITO NAME - CHANGE HERE
        linearLayout = (LinearLayout) hView.findViewById(R.id.nav_header_linear);
        imageView = (ImageView) hView.findViewById(R.id.imageView);
        usernameTitle = (TextView) hView.findViewById(R.id.username);
        textViewEmail = (TextView) hView.findViewById(R.id.textViewEmail);

        if (AuthorizationHandle.mainAuthMethod.equals(AuthorizationHandle.FEDERATED_IDENTITIES)) {
            usernameTitle.setText(FacebookInformationSeeker.facebookName);
            textViewEmail.setText(FacebookInformationSeeker.facebookEmail);
        } else {
            CognitoIdentityPoolManager.getPool().getUser(CognitoIdentityPoolManager.getPool().getCurrentUser().getUserId()).getDetailsInBackground(detailsHandler);
        }
//        Picasso.get().load(FacebookInformationSeeker.facebookProfilePhotoUrl).into(imageView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }


    private void startBaseFragment() {

        listDevicesProgressDialog = new ProgressDialog(this);
        listDevicesProgressDialog.setMessage("Buscando Lista...");
        listDevicesProgressDialog.setCanceledOnTouchOutside(false);
        listDevicesProgressDialog.show();

        DevicePersistance.fastGetAllDevicesFromUser(new ServerCallback() {
            @Override
            public void onServerCallback(boolean status, String response) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        listDevicesProgressDialog.dismiss();
                        if (DevicePersistance.lastUpdateUserDevices.size() == 0) {
                            fragmentChanger(navigationView.getMenu().getItem(1), SearchForDevicesFragment.class);
                            navigationView.getMenu().getItem(0).setChecked(false);
                        } else {
                            Class showerListFragment = ShowerListFragment.class;
                            fragmentChanger(navigationView.getMenu().getItem(0), ShowerListFragment.class);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.startBaseFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shower_base, menu);
        startBaseFragment();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass;
        boolean loadDevices = false;
        fragmentClass = ShowerListFragment.class;

        switch (item.getItemId()) {
            case R.id.nav_find_devices:
                fragmentClass = SearchForDevicesFragment.class;
                break;
            case R.id.nav_help:
                Intent listOfDevices = new Intent(ShowerNavigationDrawer.this, Walkthrough.class);
                startActivity(listOfDevices);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
            case R.id.nav_account:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.nav_list_of_devices:
                loadDevices = true;
                listDevicesProgressDialog = new ProgressDialog(this);
                listDevicesProgressDialog.setMessage("Buscando Lista...");
                listDevicesProgressDialog.setCanceledOnTouchOutside(false);
                listDevicesProgressDialog.show();

                DevicePersistance.fastGetAllDevicesFromUser(new ServerCallback() {
                    @Override
                    public void onServerCallback(boolean status, String response) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                listDevicesProgressDialog.dismiss();
                                Class showerListFragment = ShowerListFragment.class;
                                fragmentChanger(item, showerListFragment);
                            }
                        });
                    }
                });
                break;
            case R.id.nav_signout:
                fragmentClass = SignOutFragment.class;
                break;
            default:
                fragmentClass = HelpFragment.class;
        }

        if (!loadDevices) {
            fragmentChanger(item, fragmentClass);
        }

        return true;
    }

    void fragmentChanger(MenuItem item, Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.base, fragment).commit();

        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onSelectedDevice(DeviceDO deviceDO) {
        DevicePersistance.selectedDevice = deviceDO;
        Fragment detailFragment = new ShowerDetailFragment();
        this.setTitle("Chuveiro");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.getFragments().get(0));
        fragmentManager.beginTransaction().replace(R.id.base, detailFragment).commit();
        navigationView.getMenu().getItem(0).setChecked(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onDailyStatisticsSelected() {
        Fragment statisticsFragment = new StatisticsDetailDailyFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragmentManager.getFragments().get(0));
        fragmentManager.beginTransaction().replace(R.id.base, statisticsFragment).commit();
        navigationView.getMenu().getItem(0).setChecked(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onFragmentInteraction(String fragmentName) {
        if (fragmentName.equals("ShowerListFragment")) {

            listDevicesProgressDialog = new ProgressDialog(this);
            listDevicesProgressDialog.setMessage("Buscando Lista...");
            listDevicesProgressDialog.setCanceledOnTouchOutside(false);
            listDevicesProgressDialog.show();

            DevicePersistance.fastGetAllDevicesFromUser(new ServerCallback() {
                @Override
                public void onServerCallback(boolean status, String response) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            listDevicesProgressDialog.dismiss();
                            Class showerListFragment = ShowerListFragment.class;
                            fragmentChanger(navigationView.getMenu().getItem(0), ShowerListFragment.class);
                        }
                    });
                }
            });
        } else if (fragmentName.equals("StatisicsDetailFragment")) {
            Fragment detailFragment = new StatisticsDetailFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(fragmentManager.getFragments().get(0));
            fragmentManager.beginTransaction().replace(R.id.base, detailFragment).commit();
            navigationView.getMenu().getItem(0).setChecked(false);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            this.setTitle("Estatísticas");

        }

    }

    @Override
    public void onResetedDevice() {
        fragmentChanger(navigationView.getMenu().getItem(1), SearchForDevicesFragment.class);
        navigationView.getMenu().getItem(0).setChecked(false);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    GetDetailsHandler detailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            Map<String, String> stringStringHashMap = new HashMap<>();
            CognitoUserAttributes cognitoUserAttributes = cognitoUserDetails.getAttributes();
            stringStringHashMap = cognitoUserAttributes.getAttributes();
            usernameTitle.setText(stringStringHashMap.get("given_name"));
            textViewEmail.setText(stringStringHashMap.get("email"));
        }

        @Override
        public void onFailure(Exception exception) {
            usernameTitle.setText("");
        }
    };
}
