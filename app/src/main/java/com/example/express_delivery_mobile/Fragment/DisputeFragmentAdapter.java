package com.example.express_delivery_mobile.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DisputeFragmentAdapter extends FragmentStateAdapter {
    public DisputeFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new PackageDisputesFragment();
        }

        return new GeneralDisputesFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
