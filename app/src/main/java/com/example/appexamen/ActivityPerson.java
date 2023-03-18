package com.example.appexamen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ActivityPerson extends AppCompatActivity {

    private ListView listView;
    private List<Person> personList;
    private PersonAdapter personAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        listView = findViewById(R.id.list_view);

        personList = new ArrayList<>();
        personAdapter = new PersonAdapter(this, personList);
        listView.setAdapter(personAdapter);

// Hacer una solicitud HTTP para obtener los datos del servidor
        String url = "http://192.168.40.1/examapp/viewPersons.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Verificar si la respuesta contiene el array "persons" del JSON
                            if (response.has("persons")) {
                                // Obtener el array "persons" del JSON
                                JSONArray jsonArray = response.getJSONArray("persons");

                                // Recorrer cada objeto en el array y agregarlo a la lista de personas
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String name = jsonObject.getString("name");
                                    String phone = jsonObject.getString("phone");
                                    String image = jsonObject.getString("image");
                                    double latitude = jsonObject.getDouble("latitud");
                                    double longitude = jsonObject.getDouble("longitud");

                                    // Decodificar la imagen Base64 a un array de bytes
                                    byte[] decodedImage = Base64.decode(image, Base64.DEFAULT);

                                    // Crear un objeto Person y agregarlo a la lista
                                    Person person = new Person(name, phone, decodedImage, latitude, longitude);
                                    personList.add(person);
                                }

                                // Notificar al adaptador que se ha actualizado la lista de personas
                                personAdapter.notifyDataSetChanged();
                            } else {
                                throw new JSONException("La respuesta no contiene el array 'persons'.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Manejar errores de analisis JSON
                            Toast.makeText(ActivityPerson.this, "Hubo un problema con la respuesta del servidor."+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // Manejar errores de solicitud HTTP
                        Toast.makeText(ActivityPerson.this, "Hubo un problema con la solicitud al servidor."+error, Toast.LENGTH_SHORT).show();
                    }
                });

// Agregar la solicitud HTTP a la cola de solicitudes
        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }
}