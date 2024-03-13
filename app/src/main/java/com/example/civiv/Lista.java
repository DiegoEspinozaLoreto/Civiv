package com.example.civiv;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Lista extends AppCompatActivity {

    private ListView list_view;

    private ArrayList names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        list_view = (ListView) findViewById(R.id.listView);

        names = new ArrayList<String>();
        names.add("Veracruz");
        names.add("Tijuana");
        names.add("Jalisco");
        names.add("Monterrey");
        names.add("Tabasco");
        names.add("maTaulipas");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        list_view.setAdapter(adapter);

    }
}
