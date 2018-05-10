package com.example.taras.threetabsapp;



import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class ImageTab extends Fragment {
    private OnFragmentInteractionListener mListener;

    private EditText editURL;
    private Button btn_Download;
    private ImageView imageView;

    //private static final String URL = "http://www.tate.org.uk/art/images/work/T/T05/T05010_10.jpg";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView = (ImageView) getActivity().findViewById(R.id.imageView_ImageTab_ID);
        editURL = (EditText) getActivity().findViewById(R.id.edit_UploadImage_ID);
        btn_Download = (Button) getActivity().findViewById(R.id.btn_UploadImage_ID);
        btn_Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Picasso.with(getActivity())
                            .load(editURL.getText().toString())
                            .placeholder(R.drawable.download)
                            .error(R.drawable.error)
                            .resize(2000, 2000)
                            .into(imageView);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Paste link in field, or maybe bad link check it",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onDetach() {
        super.onDetach();
        InputMethodManager inm = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE) ;
        inm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        mListener = null;
    }

    interface OnFragmentInteractionListener {}

}

