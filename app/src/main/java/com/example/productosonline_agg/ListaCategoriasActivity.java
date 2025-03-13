package com.example.productosonline_agg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ListaCategoriasActivity extends AppCompatActivity {


    private List<Categoria> listaCategorias;

    private ListAdapterCategoria listAdapterPedido;

    private RecyclerView recyclerView;

    private LinearLayout contenidoLayout;
    private LinearLayout mProgressBar;

    private String urlCategorias = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/readAllCategories.php?type=JSON";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_categorias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listaCategoriasLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Obtenemos el recyclerView y le establecemos el linearLayoutManager
        recyclerView = findViewById(R.id.listRecyclerCategorias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contenidoLayout = (LinearLayout) findViewById(R.id.contenidoCategoriasLayout);
        mProgressBar = findViewById(R.id.progressBarCategoriasLayout);
        this.listaCategorias = new ArrayList<Categoria>();
        contenidoLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        cargaYMuestraCategorias();
    }

    /**
     * Carga y muestra por pantalla todas las categorias almacenadas en la base de datos
     */
    private void cargaYMuestraCategorias(){
        //Establecemos los parámetros necesarios para la url de la categoria
        urlCategorias += "&apikey=" + InicioActivity.mPreferences.getString("apikey", "");
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlCategorias,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            procesaJSONyCargaLista(utf8String); //Procesamos el JSON y cargamos la lista de categorias

                            listAdapterPedido = new ListAdapterCategoria(listaCategorias, ListaCategoriasActivity.this);
                            recyclerView.setAdapter(listAdapterPedido);

                            // Actualizamos la vista para ocultar la progressBar y mostrar la lista.
                            mProgressBar.setVisibility(View.GONE);
                            contenidoLayout.setVisibility(View.VISIBLE);
                        } catch (UnsupportedEncodingException e) {
                            Log.e("Error", "Error al parsear a UTF-8: " + e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListaCategoriasActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
                Intent intentMenuActivity = new Intent(ListaCategoriasActivity.this, MenuActivity.class);
                startActivity(intentMenuActivity);
                finish();
            }

        });
        queue.add(stringRequest);
    }

    /**
     * Procesamos el JSON y lo cargamos en una lista de categoria para posteriormente mostrarlo en pantalla gracias al recyclerView
     * @param response
     */
    private void procesaJSONyCargaLista(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseJson = jsonObject.getJSONObject("response"); //Obtenemos el response
            JSONArray categories = responseJson.getJSONArray("category"); //Obtenemos todas las categorias que devuelve
            for (int i = 0; i < categories.length(); i++) { //Hacemos un for por cada categroia que nos haya devuelot y lo almacenamos en la lista de categorias
                JSONObject categoryObject = categories.getJSONObject(i);
                int idCategory = categoryObject.getInt("idCategory");
                String name = categoryObject.getString("name");
                Categoria categoria = new Categoria(idCategory, name);
                listaCategorias.add(categoria);
            }
        } catch (JSONException e) {
            Log.d("XXXX", e.toString());
        }
    }
}