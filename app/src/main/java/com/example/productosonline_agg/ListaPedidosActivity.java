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

public class ListaPedidosActivity extends AppCompatActivity {

    protected static List<Pedido> listaPedidos;

    private ListAdapterPedido listAdapterPedido;

    private RecyclerView recyclerView;

    private LinearLayout contenidoLayout;
    private LinearLayout mProgressBar;

    private String urlLeerPedidos = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/readOrderRelatedWithAUser.php?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_pedidos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listaPedidoLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Obtenemos el recyclerView y le establecemos el linearLayoutManager
        recyclerView = findViewById(R.id.listRecyclerPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contenidoLayout = (LinearLayout) findViewById(R.id.listaPedidoContentLayout);
        mProgressBar = findViewById(R.id.listaPedidoProgressBarLayout);
        this.listaPedidos = new ArrayList<Pedido>();
        contenidoLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        cargaYMuestraListaPedidos();
    }

    /**
     * Carga en la lista de pedidos los pedidos existentes en la base de datos, y se muestra todos los pedidos por pantalla gracias al recyclerView
     */
    private void cargaYMuestraListaPedidos() {
        //Establecemos los parámetros al url de leer pedidos
        urlLeerPedidos += "apikey=" + InicioActivity.mPreferences.getString("apikey", "");
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlLeerPedidos,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            procesaJSONyCargaLista(utf8String); //Se procesa el JSON y se carga la lista de pedidos
                            if(!listaPedidos.isEmpty()){ //Si una vez procesado el JSON hay pedidos, se muestran por pantalla gracias al recyclerView
                                listAdapterPedido = new ListAdapterPedido(listaPedidos, ListaPedidosActivity.this);
                                recyclerView.setAdapter(listAdapterPedido);
                                // Actualizamos la vista para ocultar la progressBar y mostrar la lista.
                                mProgressBar.setVisibility(View.GONE);
                                contenidoLayout.setVisibility(View.VISIBLE);
                            }else{ //En caso de que no haya pedidos, se vuelve al activity anterior
                                Intent menuActivity = new Intent(ListaPedidosActivity.this, MenuActivity.class);
                                startActivity(menuActivity);
                                Toast.makeText(ListaPedidosActivity.this, "No hay pedidos que mostrar", Toast.LENGTH_LONG).show();
                            }
                        } catch (UnsupportedEncodingException e) {
                            Log.e("Error", "Error al parsear a UTF-8: " + e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListaPedidosActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
                Intent intentMenuActivity = new Intent(ListaPedidosActivity.this, MenuActivity.class);
                startActivity(intentMenuActivity);
            }

        });

        queue.add(stringRequest);
    }

    /**
     * Se procesa el JSON de leer productos y se carga la lista de pedidos
     * @param response
     */
    private void procesaJSONyCargaLista(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");

            if (status.equals("OK")) { //Si el status es OK, se obitene los valores
                JSONArray valuesArray = jsonObject.getJSONArray("values");


                for (int i = 0; i < valuesArray.length(); i++) { //Por cada pedido, se obtiene sus datos
                    JSONObject orderObject = valuesArray.getJSONObject(i);
                    int pedidoId = orderObject.getInt("orderid");
                    int tiendaId = orderObject.getInt("shopid");
                    String estadoPedido = orderObject.getString("state");
                    String tienda = orderObject.getString(("shop"));
                    JSONArray productsArray = orderObject.getJSONArray("products");
                    ArrayList<Integer> products = new ArrayList<>();
                    for (int j = 0; j < productsArray.length(); j++) {
                        products.add(productsArray.getInt(j));
                    }
                    //Por cada pedido, se añade a la lista de pedidos
                    listaPedidos.add(new Pedido(pedidoId, tienda, estadoPedido, products, tiendaId));
                }
            }
        } catch (JSONException e) {
            Intent menuActivity = new Intent(ListaPedidosActivity.this, MenuActivity.class);
            startActivity(menuActivity);
            Toast.makeText(this, "No se ha podido obtener los datos de los pedidos", Toast.LENGTH_LONG).show();
        }
    }
}