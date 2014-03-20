package com.example.client.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.client.R;
import com.example.client.model.Pizza;
import java.util.ArrayList;


public class Adapter extends BaseAdapter {
    private Context context;
    private ArrayList<Pizza> listOfPizza = null;

    public Adapter(Context context, ArrayList<Pizza> objects){
       this.context = context;
       this.listOfPizza = new ArrayList<Pizza>(objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rValue = convertView;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            rValue = inflater.inflate(R.layout.list_item, parent, false);
        }

        Pizza pizza = listOfPizza.get(position);

        TextView text = (TextView) rValue.findViewById(R.id.textViewName);
        text.setText(pizza.getName());
        text = (TextView) rValue.findViewById(R.id.textViewPrice);
        text.setText(Double.toString(pizza.getPrice()));
        return rValue;
    }

    @Override
    public int getCount() {
        return listOfPizza.size();
    }

    @Override
    public Object getItem(int pozition) {
        return listOfPizza.get(pozition);
    }

    @Override
    public long getItemId(int pozition) {
        return pozition;
    }

    public void add(final Pizza pizza){
        listOfPizza.add(pizza);
    }

    public ArrayList<Pizza> getDataSet(){
        return listOfPizza;
    }
}
