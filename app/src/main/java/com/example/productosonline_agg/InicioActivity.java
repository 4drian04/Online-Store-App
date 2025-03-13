package com.example.productosonline_agg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class InicioActivity extends AppCompatActivity {


    protected static SharedPreferences mPreferences;
    protected static String sharedPrefFile =
            "com.example.productosonline_agg";

    private Button buttonIniciaSesion;

    private Button buttonRegistrar;

    private LinearLayout menu;
    private LinearLayout progressBar;

    private TextInputEditText nombreUsuarioEditText;
    private TextInputEditText contrasenhaEditText;
    private String nombreUsuario;
    private String contrasenha;

    //Nos permite ver si los campos de texto están correctos
    private boolean esCorrecto=true;

    //Url para leer un usuario
    private String urlReadUser = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/readUserapp.php?type=JSON";
    //Url para crear un usuario
    private String urlCreateUser = "http://iescristobaldemonroy.duckdns.org:81/USER83/OnlineStoreProject-AdrianGarciaGarcia/services/createUserapp.php?type=JSON";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.inicioactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menu = (LinearLayout) findViewById(R.id.menu);
        progressBar = (LinearLayout) findViewById(R.id.pantallaCarga);
        //Creamos un shared preferences para guardar algunos datos necesarios
        mPreferences = getSharedPreferences(sharedPrefFile,
                        MODE_PRIVATE);
        //borrarDatos();
        //Si hay datos guardados del usuario, lo enviamos directamente al PinActivity
        if(!mPreferences.getString("apikey", "null").equals("null")){
            enviaraPinActivity();
        }
        buttonIniciaSesion = (Button) findViewById(R.id.login_button);
        buttonIniciaSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esCorrecto=true;
                obtenerBotonesYComprobarErrores(); //Si le da a iniciar sesión, comprobamos que estén los campos correctamente
                if(esCorrecto){ //Si están los campos correctos, leemos el usuario, ya que ha iniciado sesión
                    urlReadUser += "&user=" + nombreUsuario + "&password=" + contrasenha; //Ponemos los parámetros al URL
                    //Escondemos el menu y mostramos el progressbar cuando el usuario le de a iniciar sesión o registrarse
                    menu.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    RequestQueue queue = Volley.newRequestQueue(InicioActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, urlReadUser,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) { //Si se puede conectar correctamente al link, leemos el JSON de inicio de sesión
                                    String response2 = null;
                                    try { //Pasamos el string a un charset para que no haya problemas con las tildes.
                                        response2 = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                                        leerORegistrarUserJSON(response2); //Leemos el JSON
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            menu.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(InicioActivity.this, "Compruebe que tienes conexión a internet. vuelve a intentarlo", Toast.LENGTH_LONG).show();
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        });

        buttonRegistrar = (Button) findViewById(R.id.register_button);
        buttonRegistrar.setOnClickListener(new View.OnClickListener() { //Hacemos algo parecido en el botón de registrar
            @Override
            public void onClick(View v) {
                esCorrecto=true;
                obtenerBotonesYComprobarErrores(); //Comprobamos que los campos estén correctamente introducidos
                if(esCorrecto){
                    urlCreateUser += "&user=" + nombreUsuario + "&password=" + contrasenha;
                    //Escondemos el menu y mostramos el progressbar cuando el usuario le de a iniciar sesión o registrarse
                    menu.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    RequestQueue queue = Volley.newRequestQueue(InicioActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, urlCreateUser,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    String response2 = null;
                                    try { //Pasamos el string a un charset para que no haya problemas con las tildes.
                                        response2 = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                                        leerORegistrarUserJSON(response2); //Leemos el JSON al crear el usuario
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            menu.setVisibility(View.VISIBLE);
                            Toast.makeText(InicioActivity.this, "Compruebe que tienes conexión a internet. vuelve a intentarlo", Toast.LENGTH_LONG).show();
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        });
    }
    private void obtenerBotonesYComprobarErrores(){
        //Obtenemos los campos de inicio o registrarse
        nombreUsuarioEditText = (TextInputEditText) findViewById(R.id.username);
        contrasenhaEditText = (TextInputEditText) findViewById(R.id.password);
        nombreUsuario = nombreUsuarioEditText.getText().toString();
        contrasenha = contrasenhaEditText.getText().toString();
        if(nombreUsuario.isEmpty()){ //Si alguno de los dos campos están vacíos, mostramos un error
            esCorrecto=false;
            nombreUsuarioEditText.setError("No has introducido ningún usuario. Debes de introducir un nombre de usuario");
            nombreUsuarioEditText.requestFocus();
        }else{
            if(nombreUsuario.length()<3){ //Y si alguno de los dos tienen una longitud menor de 3, también se mostrará un error
                esCorrecto=false;
                nombreUsuarioEditText.setError("El nombre de usuario debe de tener como mínimo tres carácteres");
                nombreUsuarioEditText.requestFocus();
            }
        }
        if(contrasenha.isEmpty()){
            esCorrecto=false;
            contrasenhaEditText.setError("No has introducido ninguna contraseña. Debes de introducir una contraseña");
            contrasenhaEditText.requestFocus();
        }else{
            if(contrasenha.length()<3){
                esCorrecto=false;
                contrasenhaEditText.setError("La contraseña debe de tener como mínimo tres carácteres");
                contrasenhaEditText.requestFocus();
            }
        }
    }

    /**
     * Leemos el JSON tanto si se registra como si inicia sesión
     * @param response
     */
    private void leerORegistrarUserJSON(String response){
        try {
            JSONObject jsonOBject = new JSONObject(response);
            JSONObject responseJson = jsonOBject.getJSONObject("response"); //Obtenemos el response del JSON
            String status = (String) responseJson.get("status"); //Obtenemos el status
            String value = (String) responseJson.get("value"); //Obtenemos el valor
            if(status.equals("OK")){ //Si el status es ok, guardamos los datos del usuario y lo enviamos al activity del PIN
                guardarDatosUsuario(value);
                enviaraPinActivity();
            }else{ //Si el status no es OK, le mostramos el error en pantalla
                progressBar.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                nombreUsuarioEditText.setError(value);
                nombreUsuarioEditText.requestFocus();
            }
        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
            menu.setVisibility(View.VISIBLE);
            Toast.makeText(InicioActivity.this, "Ha ocurrido un error inesperado. Vuelve a intentarlo más tarde", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Guardamos los datos del usuario en el shared preferences
     * @param apikey
     */
    private void guardarDatosUsuario(String apikey){
        SharedPreferences.Editor preferencesEditor =
                mPreferences.edit();
        preferencesEditor.putString("apikey", apikey);
        preferencesEditor.putString("usuario", nombreUsuario);
        preferencesEditor.putString("contrasenha", contrasenha);
        preferencesEditor.apply();
    }

    private void borrarDatos(){
        SharedPreferences.Editor preferencesEditor =
                mPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }

    /**
     * En caso de que todo esté correcto, enviamos al usuario al PinActivity
     */
    private void enviaraPinActivity(){
        Intent intentPIN = new Intent(InicioActivity.this, PinActivity.class);
        startActivity(intentPIN);
        finish();
    }
}