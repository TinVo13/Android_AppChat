package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.appchat.Adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mtabAdapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat me!");

        mViewPager = findViewById(R.id.view_pager);
        mtabAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mtabAdapter);

        mTabLayout = findViewById(R.id.main_tab);
        mTabLayout.setupWithViewPager(mViewPager);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            auth.signOut();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.setting){
            Intent intent = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.searchbar){
            Intent intent = new Intent(MainActivity.this,SearchFriendActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.taonhom){
            Intent intent = new Intent(MainActivity.this,GroupActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.loimoi){
            Intent intent = new Intent(MainActivity.this,RequestActivity.class);
            startActivity(intent);
        }
        return true;
    }
}