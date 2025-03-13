package com.example.productosonline_agg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ListaProductosActivity extends AppCompatActivity {

    private List<Producto> listaProductos;

    private ListAdapterProducto listAdapterProducto;

    private RecyclerView recyclerView;

    private LinearLayout contenidoLayout;
    private LinearLayout mProgressBar;
    public static Button botonPedido;

    protected static ArrayList<Producto> numeroProductos = new ArrayList<Producto>();
    private String urlLeerProductosCategoria = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/readProductWithCategoryRelated.php?type=JSON";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_productos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listaProductosLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        botonPedido = (Button) findViewById(R.id.pedidosButton);
        botonPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor preferencesEditor =
                        InicioActivity.mPreferences.edit();
                preferencesEditor.putBoolean("productosSeleccionable", false); //Como estamos en ListaProductosActivity, no queremos que los pedidos sean seleccionables
                preferencesEditor.apply();
                Intent detallePedidoActivity = new Intent(ListaProductosActivity.this, DetallePedidoActivity.class);
                detallePedidoActivity.putExtra("activityPedidos", false); //Pasamos un boolean para saber que venimos de ListaProductosActivity
                startActivity(detallePedidoActivity);
            }
        });
        if(!numeroProductos.isEmpty()){ //Si la lista de productos no está vacío, se muestra el boton de hacer pedido
            botonPedido.setVisibility(View.VISIBLE);
        }
        recyclerView = findViewById(R.id.productosCategoriaRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DisplayMetrics metrics = new DisplayMetrics();
        //Esto es para que, independientemente de la pantalla, el boton de enviar pedido, siempre aparezca en la parte inferior de la pantala
        getWindowManager().getDefaultDisplay().getMetrics(metrics); //Se obtiene el tamaño de la pantalla
        int width = metrics.widthPixels; // ancho absoluto en pixels
        int height = metrics.heightPixels; // alto absoluto en pixels
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = height-450;
        recyclerView.setLayoutParams(params);
        contenidoLayout = findViewById(R.id.contenidoProductoCategoria);
        mProgressBar = findViewById(R.id.progressBarProductosCategoria);
        this.listaProductos = new ArrayList<Producto>();
        contenidoLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        cargaYMuestraListaProductos();
    }

    /**
     * Carga y muestra por pantalla los distintos productos existentes en la base de datos
     */
    private void cargaYMuestraListaProductos(){
        urlLeerProductosCategoria += "&apikey=" + InicioActivity.mPreferences.getString("apikey", "") + "&categoryid=" + getIntent().getExtras().getInt("categoriaId");
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlLeerProductosCategoria,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            procesaJSONyCargaLista(utf8String);

                            listAdapterProducto = new ListAdapterProducto(listaProductos, ListaProductosActivity.this);
                            recyclerView.setAdapter(listAdapterProducto);

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
                Toast.makeText(ListaProductosActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
                Intent intentListaCategoriaActivity = new Intent(ListaProductosActivity.this, ListaCategoriasActivity.class);
                startActivity(intentListaCategoriaActivity);
            }

        });

        queue.add(stringRequest);
    }

    /**
     * Lee el JSON de los productos existentes y los añade en la lista de productos
     * @param response
     */
    private void procesaJSONyCargaLista(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseJson = jsonObject.getJSONObject("response");
            JSONArray products = responseJson.getJSONArray("products");//Cogemos los productos, y lo recorremos en un bucle para añadirlo a la lista

            for (int i = 0; i < products.length(); i++) {
                JSONObject productObject = products.getJSONObject(i);
                int idproduct = productObject.getInt("idproduct");
                String name = productObject.getString("name");
                String fileurl = productObject.getString("fileurl");
                String category = productObject.getString("category");

                Producto product = new Producto(idproduct, name, category, fileurl);
                this.listaProductos.add(product);
            }
        } catch (JSONException e) {
            Intent listaCategoriaActivity = new Intent(ListaProductosActivity.this, ListaCategoriasActivity.class);
            startActivity(listaCategoriaActivity);
            Toast.makeText(this, "No se han podido obtener los datos de los productos", Toast.LENGTH_LONG).show();
        }
    }
}