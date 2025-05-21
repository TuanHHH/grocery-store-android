package com.hp.grocerystore.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hp.grocerystore.view.fragment.HomeFragment;
import com.hp.grocerystore.view.fragment.ProfileFragment;
import com.hp.grocerystore.view.fragment.SearchFragment;
import com.hp.grocerystore.view.fragment.WishlistFragment;

public class ViewpageAdapter extends FragmentStateAdapter {
    public ViewpageAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new SearchFragment();
            case 2: return new WishlistFragment();
            case 3: return new ProfileFragment();
            default: return new HomeFragment();
        }
    }    @Override
    public int getItemCount() {
        return 4;
    }

}
