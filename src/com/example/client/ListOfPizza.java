package com.example.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.example.client.database.PizzashopDatabase;
import com.example.client.model.Pizza;
import com.example.client.utils.Adapter;
import java.util.ArrayList;


public class ListOfPizza extends Activity {

    private PizzashopDatabase dbPizzashop = null;
    private ArrayList<Pizza> listOfPizza = null;

    private Adapter adapter = null;
    private ListView listView = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_pizza);

        listView = (ListView) findViewById(R.id.listView);

        dbPizzashop = new PizzashopDatabase(getApplicationContext());

        listOfPizza = new ArrayList<Pizza>();
        dbPizzashop.getValidDataFromPizza(listOfPizza);
        adapter = new Adapter(this, listOfPizza);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbPizzashop.getValidDataFromPizza(listOfPizza);
    }

    @Override
    protected void onDestroy() {
        dbPizzashop.close();
    }
}

