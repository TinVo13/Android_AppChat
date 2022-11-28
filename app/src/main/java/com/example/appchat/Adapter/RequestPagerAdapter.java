package com.example.appchat.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.appchat.ReceivedFragment;
import com.example.appchat.SendedFragment;

public class RequestPagerAdapter extends FragmentStatePagerAdapter {
    public RequestPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                return new ReceivedFragment();
            }
            case 1:{
                return new SendedFragment();
            }
            default:
                return new ReceivedFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:{
                title = "Đã nhận";
                break;
            }
            case 1:{
                title = "Đã gửi";
                break;
            }
        }
        return title;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
