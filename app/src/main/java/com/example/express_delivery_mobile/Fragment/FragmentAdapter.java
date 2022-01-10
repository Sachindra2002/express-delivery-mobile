package com.example.express_delivery_mobile.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new DriverStartedMailsFragment();
            case 2:
                return new DriverPickedupMailsFragment();
            case 3:
                return new DriverTransitMailsFragment();
            case 4:
                return new DriverOutForDeliveryFragment();
            case 5:
                return new DriverDeliveredMailsFragment();
        }
        return new DriverAcceptedMailsFragment();
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
