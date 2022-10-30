package com.example.appchat.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.appchat.ChatFragment;
import com.example.appchat.FriendFragment;
import com.example.appchat.ProfileFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                return new ChatFragment();
            }
            case 1:{
                return new FriendFragment();
            }
            case 2:{
                return new ProfileFragment();
            }
            default: return new ChatFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:{
                return "Chats";
            }
            case 1:{
                return "Contacts";
            }
            case 2:{
                return "Groups";
            }
            default:
                return null;
        }
    }
}
