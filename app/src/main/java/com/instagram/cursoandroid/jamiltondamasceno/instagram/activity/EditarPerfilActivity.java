package com.instagram.cursoandroid.jamiltondamasceno.instagram.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.R;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.helper.ConfiguracaoFirebase;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.helper.UsuarioFirebase;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;
    private String identificadorUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //configuracoes inicias
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();



        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //inicializar componentes
        inicializarComponentes();

        //recuperar dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText(usuarioPerfil.getDisplayName().toUpperCase());
        editEmailPerfil.setText(usuarioPerfil.getEmail());

        Uri url = usuarioPerfil.getPhotoUrl();
        if( url != null  ){
            Glide.with(EditarPerfilActivity.this)
                    .load(url)
                    .into(imageEditarPerfil);

        }else{
            imageEditarPerfil.setImageResource(R.drawable.avatar);
        }

        //salvar alteracoes do nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeAtualizado = editNomePerfil.getText().toString();

                //atualizar nome no perfil
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                //atualizar nome no banco de dados
                usuarioLogado.setNome( nomeAtualizado );
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this,
                        "Dados alterados com sucesso",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //alterar foto do usuario
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{
                //selecao apenas da galeria
                switch( requestCode ){
                    case SELECAO_GALERIA:

                        Uri localImagemSeleconada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSeleconada);
                        break;
                }
                // caso tenha sido escolhido uma imagem
                if( imagem != null ){
                    //configura imagem na tela
                    imageEditarPerfil.setImageBitmap(imagem);

                    //recuperar dados da imagem parao firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();


                    // salvar imagem no firebase
                    StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child( identificadorUsuario +".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // recuperar local da foto

                            Uri url = taskSnapshot.getDownloadUrl();
                            atualizarFotoUsuario(url);

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void atualizarFotoUsuario(Uri url){
        //atualizarr foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        // atualizar foto no firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();
        Toast.makeText(EditarPerfilActivity.this,
                "Sua foto foi atualizada",
                Toast.LENGTH_SHORT).show();
    }

    public void inicializarComponentes(){
        imageEditarPerfil      = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto        = findViewById(R.id.textAlterarfoto);
        editNomePerfil         = findViewById(R.id.editNomePerfil);
        editEmailPerfil        = findViewById(R.id.editEmailPerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editEmailPerfil.setFocusable(false);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
