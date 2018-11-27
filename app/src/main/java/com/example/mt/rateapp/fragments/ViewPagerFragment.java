package com.example.mt.rateapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mt.rateapp.R;
import com.example.mt.rateapp.models.Item;

import java.io.Serializable;
import java.util.List;

public class ViewPagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_INDEX = "index";
    private static final String ARG_LIST = "list";

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<Item> items;
    private int index;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ViewPagerFragment newInstance(int index, List<Item> items) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putSerializable(ARG_LIST, (Serializable) items);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_INDEX, 0);
            items = (List<Item>) getArguments().getSerializable(ARG_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        mPager = view.findViewById(R.id.ViewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager(), items);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(index);
        return view;
    }

    public void notifyDataSetChange(int index){
        //mPagerAdapter.notifyDataSetChanged();
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(index);
//        Log.v("items", items.toString());
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        List<Item> items;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Item> items) {
            super(fm);
            this.items = items;
        }

        @Override
        public Fragment getItem(int position) {
            return ItemDetailFragment.newInstance(items.get(position));
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }
}
