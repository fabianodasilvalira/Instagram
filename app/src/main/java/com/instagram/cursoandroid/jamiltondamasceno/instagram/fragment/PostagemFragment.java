package com.instagram.cursoandroid.jamiltondamasceno.instagram.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.instagram.cursoandroid.jamiltondamasceno.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostagemFragment extends Fragment {


    public PostagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_postagem, container, false);
    }

}
