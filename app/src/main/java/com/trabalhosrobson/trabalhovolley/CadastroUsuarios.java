package com.trabalhosrobson.trabalhovolley;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CadastroUsuarios extends AppCompatActivity {

    Button btnFotos;
    // variavel constante
    private static int CODE_REQ = 0205;
    Bitmap photoBitmap;
    ImageView photoView;

    EditText nome,sobrenome,email;
    // add requeste a fila
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    // link para a web service
    String HttpUrl = "https://sistemagte.xyz/android/trabRobson/cad.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuarios);

        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(CadastroUsuarios.this);

        btnFotos = findViewById(R.id.btnImg);
        photoView = findViewById(R.id.imgSelecionada);
        nome = findViewById(R.id.nome);
        sobrenome = findViewById(R.id.sobrenome);
        email = findViewById(R.id.email);

        btnFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inicialização da intent para poder selecionar uma foto
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, CODE_REQ);
            }

        });
    }

    //Metodo que é executado ao selecionar a imagem.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK && requestCode == CODE_REQ) {
            Uri photoData = data.getData();

            try {
                //transforma a img em formato bitmap
                photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoData);
                photoView.setImageBitmap(photoBitmap);
            } catch (IOException e) {
                // mostra qual erro ocorreu
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    public void cadastrarImg(View view) {
        // define uma mensagem para o progressDialog
        progressDialog.setMessage("Cadastrando");
        // mostra a mensagem para o progressDialog
        progressDialog.show();
        // o começo do stringRequest, para a interação com o php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        progressDialog.dismiss();// remove feedback de cadastrado
                        Toast.makeText(CadastroUsuarios.this, "Cadastrado!", Toast.LENGTH_SHORT).show();
                        Intent tela = new Intent(CadastroUsuarios.this, MainActivity.class);
                        startActivity(tela);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // retira o progressDialog
                        progressDialog.dismiss();

                        Toast.makeText(CadastroUsuarios.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // um objeto que mapeia chaves para valor
                Map<String, String> params = new HashMap<>();
                // parametros que seram mandados para a webservice
                params.put("nome", nome.getText().toString());
                params.put("sobrenome", sobrenome.getText().toString());
                params.put("email", email.getText().toString());
                params.put("foto", imageToString(photoBitmap));

                return params;
            }
        };
        //limpa o cache da queue
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
    // metodo que transforma bitmap(img) em string
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);//comprime em JPEG
        byte[] imgBytes = byteArrayOutputStream.toByteArray();//Transforma em um array de bytes
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);//nosso array leva um encode para base64m q é retornado como parametro para a webservice
    }
}
