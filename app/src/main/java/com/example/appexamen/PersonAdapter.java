package com.example.appexamen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PersonAdapter extends ArrayAdapter<Person> {

    private Context context;
    private List<Person> persons;

    public PersonAdapter(Context context, List<Person> persons) {
        super(context, R.layout.activity_person, persons);
        this.context = context;
        this.persons = persons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_person, null, false);

        ImageView imageView = view.findViewById(R.id.imageview);
        TextView nombreTextView = view.findViewById(R.id.nombre);
        TextView telefonoTextView = view.findViewById(R.id.telefono);

        // Obtener la persona en la posición actual
        Person person = persons.get(position);

        // Cargar la imagen desde la URL usando Glide
        Glide.with(context).load(person.getImage()).into(imageView);

        // Mostrar el nombre y el teléfono
        nombreTextView.setText(person.getName());
        telefonoTextView.setText(person.getPhone());

        return view;
    }
}
