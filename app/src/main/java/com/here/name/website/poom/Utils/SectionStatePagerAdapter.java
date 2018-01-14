package com.here.name.website.poom.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Charles on 6/30/2017.
 */

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter{

    private final List<Fragment> mFragmentList= new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments= new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers= new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames=new HashMap<>();

    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName){
        mFragmentList.add(fragment);
        mFragments.put(fragment, mFragmentList.size()-1);
        mFragmentNumbers.put(fragmentName, mFragmentList.size()-1);
        mFragmentNames.put(mFragmentList.size()-1,fragmentName);
    }

    //Returns the fragment with name @param
    //@param= fragmentName
    public Integer getFragmentNumber(String fragmentName){
        if (mFragmentNumbers.containsKey(fragmentName)){
            return mFragmentNumbers.get(fragmentName);
        } else{
            return null;
        }
    }

    //Returns the fragment with name @param
    //@param= fragmentName
    public Integer getFragmentNumber(Fragment fragment){
        if (mFragmentNumbers.containsKey(fragment)){
            return mFragmentNumbers.get(fragment);
        } else{
            return null;
        }
    }

    //Returns the fragment with name @param
    //@param= fragmentName
    public String getFragmentName(Integer fragmentNumber){
        if (mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames.get(fragmentNumber);
        } else{
            return null;
        }
    }
}
