package edu.univdhaka.cse2216.myplane.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.univdhaka.cse2216.myplane.R;

import edu.univdhaka.cse2216.myplane.utils.Alarm;
import edu.univdhaka.cse2216.myplane.utils.AlarmReceiver;
import edu.univdhaka.cse2216.myplane.utils.NotificationScheduler;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FrameLayout parentLayout;
    NavigationView navigationView;
    FragmentManager fm;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout=findViewById(R.id.parentId);
        drawerLayout = findViewById(R.id.drawerId);
        mToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView=(NavigationView)findViewById(R.id.navigationDrawerId);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            hideKeyboard(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id= menuItem.getItemId();
        if(id==R.id.homeId)
        {
            fm=MainActivity.this.getFragmentManager();
            for(int i=0;i<fm.getBackStackEntryCount(); ++i)
            {
                fm.popBackStack();
            }

            Fragment fragment=new HomeFragment();
            FragmentManager fragmentManager= getFragmentManager();

            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.parentId,fragment);
            fragmentTransaction.commit();
        }
        else if(id==R.id.donelistId)
        {
            fm=MainActivity.this.getFragmentManager();
            for(int i=0;i<fm.getBackStackEntryCount(); ++i)
            {
                fm.popBackStack();
            }

            Fragment fragment=new DoneList();
            FragmentManager fragmentManager= getFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.parentId,fragment,"doneList");
            fragmentTransaction.addToBackStack("doneList");
            fragmentTransaction.commit();
        }
        else if(id==R.id.addId)
        {
            fm=MainActivity.this.getFragmentManager();
            for(int i=0;i<fm.getBackStackEntryCount(); ++i)
            {
                fm.popBackStack();
            }

            Fragment fragment=new AddNew();
            FragmentManager fragmentManager= getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("ActivityType","newAdd");
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.parentId,fragment);
            fragmentTransaction.addToBackStack(String.valueOf(fragment));
            fragmentTransaction.commit();
        }

        else if(id==R.id.aboutId)
        {
            fm=MainActivity.this.getFragmentManager();
            for(int i=0;i<fm.getBackStackEntryCount(); ++i)
            {
                fm.popBackStack();
            }

            Fragment fragment=new AboutApp();
            FragmentManager fragmentManager= getFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.parentId,fragment);
            fragmentTransaction.addToBackStack(String.valueOf(fragment));
            fragmentTransaction.commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() == 0 )
        {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.alert_title)
                    .setMessage(R.string.alert_message)
                    .setIcon(R.drawable.question)
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAlarm(long diff,int id)
    {
        if (diff>0)
            new NotificationScheduler(id,diff).setReminder(MainActivity.this, AlarmReceiver.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void cancelAlarm(int id){
        new NotificationScheduler(id,0).cancelReminder(MainActivity.this, AlarmReceiver.class);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}