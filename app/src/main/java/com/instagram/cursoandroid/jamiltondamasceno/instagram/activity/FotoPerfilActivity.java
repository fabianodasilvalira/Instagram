package com.instagram.cursoandroid.jamiltondamasceno.instagram.activity;

import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.R;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.helper.UsuarioFirebase;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.model.Usuario;

public class FotoPerfilActivity extends AppCompatActivity {

    private ImageView imagePerfil;
    private TextView textNomePerfil;
    private Usuario usuarioLogado;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_perfil);

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Foto de perfil");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);


        imagePerfil = findViewById(R.id.imageFotoPerfilPrincipal);
        textNomePerfil = findViewById(R.id.textNomePerfilPrincipal);


        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        carregarDadosUsuarioLogado();



    }

    private void carregarDadosUsuarioLogado(){

        // recuperar nome usuario logado
        textNomePerfil.setText(usuarioLogado.getNome());

        //recuperar foto do usuario
        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if (caminhoFoto != null) {
            abrirDialogCarregamento("Carregando foto");
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(this)
                    .load(url)
                    .into(imagePerfil);

        }
        dialog.cancel();
    }

    private void abrirDialogCarregamento(String titulo){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle( titulo );
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;

    }
}
