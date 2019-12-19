package com.instagram.cursoandroid.jamiltondamasceno.instagram.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.R;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.activity.EditarPerfilActivity;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.helper.UsuarioFirebase;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo, textNomePerfil;
    private Button buttonAcaoPerfil;


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //configuracoes dos componentes
        gridViewPerfil     = view.findViewById(R.id.gridViewPerfil);
        progressBar        = view.findViewById(R.id.progressBarPerfil);
        imagePerfil        = view.findViewById(R.id.imageEditarPerfil);
        textNomePerfil     = view.findViewById(R.id.textNomePerfil);
        textPublicacoes    = view.findViewById(R.id.textPublicacoes);
        textSeguidores     = view.findViewById(R.id.textSeguidores);
        textSeguindo       = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);



        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        textNomePerfil.setText(usuarioPerfil.getDisplayName().toUpperCase());

        //abror edição do perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

}
