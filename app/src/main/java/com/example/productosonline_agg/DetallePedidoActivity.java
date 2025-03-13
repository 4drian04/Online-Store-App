package com.example.productosonline_agg;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetallePedidoActivity extends AppCompatActivity implements OnMapReadyCallback {


    private Button eliminarPedidoButton;
    private double latitudTienda;

    private double longitudTienda;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_REQUEST_CODE = 1001;
    private List<Producto> listaProductos;

    private ListAdapterProducto listAdapterProducto;

    private RecyclerView recyclerView;

    private LinearLayout contenidoLayout;
    private LinearLayout mProgressBar;

    private String leerTienda = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/readShop.php?";
    private String urlLeerProductos = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/readProductWithOrderRelated.php?";
    private String urlEliminarPedido = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/deleteOrder.php?";

    private String urlLeerTodasTiendas = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/readAllShop.php?";

    private String urlCrearPedido = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/createOrder.php?type=JSON";

    private String urlLineaCompra = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/createOrderProduct.php?type=JSON";
    private Pedido pedido;

    private int shopId = 0;

    private HashMap<Marker, Integer> marcadores = new HashMap<Marker, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_pedido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detallePedidoLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Instancia de FusedLocationProviderClient, que es la API de Google para obtener ubicaciones de forma eficiente.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Gestionamos el fragmento de pantalla en el que se ubicará nuestro mapa.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        recyclerView = findViewById(R.id.productosRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //Cogemos el RecycleView del DetallePedido y le asignamos el LinearLayoutManager
        contenidoLayout = findViewById(R.id.contenidoProducto);
        mProgressBar = findViewById(R.id.progressBarProductos);
        this.listaProductos = new ArrayList<Producto>();
        contenidoLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        //Si la Activity del que viene es de ActivityListaPedidos, ponemos que el botón realice una función
        if(getIntent().getExtras().getBoolean("activityPedidos")){
            pedido = ListaPedidosActivity.listaPedidos.get(getIntent().getExtras().getInt("productId"));
            eliminarPedidoButton = (Button) findViewById(R.id.buttonEliminarPedido);
            if(pedido.getEmpleadoAsignado().equals("PENDING")){
                eliminarPedidoButton.setVisibility(View.VISIBLE);
                eliminarPedidoButton.setOnClickListener(new View.OnClickListener() { //El botón lo que hará será eliminar el pedido, siempre y cuando el pedido no haya sido servido
                    @Override
                    public void onClick(View view) {
                        eliminarPedido();
                    }
                });
            }
            cargaYMuestraListaProductos();
        }else{ //Si la Activity del que viene no es ActivityListaPedido, ponemos que el botón realice otra función
            listAdapterProducto = new ListAdapterProducto(ListaProductosActivity.numeroProductos,DetallePedidoActivity.this);
            recyclerView.setAdapter(listAdapterProducto);
            eliminarPedidoButton = (Button) findViewById(R.id.buttonEliminarPedido);
            eliminarPedidoButton.setText("CONFIRMAR PEDIDO");
            eliminarPedidoButton.setVisibility(View.VISIBLE);
            eliminarPedidoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(shopId==0){ //Si no ha seleccionado una tienda, le mostramos un mensaje de error
                        Toast.makeText(DetallePedidoActivity.this, "Debes seleccionar una tienda para comprar el pedido.", Toast.LENGTH_LONG).show();
                    }else{ //Si selecciona una tienda y le da click a confirmar pedido, realizará el pedido
                        realizarPedido();
                    }
                }
            });
            contenidoLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Realiza el pedido en caso de que nos encontemos en la situación de que el usuario haya hecho un pedido (recordemos que es posible que venga del activity ListaPedidosActivity, que en ese caos, no se realiza esta función)
     */
    private void realizarPedido(){
        //Le pasamos los parámetros al URL de crear pedidos
        urlCrearPedido += "&apikey=" + InicioActivity.mPreferences.getString("apikey", "")+ "&shopid=" + shopId;
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlCrearPedido,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int orderId;
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            orderId = leerJSONPedido(utf8String);
                            realizarLineaCompra(orderId); //Si Volley lee correctamente el URL, se realizará el pedido
                        } catch (UnsupportedEncodingException e) {
                            Log.e("Error", "Error al parsear a UTF-8: " + e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { //Si ocurre un error le indicamos que ha ocurrido un error e la red.
                Toast.makeText(DetallePedidoActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
                Intent intentMenuActivity = new Intent(DetallePedidoActivity.this, MenuActivity.class);
                startActivity(intentMenuActivity);
            }

        });

        queue.add(stringRequest);
    }

    /**
     * Una vez realizado el pedido, se realizará la linea de compra
     * @param orderId
     */
    private void realizarLineaCompra(int orderId){
        //Se le pasa los parámetros al URL de la linea de compra
        urlLineaCompra += "&apikey=" + InicioActivity.mPreferences.getString("apikey", "")+ "&orderid=" + orderId + "&productsid=";
        int contador=0;
        for(Producto producto: ListaProductosActivity.numeroProductos){ //Por cada producto que hay en la lista, lo añadimos al URL con | en cada producto
            if(contador!=0){
                urlLineaCompra+="|";
            }
            urlLineaCompra += producto.getProductoId();
            contador++;
        }
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlLineaCompra,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int orderId;
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            leerJsonLineaCompra(utf8String); //Lee el JSON de la linea compra

                        } catch (UnsupportedEncodingException e) {
                            Log.e("Error", "Error al parsear a UTF-8: " + e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetallePedidoActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
                Intent intentMenuActivity = new Intent(DetallePedidoActivity.this, MenuActivity.class);
                startActivity(intentMenuActivity);
            }

        });

        queue.add(stringRequest);
    }

    /**
     * Lee el JSON al realizar la linea de compra
     * @param response
     */
    private void leerJsonLineaCompra(String response){
        try {
            JSONObject jsonObject = new JSONObject(response); //Obtiene la respuesta y lo pasa a JSON
            JSONObject responseObject = jsonObject.getJSONObject("response"); //Obtenemos el response para luego obtener el status y value
            String estado = responseObject.getString("status");
            if(estado.equals("OK")){ //Si el status es OK se borra toda la lista y se pasa al menuActivity
                ListaProductosActivity.numeroProductos.clear();
                Toast.makeText(DetallePedidoActivity.this, "Se ha realizado correctamente el pedido", Toast.LENGTH_SHORT).show();
                Intent menuActivity = new Intent(DetallePedidoActivity.this, MenuActivity.class);
                startActivity(menuActivity);
                finishAffinity(); //Además, borramos de la pila de activity el DetallePedidoActivity, para que cuando le de para atras no vuelva al DetallePedidoActivity
            }else{
                Toast.makeText(DetallePedidoActivity.this, "No se ha podido realizar el pedido. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lee el JSON del pedido para posteriormente poder realizar la linea de compra
     * @param response
     * @return Devuelve el order id
     */
    private int leerJSONPedido(String response){
        int orderId=0;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            String estado = responseObject.getString("status");
            if(estado.equals("OK")){ //Si el status es OK, devuelve el orderId
                orderId = responseObject.getInt("value");
            }else{
                Toast.makeText(DetallePedidoActivity.this, "No se ha podido realizar el pedido. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderId;
    }

    /**
     * Eliminar el pedido si no está servido
     */
    private void eliminarPedido(){
        //Le pasamos los parámetros a la url de eliminar el pedido
        urlEliminarPedido += "apikey=" + InicioActivity.mPreferences.getString("apikey", "")+ "&orderid=" + pedido.getPedidoId();
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlEliminarPedido,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("XXXX", "AAQUI ENTRO");
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            procesaJSONYEliminaPedido(utf8String); //Se procesa el JSON de eliminar pedido
                        } catch (UnsupportedEncodingException e) {
                            Log.e("Error", "Error al parsear a UTF-8: " + e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetallePedidoActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
                Intent intentMenuActivity = new Intent(DetallePedidoActivity.this, MenuActivity.class);
                startActivity(intentMenuActivity);
            }

        });

        queue.add(stringRequest);
    }

    /**
     * Procesa el JSON al eliminar el pedido
     * @param response
     */
    private void procesaJSONYEliminaPedido(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseJson = jsonObject.getJSONObject("response");
            String status = (String) responseJson.get("status");
            if(status.equals("OK")){ //Si el status es OK, quiere decir que el pedido se ha eliminado correctamente y enviamos al usuario al MenuActivity
                Intent menuActivity = new Intent(DetallePedidoActivity.this, MenuActivity.class);
                startActivity(menuActivity);
                Toast.makeText(this, "Se ha borrado el pedido correctamente", Toast.LENGTH_SHORT).show();
                finishAffinity(); //Se elimina toda la pila de actividades, para que si el usuario vuelve atras, no pueda eliminar otra vez el pedido
            }
        } catch (JSONException e) {
            Intent intentMenuActivity = new Intent(DetallePedidoActivity.this, MenuActivity.class);
            startActivity(intentMenuActivity);
            Toast.makeText(this, "Ha ocurrido un error al intentar borrar el pedido. Vuelve a intentarlo", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Carga y muestra la lista de productos en pantalla, poniendole un adapter al recyclerView
     */
    private void cargaYMuestraListaProductos() {
        //Le pongo los parámetros necesarios al url de leer productos
        urlLeerProductos += "apikey=" + InicioActivity.mPreferences.getString("apikey", "")+ "&orderid=" + pedido.getPedidoId();
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlLeerProductos,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            procesaJSONyCargaLista(utf8String);

                            listAdapterProducto = new ListAdapterProducto(listaProductos,DetallePedidoActivity.this);
                            recyclerView.setAdapter(listAdapterProducto); //Le pongo el adapter del producto

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
                Toast.makeText(DetallePedidoActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
                Intent intentMenuActivity = new Intent(DetallePedidoActivity.this, MenuActivity.class);
                startActivity(intentMenuActivity);
            }

        });

        queue.add(stringRequest);
    }

    /**
     * Procesamos el JSON y vamos añadiendo a la lista los productos que se van leyendo en el JSON
     * @param response
     */
    private void procesaJSONyCargaLista(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseJson = jsonObject.getJSONObject("response");
            String status = (String) responseJson.get("status");

            if (status.equals("OK")) { //Si el status es OK, cogemos los productos
                JSONArray productsArray = responseJson.getJSONArray("products");

                for (int i = 0; i < productsArray.length(); i++) { //Hacemos un for para recoger cada producto que haya en el JSON
                    JSONObject productObject = productsArray.getJSONObject(i);
                    int idProducto = productObject.getInt("idproduct");
                    String nombreProducto = productObject.getString("name");
                    String nombreCategoria = productObject.getString("category");
                    //Si por casualidad el fileUrl no exista, ponemos que sea null, para que el producto se muestre, aunque sea con una imagen por defecto
                    String file = productObject.isNull("fileurl") ? null : productObject.getString("fileurl");
                    //Una vez recogido los datos, creamos un objeto producto y lo añadimos a la lista de productos
                    Producto producto = new Producto(idProducto, nombreProducto, nombreCategoria, file);
                    this.listaProductos.add(producto);
                }
            }else{
                Toast.makeText(this, "No hay pedidos que mostrar", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Ha ocurrido un error al obtener los datos del pedido", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Nuestro mapa será el mapa generado por Google
        mMap = googleMap;

        // Configurar tipo de mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Añadir varios marcadores
        addMarkers();

        // Añadir marcador con la posición del usuario.
        addUserPositionMarker();

        // Habilitar clics en los marcadores
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Acción al seleccionar un marcador
                marker.showInfoWindow();
                if(!getIntent().getExtras().getBoolean("activityPedidos")){
                    //Obtenemos el id de la tienda para comprobar que el usuario ha seleccionado una tienda
                    shopId = marcadores.get(marker);
                }
                return false; // Si es false, mantiene el comportamiento predeterminado
            }
        });
    }

    private void addMarkers() {
        if(getIntent().getExtras().getBoolean("activityPedidos")){
            leerLongitudLatitudTienda();
        }else{
            leerTodasTiendas();
        }
    }

    /**
     * Leemos todas las tiendas para mostrarlos en el mapa
     */
    private void leerTodasTiendas(){
        urlLeerTodasTiendas += "apikey=" + InicioActivity.mPreferences.getString("apikey", "");
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlLeerTodasTiendas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            procesarTodasTiendasJSON(utf8String);
                        } catch (UnsupportedEncodingException e) {
                            Log.e("Error", "Error al parsear a UTF-8: " + e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetallePedidoActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
            }

        });

        queue.add(stringRequest);
    }

    /**
     * Procesamos las tiendas disponibles en la base de datos y lo añadimos como marcadores en el mapa
     * @param response
     */
    private void procesarTodasTiendasJSON(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseJSON = jsonObject.getJSONObject("response");
            JSONArray shops = responseJSON.getJSONArray("shops");

            for (int i = 0; i < shops.length(); i++) {
                JSONObject shopObject = shops.getJSONObject(i);
                int shopid = shopObject.getInt("shopid");
                String tituloTienda = shopObject.getString("description");
                double latitud = shopObject.getDouble("latitude");
                double longitud = shopObject.getDouble("longitude");
                LatLng posicionTienda = new LatLng(latitud, longitud);
                //Añadimos el marcador en el mapa y lo guardamos en un diccionario para luego obtener el id de la tienda y comprobar que ha seleccionado una tienda
                Marker marker = mMap.addMarker(new MarkerOptions().position(posicionTienda).title(tituloTienda));
                marcadores.put(marker,shopid);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Ha ocurrido un error al obtener los datos del mapa", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Lee la longitud y latitud de una tienda especifica, para mostrarlo en el mapa
     */
    private void leerLongitudLatitudTienda(){
        leerTienda += "apikey=" + InicioActivity.mPreferences.getString("apikey", "") + "&shopid=" + pedido.getTiendaAsignadaId();
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se prepara la petición con la URL del servicio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, leerTienda,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Obtener el String como ISO y parsear a UTF-8
                        try {
                            byte[] bytes = response.getBytes("ISO-8859-1");
                            String utf8String = new String(bytes, "UTF-8");
                            procesaJSONTienda(utf8String);
                            LatLng posicionTienda = new LatLng(latitudTienda, longitudTienda);
                            mMap.addMarker(new MarkerOptions().position(posicionTienda).title(pedido.getTiendaAsignada()));
                        } catch (UnsupportedEncodingException e) {
                            Log.e("Error", "Error al parsear a UTF-8: " + e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetallePedidoActivity.this, "Ha ocurrido un error en la red. Vuelve a intentarlo", Toast.LENGTH_LONG).show();
            }

        });

        queue.add(stringRequest);
    }

    /**
     * Se procesa el JSON de la tienda leída, obteniendo su latitud y longitud
     * @param response
     */
    private void procesaJSONTienda(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");

            if ("OK".equals(status)) {
                String latitud = jsonObject.getString("latitude");
                String longitud = jsonObject.getString("longitude");
                latitudTienda = Double.parseDouble(latitud);
                longitudTienda = Double.parseDouble(longitud);
            }else{
                Toast.makeText(this, "Ha ocurrido un error al obtener la localización de la tienda.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.d("XXXX", e.toString());
            Toast.makeText(this, "Ha ocurrido un error al obtener la localización de la tienda.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addUserPositionMarker(){
        // Solicitud de permisos en tiempo de ejecución
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        // Listener de actualización de posición: cada 2 segundos
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        // Gestión de la respuesta del servicio de ubicación.
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // Si todo va correctamente, posicionamos el avatar sobre nuestra posición en el mapa
                        // y hacemos un zoom medio.
                        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(userLatLng)
                                .title("¡Aquí estás!")
                                .icon( bitmapDescriptorFromVector(DetallePedidoActivity.this, R.drawable.avatar_usuario) ));

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 8));
                        fusedLocationClient.removeLocationUpdates(this); // Detener actualizaciones para ahorrar batería
                        break;
                    }
                }
            }
        };

        // Se establece la petición de ubicación, la gestión de la respuesta y se define la ejecución automática.
        // fusedLocationClient → Es una instancia de FusedLocationProviderClient, que es la API de Google para obtener ubicaciones de forma eficiente.
        // requestLocationUpdates(...) → Es el método que solicita actualizaciones de ubicación basadas en los parámetros que le pasamos.
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Gestión de la respuesta de los permisos solicitados en tiempo de ejecución.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Verifica si la respuesta es para el permiso de ubicación..
        if (requestCode == LOCATION_REQUEST_CODE) {
            // Comprueba si el permiso fue concedido.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el permiso fue concedido, añade el marcador del usuario.
                addUserPositionMarker();
            } else {
                // Si el permiso fue denegado, muestra un mensaje informativo al usuario.
                Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Este método transforma un .png en un bitmap
     *
     * @param context
     * @param vectorResId
     * @return
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (vectorDrawable == null) {
            return null;
        }

        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth()/3, vectorDrawable.getIntrinsicHeight()/3);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth()/3, vectorDrawable.getIntrinsicHeight()/3, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}