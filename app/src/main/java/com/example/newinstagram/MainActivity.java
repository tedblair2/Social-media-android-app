package com.example.newinstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.newinstagram.Fragments.HomeFragment;
import com.example.newinstagram.Fragments.NotificationFragment;
import com.example.newinstagram.Fragments.ProfileFragment;
import com.example.newinstagram.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragment=new HomeFragment();
                        break;
                    case R.id.nav_search:
                        fragment=new SearchFragment();
                        break;
                    case R.id.nav_add:
                        fragment=null;
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        fragment=new NotificationFragment();
                        break;
                    case R.id.nav_profile:
                        fragment=new ProfileFragment();
                        break;
                }
                if (fragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }

                return true;
            }
        });
        Bundle bundle=getIntent().getExtras();
        if (bundle != null){
            String profileId=bundle.getString("publisherId");
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }
    }
}