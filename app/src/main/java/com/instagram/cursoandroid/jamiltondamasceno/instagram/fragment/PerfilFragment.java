package com.instagram.cursoandroid.jamiltondamasceno.instagram.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.R;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.activity.EditarPerfilActivity;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.activity.FotoPerfilActivity;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.activity.PerfilAmigoActivity;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.activity.VisualizarPostagemActivity;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.adapter.AdapterGrid;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.helper.ConfiguracaoFirebase;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.helper.UsuarioFirebase;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.model.Postagem;
import com.instagram.cursoandroid.jamiltondamasceno.instagram.model.Usuario;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

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
    private Usuario usuarioLogado;
    private List<Postagem> postagens;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;
    private DatabaseReference postagensUsuarioRef;
    private AdapterGrid adapterGrid;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);


        // configuracoes iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // configurar refernecia postagens usuario
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(usuarioLogado.getId());

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        // Recuperação dos componentes
        inicializarComponentes(view);

        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        textNomePerfil.setText(usuarioPerfil.getDisplayName().toUpperCase());

        //abrir edição do perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });
        //inicializar image Loader
        inicializarImageLoader();
        // Carrega as fotos das postagens de um usuario
        carregarFotosPostagem();

        imagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FotoPerfilActivity.class);
                startActivity(i);
            }
        });
/***********************/
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Postagem postagem = postagens.get(position);
                Intent i = new Intent(getActivity(), VisualizarPostagemActivity.class);
                i.putExtra("postagem", postagem);
                i.putExtra("usuario",usuarioLogado);

                startActivity(i);
            }
        });
/**************************/
        return view;
    }

    public void carregarFotosPostagem(){

        postagens = new ArrayList<>();

        //Recupera as fotos postadas pelo usuario
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth( tamanhoImagem );

                List<String> urlFotos = new ArrayList<>();
                for( DataSnapshot ds: dataSnapshot.getChildren() ){
                    Postagem postagem = ds.getValue( Postagem.class );
                    postagens.add(postagem);
                    urlFotos.add( postagem.getCaminhoFoto() );

                    //Log.i("postagem", "url:" + postagem.getCaminhoFoto() );
                }

                 //Configurar adapter
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_postagem, urlFotos );
                gridViewPerfil.setAdapter( adapterGrid );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void inicializarComponentes(View view){
        gridViewPerfil     = view.findViewById(R.id.gridViewPerfil);
        progressBar        = view.findViewById(R.id.progressBarPerfil);
        imagePerfil        = view.findViewById(R.id.imageEditarPerfil);
        textNomePerfil     = view.findViewById(R.id.textNomePerfil);
        textPublicacoes    = view.findViewById(R.id.textPublicacoes);
        textSeguidores     = view.findViewById(R.id.textSeguidores);
        textSeguindo       = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
    }

    // Instancia a UniversalImageLoader
    public void inicializarImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();

        ImageLoader.getInstance().init(config);

    }

    private void recuperarDadosUsuarioLogado() {
        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getId());
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguindo = String.valueOf(usuario.getSeguindo());
                        String seguidores = String.valueOf(usuario.getSeguidores());

                        //configura valores recuperados
                        textPublicacoes.setText(postagens);
                        textSeguidores.setText(seguidores);
                        textSeguindo.setText(seguindo);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }



    private void recuperarFotoUsuario(){

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        //recuperar foto do usuario
        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if (caminhoFoto != null) {
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity())
                    .load(url)
                    .into(imagePerfil);
        }
        textNomePerfil.setText(usuarioLogado.getNome());
    }

    @Override
    public void onStart() {
        super.onStart();

        // recupera dados do usuario logado
        recuperarDadosUsuarioLogado();
        recuperarFotoUsuario();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }

}
