package com.example.towaquiz.viewcontrollers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.towaquiz.delegates.DBManager;
import com.example.towaquiz.R;
import com.example.towaquiz.delegates.AlertDialog;
import com.example.towaquiz.server.RESTService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ResponseForm extends AppCompatActivity {

    ListView list;
    Intent intent;
    ArrayList<String> elements;
    JSONObject object;
    JSONArray array;
    String url;
    String params;

    private DBManager dbManager;

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            ResponseForm form = (ResponseForm) parent.getContext();

            String target = String.valueOf( ((TextView)v).getText() );

            Log.i("target", target);
            if(target.contains("Array") || target.contains("data")) {
                try {
                    String name = form.object.names().getString(position);
                    form.populateView(form.object.get(name).toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public ResponseForm(){
        this.elements = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.response_view);
        //get previous view
        this.intent = getIntent();
        this.list = findViewById(R.id.param_list);
        this.list.setOnItemClickListener(messageClickedHandler);
        String response = this.fetch(this.intent.getStringExtra(Intent.EXTRA_TEXT));

        if(response.contains("404")){
            AlertDialog.showDialog("the resource does not exist", "404 not found", this);
        }else if(response.contains("500")){
            AlertDialog.showDialog("an internal server error occurred", "server error", this);
        }else{
            this.populateView(response);
            this.dbManager = new DBManager(this);
            this.dbManager.open();

            this.dbManager.insert(this.url, this.params);

            this.dbManager.close();
        }
    }

    private String fetch(String input){
        String[] extra = input.split(";");
        this.url = extra[0];
        this.params = extra.length > 2 ? extra[2] : "";
        String response = "";
        try {
            response = new RESTService().execute(
                extra[0], extra[1], this.params
            ).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
            AlertDialog.showDialog("network error", "error", this);
        } catch (ExecutionException e) {
            e.printStackTrace();
            AlertDialog.showDialog("Execution error", e.getMessage(), this);
        }
        return response;
    }


    private void populateView(String content) {

        this.elements = this.filterData(content);

        this.list.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.param_view,
                this.elements)
        );
    }

    private ArrayList<String> filterData(String content){
        ArrayList<String> result = new ArrayList<>();
        Object json;
        Log.i("content", content);

        try {
            JSONTokener tokenizer = new JSONTokener(content);

            //map array to arrayList
            while(tokenizer.more()){

                json = tokenizer.nextValue();

                if(json instanceof JSONObject){
                    JSONArray array;
                    Object attr;
                    String attrName;

                    this.object = (JSONObject)json;
                    array = object.names();

                    for(int i = 0; i < array.length(); i++){
                        attrName = array.getString(i);
                        attr = this.object.get(attrName);
                        if(attr instanceof JSONArray){
                            result.add(attrName + " = Array");
                        }else {
                            result.add(attrName + " = " + attr.toString());
                        }
                    }

                }else if(json instanceof JSONArray){
                    this.array = (JSONArray)json;
                    Object elem;

                    for(int i = 0; i < this.array.length(); i++){
                        elem = this.array.get(i);
                        if(elem instanceof JSONObject){
                            result.add("param" + i + " = Object");
                        }else {
                            result.add("param" + i + " = " + elem);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
