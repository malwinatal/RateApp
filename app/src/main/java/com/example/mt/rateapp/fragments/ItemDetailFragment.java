package com.example.mt.rateapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.AppCompatRatingBar;
import android.widget.TextView;

import com.example.mt.rateapp.R;
import com.example.mt.rateapp.models.Item;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ItemDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Item mItem;

    private OnFragmentInteractionListener mListener;


    public ItemDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param item Parameter 1.
     *
     * @return A new instance of fragment ItemDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemDetailFragment newInstance(Item item) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItem = (Item) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);
        CircleImageView img = view.findViewById(R.id.image_show);
        TextView name = view.findViewById(R.id.name_show);
        AppCompatRatingBar rate = view.findViewById(R.id.ratingBar_show);
        TextView notes = view.findViewById(R.id.notes_show);
        TextView date = view.findViewById(R.id.date_show);

        img.setImageURI(Uri.parse(mItem.imageUrl));
        name.setText(mItem.name);
        rate.setRating(mItem.score);
        rate.setIsIndicator(false);
        Log.v(mItem.name, rate.getRating()+"");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        date.setText(simpleDateFormat.format(mItem.date));
        notes.setText(mItem.notes);

        FloatingActionButton fabDel = view.findViewById(R.id.fab_del);
        fabDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemButton(mItem);
            }
        });

        FloatingActionButton fabEd = view.findViewById(R.id.fab_edit);
        fabEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editItemButton(mItem);
            }
        });

        Log.v(mItem.name, mItem.score+"");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void removeItemButton(Item item) {
        if (mListener != null) {
            mListener.removeItemInteraction(item);
        }
    }

    public void editItemButton(Item item) {
        if (mListener != null) {
            mListener.editItemInteraction(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void removeItemInteraction(Item item);
        void editItemInteraction(Item item);
    }

}
