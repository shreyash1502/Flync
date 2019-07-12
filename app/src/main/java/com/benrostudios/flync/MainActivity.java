package com.benrostudios.flync;


import android.os.Bundle;

import android.view.MenuItem;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class
MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{
//You always Implement the BottomNavBar when working with frags


    public static String tag;
    public static Fragment passer;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Just defining where the Nav Bar actually is
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(this);
        //Loading the default Fragment that should be loaded at start of the app
        if (savedInstanceState != null)
        {
            tag = savedInstanceState.getString("FragTag");
            getSupportFragmentManager().findFragmentByTag(tag);
            Toast.makeText(this, "FragReused", Toast.LENGTH_SHORT).show();

        }
        else
        {
            loadFrag(new HomeFragment(), "home");
        }
    }

    //This is a fragment transaction , basically it manages the fragments and swaps them on the activity
    public boolean loadFrag(Fragment fragment, String Tag)
    {
        if (fragment != null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, fragment, Tag)
                    .commit();

            return false;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        Fragment fragmenthere = null;
        //A super simple switch case , ALL HAIL NETBEANS

        switch (menuItem.getItemId())
        {
            case R.id.navigation_home:
                fragmenthere = new HomeFragment();
                tag = "home";
                break;
            case R.id.navigation_history:
                fragmenthere = new HistoryFragment();
                tag = "history";
                break;
            case R.id.navigation_settings:
                fragmenthere = new SettingsFragment();
                tag = "settings";
                break;
        }

        loadFrag(fragmenthere, tag);
        passer = fragmenthere;


        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("FragTag", tag);

    }



}
