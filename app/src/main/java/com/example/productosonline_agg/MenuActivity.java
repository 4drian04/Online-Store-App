package com.example.productosonline_agg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {

    private Button listaPedidos;

    private Button realizarPedidos;

    private Button cerrarSesion;

    private TextView tituloMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activityMenu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tituloMenu = (TextView) findViewById(R.id.tituloMenu);
        tituloMenu.setText(tituloMenu.getText().toString() + ", " + InicioActivity.mPreferences.getString("usuario", "usuario"));
        //Boton que nos llevará a la lista de pedidos
        listaPedidos = (Button) findViewById(R.id.listarPedidosButton);
        listaPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor preferencesEditor =
                        InicioActivity.mPreferences.edit();
                //Este boolean nos va a permitir si el producto es seleccionable (si el item es clickable) o no
                preferencesEditor.putBoolean("productosSeleccionable", false);
                preferencesEditor.apply();
                Intent listarPedidoIntent = new Intent(MenuActivity.this, ListaPedidosActivity.class);
                startActivity(listarPedidoIntent);
            }
        });

        //Botón que nos llevará a la ListaCategoriaActivity
        realizarPedidos = (Button) findViewById(R.id.realizarPedidosButton);
        realizarPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listarCategoriasIntent = new Intent(MenuActivity.this, ListaCategoriasActivity.class);
                startActivity(listarCategoriasIntent);
            }
        });

        //Este botón nos eliminará los datos de la aplicación y nos llevará a la primera activity
        cerrarSesion = (Button) findViewById(R.id.cerrarSesionButton);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarDatos();
                Intent inicioActivityIntent = new Intent(MenuActivity.this, InicioActivity.class);
                startActivity(inicioActivityIntent);
                finishAffinity();
            }
        });
    }

    /**
     * Este método nos permite borrar los datos de la aplicación
     */
    private void borrarDatos(){
        SharedPreferences.Editor preferencesEditor =
                InicioActivity.mPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }
}