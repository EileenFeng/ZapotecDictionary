package edu.haverford.cs.zapotecdictionary;

// reference: https://gist.github.com/nesquena/f54a991ccb4e5929e0ec

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class FragmentTabListener<T extends Fragment> implements TabListener {
    private Fragment mFragment;
    private final FragmentActivity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private final int mfragmentContainerId;
    private final Bundle mfragmentArgs;

    // This version defaults to replacing the entire activity content area
    // new FragmentTabListener<SomeFragment>(this, "first", SomeFragment.class))
    public FragmentTabListener(FragmentActivity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        mfragmentContainerId = android.R.id.content;
        mfragmentArgs = new Bundle();
    }

    // This version supports specifying the container to replace with fragment content
    // new FragmentTabListener<SomeFragment>(R.id.flContent, this, "first", SomeFragment.class))
    public FragmentTabListener(int fragmentContainerId, FragmentActivity activity,
                               String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        mfragmentContainerId = fragmentContainerId;
        mfragmentArgs = new Bundle();
    }

    // This version supports specifying the container to replace with fragment content and fragment args
    // new FragmentTabListener<SomeFragment>(R.id.flContent, this, "first", SomeFragment.class, myFragmentArgs))
    public FragmentTabListener(int fragmentContainerId, FragmentActivity activity,
                               String tag, Class<T> clz, Bundle args) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        mfragmentContainerId = fragmentContainerId;
        mfragmentArgs = args;
    }

    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
        FragmentTransaction sft = mActivity.getSupportFragmentManager().beginTransaction();
        mFragment = Fragment.instantiate(mActivity, mClass.getName(), mfragmentArgs);
        sft.add(mfragmentContainerId, mFragment, mTag);
        sft.replace(android.R.id.content, mFragment, mTag);
        if(tab.getPosition() != 0) {
            Fragment cur = null;
            try {
                cur = mActivity.getSupportFragmentManager().findFragmentByTag("WordOfDay");
            } catch (Exception e) {
                Log.e("onTabSelected",  "when click on " + mTag + e.toString());
            }
            if(cur != null) {
                sft.remove(cur);
            }
        } else {
            String oldQuery = ((SearchFragment)mFragment).getOldQuery();
            if(oldQuery == null || oldQuery.length() == 0){
                Fragment f = mActivity.getSupportFragmentManager().findFragmentByTag("WordOfDay");
                if(f == null) {
                    sft.add(android.R.id.content, new WordOfDayFragment(), "WordOfDay");
                    mActivity.getSupportFragmentManager().popBackStackImmediate("WordOfDay", 0);
                } //else if (mActivity.getSupportFragmentManager().get)
            }
        }
        sft.commit();
    }

    public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
        FragmentTransaction sft = mActivity.getSupportFragmentManager().beginTransaction();
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            sft.detach(mFragment);
        }
        sft.commit();
    }

    public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}