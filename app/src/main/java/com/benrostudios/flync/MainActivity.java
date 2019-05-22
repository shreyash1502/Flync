package com.benrostudios.flync;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

public class
MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);

        navigation.setOnNavigationItemSelectedListener(this);
        loadFrag(new HomeFragment());
    }

    private boolean loadFrag (Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame,fragment)
                    .commit();


            return false;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragmenthere = null;
        switch (menuItem.getItemId()){
            case R.id.navigation_home : fragmenthere =  new HomeFragment();
                break;
            case R.id.navigation_history : fragmenthere = new History();
                break;
            case R.id.navigation_settings : fragmenthere = new Settings();
                break;}

        loadFrag(fragmenthere);


        return true;
    }
}
