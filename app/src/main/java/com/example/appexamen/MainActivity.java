package com.example.appexamen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    Button btnCamara, btnGuardar, btnListaPersonas;
    ImageView imagen;
    TextView txtNombre, txtTelefono;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        btnCamara = findViewById(R.id.btnCamara);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnListaPersonas = findViewById(R.id.btnListaPersonas);
        imagen = findViewById(R.id.imageView2);

        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNombre.requestFocus();

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamara();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = txtNombre.getText().toString();
                String telefono = txtTelefono.getText().toString();
                //     String geolocalizacion = obtenerGeolocalizacion();
                // Enviar la imagen y los datos al servidor
                uploadData();
            }
        });

        btnListaPersonas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityPerson.class);
                startActivity(intent);
            }
        });

    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagen.setImageBitmap(imageBitmap);
        }
    }
    private void uploadData() {
        // Obtener los datos del formulario
        String nombre = txtNombre.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String imagenBase64 = getImagenBase64();
        double latitud = 1.2;  // dejar espacio para obtener latitud getLatitud()
        double longitud = 2.3;// dejar espacio para obtener longitud    getLongitud()

        // Realizar la solicitud al servidor
        String url = "http://192.168.40.1/examapp/Persons.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Procesar la respuesta del servidor
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int success = jsonResponse.getInt("success");
                            String message = jsonResponse.getString("message");
                            if (success == 1) {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                // Reiniciar el formulario
                                txtNombre.setText("");
                                txtTelefono.setText("");

                            } else {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error al conectarse al servidor"+error, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("telefono", telefono);
                params.put("base64", imagenBase64);
                params.put("latitud", Double.toString(latitud));
                params.put("longitud", Double.toString(longitud));
                return params;
            }
        };

        // Agregar la solicitud a la cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String getImagenBase64() {
        // Obtener la imagen del ImageView como Bitmap
        Bitmap bitmap = ((BitmapDrawable) imagen.getDrawable()).getBitmap();
        // Convertir el Bitmap a Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imagenBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imagenBase64;
    }
}