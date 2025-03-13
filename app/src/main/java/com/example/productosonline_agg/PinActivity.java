package com.example.productosonline_agg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class PinActivity extends AppCompatActivity {

    private EditText pinEditText;

    private TextView tituloPin;

    private Button buttonPin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Le mostramos un texto de bienvenida al usuario
        tituloPin = (TextView) findViewById(R.id.tituloPin);
        String textoBienvenida = tituloPin.getText().toString() + ", " + InicioActivity.mPreferences.getString("usuario", "");
        tituloPin.setText(textoBienvenida);

        buttonPin = (Button) findViewById(R.id.enviarPin);
        buttonPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinEditText = (TextInputEditText) findViewById(R.id.pin);
                String pinString = pinEditText.getText().toString();
                if(pinString.length()<4){ //Cuando el usuario le de clic al botón para guardar el PIN,  comprobamos que tenga 4 dígitos
                    pinEditText.setError("El PIN tiene que ser de 4 dígitos");
                    pinEditText.requestFocus();
                }else{ //Si el PIN está correcto, comprobamos si es la primera vez que accede o si ya hay datos guardados en la aplicación
                    SharedPreferences.Editor preferencesEditor =
                            InicioActivity.mPreferences.edit();
                    int pinEntero = Integer.parseInt(pinString);
                    if(InicioActivity.mPreferences.getInt("pin", 0)!=0){ //Si no es la primera vez que accedes a la aplicación, se lo preguntamos solo una vez
                        if(pinEntero==InicioActivity.mPreferences.getInt("pin", 0)){
                            Intent mostrarMenuIntent = new Intent(PinActivity.this, MenuActivity.class);
                            startActivity(mostrarMenuIntent);
                        }else{ //Si hay algun dato incorrecto, se lo indicamos al usuario
                            pinEditText.setError("No coinciden los valores. Vuelve a intentarlo.");
                            pinEditText.requestFocus();
                        }
                    }else{ //Y si es la primera vez que accede, se lo preguntamos de nuevo
                        preferencesEditor.putInt("pin", pinEntero);
                        preferencesEditor.apply();
                        pinEditText.setText("");
                        Toast.makeText(PinActivity.this, "Vuelve a introducir el PIN", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });
    }
}