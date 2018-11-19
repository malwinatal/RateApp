package com.example.mt.rateapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.mt.rateapp.models.Item;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "image";
    private FloatingActionButton fabSave;
    private EditText name;
    private EditText notes;
    private RatingBar rate;

    // TODO: Rename and change types of parameters
    private Bitmap image;

    private OnFragmentInteractionListener mListener;

    public AddingFragment() {
        // Required empty public constructor
    }

    public static AddingFragment newInstance(Bitmap imageBitmap) {
        AddingFragment fragment = new AddingFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1,imageBitmap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adding, container, false);
        ImageView imageView = view.findViewById(R.id.image_camera);
        imageView.setImageBitmap(image);
        fabSave = view.findViewById(R.id.fab_save);
        name = view.findViewById(R.id.editText_name);
        notes = view.findViewById(R.id.editText_notes);
        rate = view.findViewById(R.id.ratingBar);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    FileOutputStream out = new FileOutputStream(photoFile);
                    image.compress(Bitmap.CompressFormat.PNG, 100, out);
                    String mCurrentPhotoPath = photoFile.getAbsolutePath();
                    String nameTxt = name.getText().toString();
                    String notesTxt = notes.getText().toString();
                    int rating = Math.round(rate.getRating());
                    Item item = new Item(nameTxt, rating, notesTxt, mCurrentPhotoPath, Calendar.getInstance().getTime());
                    sendItem(item);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void sendItem(Item item) {
        if (mListener != null) {
            mListener.onFragmentInteraction(item);
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
        void onFragmentInteraction(Item item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
