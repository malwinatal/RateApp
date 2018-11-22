package com.example.mt.rateapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.AppCompatRatingBar;

import com.example.mt.rateapp.R;
import com.example.mt.rateapp.models.Item;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private Item item;
    private FloatingActionButton fabSave;
    private EditText name;
    private EditText notes;
    private AppCompatRatingBar rate;

    private OnFragmentInteractionListener mListener;

    public EditingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param item Parameter 1.
     *
     * @return A new instance of fragment EditingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditingFragment newInstance(Item item) {
        EditingFragment fragment = new EditingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (Item) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding, container, false);

        ImageView imageView = view.findViewById(R.id.image_camera);
        name = view.findViewById(R.id.editText_name);
        notes = view.findViewById(R.id.editText_notes);
        rate = view.findViewById(R.id.ratingBar);
        fabSave = view.findViewById(R.id.fab_save);

        imageView.setImageURI(Uri.parse(item.imageUrl));
        name.setText(item.name);
        notes.setText(item.notes);
        rate.setRating(item.score);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item newItem = item.clone();
                newItem.name = name.getText().toString();
                newItem.notes = notes.getText().toString();
                newItem.score = Math.round(rate.getRating());

                onEditButtonPressed(item, newItem);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onEditButtonPressed(Item item, Item newItem) {
        if (mListener != null) {
            mListener.onEditInteraction(item, newItem);
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
        void onEditInteraction(Item item, Item newItem);
    }
}
