package com.example.client;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.client.constants.Constants;
import com.example.client.database.PizzashopDatabase;
import com.example.client.model.Pizza;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MyActivity extends Activity implements OnClickListener {

    private PizzashopDatabase dbPizzashop = null;
    private ArrayList<Pizza> listOfPizza = null;

    Pizza pizza;

    private TextView textTitle = null;

    private EditText editName = null;
    private EditText editPrice = null;
    private EditText editID = null;

    private Button buttonPost = null;
    private Button buttonGet = null;
    private Button buttonSeeList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textTitle = (TextView) findViewById(R.id.textTitle);

        editName = (EditText) findViewById(R.id.editName);
        editPrice = (EditText) findViewById(R.id.editPrice);
        editID = (EditText) findViewById(R.id.editID);

        buttonPost = (Button) findViewById(R.id.buttonPost);
        buttonGet = (Button) findViewById(R.id.buttonGet);
        buttonSeeList = (Button) findViewById(R.id.buttonSeeList);

        dbPizzashop = new PizzashopDatabase(getApplicationContext());
        listOfPizza = new ArrayList<Pizza>();

        if(isConnected()){
            textTitle.setBackgroundColor(0xFF00CC00);
            textTitle.setText("You are connected! :)");
        }
        else{
            textTitle.setText("You are NOT connected! :(");
        }

        buttonGet.setOnClickListener(this);
        buttonPost.setOnClickListener(this);
        buttonSeeList.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonPost:
                if(!validatePost()){
                    Toast.makeText(getBaseContext(), "Enter valid data!", Toast.LENGTH_LONG).show();
                }
                else {
                    new HttpAsyncTask().execute("http://10.0.2.2:8080/pizza", "POST");
                }
                break;
            case R.id.buttonGet:
                if(!validateGet()){
                    Toast.makeText(getBaseContext(), "Enter valid data!", Toast.LENGTH_LONG).show();
                }
                else{
                    HttpAsyncTask task = new HttpAsyncTask();
                    task.execute("http://10.0.2.2:8080/pizza", "GET");
                }
                break;
            case R.id.buttonSeeList:
                Intent startIntent = new Intent(getApplicationContext(), ListOfPizza.class);
                startActivity(startIntent);
                break;
        }
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

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... param) {

            if(param[1].equals("POST")){
                pizza = new Pizza();
                pizza.setName(editName.getText().toString());
                pizza.setPrice(Double.valueOf(editPrice.getText().toString()));
                return POST(param[0],pizza);
            }
            else if(param[1].equals("GET")){
                return GET(param[0] + "/" + editID.getText().toString());
            }
            else
                return null;
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getBaseContext(), "Get answer:" + result, 400).show();

            if(result!= null){
                ArrayList<Pizza> list = parseData(result);
                if(list != null)
                {
                    dbPizzashop.addDataToPizza(list);
                    dbPizzashop.getValidDataFromPizza(listOfPizza);
                }
            }
        }

        private Pizza parseObject(String json){

            try{
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(json);

                JSONObject jsonObject = (JSONObject) obj;

                Pizza temp = new Pizza();
                temp.setName(jsonObject.get("name").toString());
                temp.setPrice(Double.valueOf(jsonObject.get("price").toString()));
                return temp;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<Pizza> parseData(String json){
            ArrayList<Pizza> list = new ArrayList<Pizza>();
            try{
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(json);

                JSONObject jsonObject = (JSONObject) obj;
                try{
                    JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                    for(int i=0; i<jsonArray.size(); i++){
                        Pizza temp = new Pizza();
                        JSONObject object = (JSONObject) jsonArray.get(i);

                        temp.setName(object.get("name").toString());
                        temp.setPrice(Double.valueOf(object.get("price").toString()));
                        list.add(temp);
                    }
                    return list;
                }
                catch (Exception e){
                    list.add(parseObject(json));
                    return list;
                }
            }
            catch (Exception e){
                return null;
            }
        }
    }

    private boolean validatePost(){
        try{
            Double.parseDouble(editPrice.getText().toString());
        }
        catch(Exception e)
        {
            return false;
        }
        if(editName.getText().toString().trim().equals(""))
            return false;
        else if(editPrice.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    private boolean validateGet(){
        if(editID.getText().toString().trim().equals("all")){
            return true;
        }
        try{
            Integer.parseInt(editID.getText().toString());
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    private static String convertInputStreamToString(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try{
            while((line = reader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                inputStream.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public static String POST(String url, Pizza pizza){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", pizza.getName());
            jsonObject.put("price", pizza.getPrice());
            json = jsonObject.toString();

            StringEntity stringEntity = new StringEntity(json);
            httpPost.setEntity(stringEntity);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

}
