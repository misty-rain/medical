package com.hope.medical.widget;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


/**
 * Created by wutao on 2016/4/19.
 */
public class LoadFragment {
    FragmentManager fragmentManager;
    public LoadFragment(FragmentManager fragmentManager){

        this.fragmentManager = fragmentManager;

    }

    public void initFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       // fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
