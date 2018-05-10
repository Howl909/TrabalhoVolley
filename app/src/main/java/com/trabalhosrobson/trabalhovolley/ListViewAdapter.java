package com.trabalhosrobson.trabalhovolley;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liver on 05/05/2018.
 */

public class ListViewAdapter extends ArrayAdapter<UsuarioConst> {

    //lista de usuarios
    private List<UsuarioConst> userList;
    private List<UsuarioConst> orig;
    private Context mCtx;

    public ListViewAdapter(List<UsuarioConst> userList, Context mCtx) {
        super(mCtx, R.layout.list_view, userList);
        this.userList = userList;
        this.mCtx = mCtx;
    }

    //Metodo que traz que traz o tamanho da lista
    @Override
    public int getCount() {
        return userList.size();
    }
    //Ultilização da classe Filter do android
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<UsuarioConst> results = new ArrayList<UsuarioConst>();
                //verifica se a lista esta vazia
                if (orig == null) {
                    //retorna a lista ao orig
                    orig = userList;
                }
                //verifica se o usuario digitou algo
                if (constraint != null) {
                    //transforma para letra minuscula
                    constraint = constraint.toString().toLowerCase();
                    //verifica se temos algo na lista original
                    if (orig != null && orig.size() > 0) {
                        //para cada obj do UsuarioConst dentro lista
                        for (final UsuarioConst g : orig) {//Ocorerá para cada usuario que está na lista
                            // faz a comparação do constraint que esta sendo digitado com todos os usuarios
                            if ((g.getNome().toLowerCase().contains(constraint.toString())) ||
                                    (g.getSobrenome().toLowerCase().contains(constraint.toString())) ||
                                    g.getEmail().toLowerCase().contains(constraint.toString())) {
                                // resultado foi inserido a results
                                results.add(g);
                            }
                        }
                    }
                    // o valor do results foi inserido no oReturn
                    oReturn.values = results;
                }
                return oReturn;
            }
            // impedir o compilador de emitir algumas advertências
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userList = (ArrayList<UsuarioConst>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View listViewItem = inflater.inflate(R.layout.list_view, null, true);

        TextView txtNome = listViewItem.findViewById(R.id.txtNome);
        TextView txtEmail = listViewItem.findViewById(R.id.txtEmail);
        final ImageView img = listViewItem.findViewById(R.id.img);


        UsuarioConst usuarioConst = userList.get(position);
        //concatena nome e sobrenome
        txtNome.setText(usuarioConst.getNome() + " " +usuarioConst.getSobrenome());
        txtEmail.setText(usuarioConst.getEmail());
        String link = usuarioConst.getImg();

        RequestQueue requestQueue = Volley.newRequestQueue(mCtx);

        // Iniciando um ImageRequest
        ImageRequest imageRequest = new ImageRequest(
                link, // URL da imagem
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        img.setImageBitmap(response);
                    }
                },
                0, // Img width
                0, // Img height
                ImageView.ScaleType.CENTER_CROP,//define o "corte" da img
                Bitmap.Config.RGB_565, //configurações de decode
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        // add requeste a fila
        requestQueue.add(imageRequest);

        return listViewItem;
    }
}
