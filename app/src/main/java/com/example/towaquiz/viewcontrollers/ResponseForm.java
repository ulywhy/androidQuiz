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

import com.example.towaquiz.delegates.CustomRequest;
import com.example.towaquiz.delegates.CustomResponse;
import com.example.towaquiz.delegates.DBManager;
import com.example.towaquiz.R;
import com.example.towaquiz.delegates.utils.AlertDialog;
import com.example.towaquiz.server.RESTService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ResponseForm extends AppCompatActivity {

    ListView list;
    Intent intent;
    ArrayList<String> elements;

    private CustomResponse response;

    private CustomRequest request;

    private DBManager dbManager;

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            ResponseForm form = (ResponseForm) parent.getContext();

            String target = String.valueOf( ((TextView)v).getText() );

            Log.i("target", target);
            if(target.contains("Array") || target.contains("data")) {
                try {
                    String name = form.response.object.names().getString(position);
                    //set new response
                    form.response = new CustomResponse(form.response.object.getString(name));
                    form.populateView();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public ResponseForm(){
        this.elements = new ArrayList<>();
    }


    private void initView(){
        setContentView(R.layout.response_view);

        this.intent = getIntent();
        this.list = findViewById(R.id.param_list);
        this.list.setOnItemClickListener(messageClickedHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initView();

        this.fetch();

        this.manageResponse();
    }

    private void manageResponse(){
        if(this.response.contains("404")){
            AlertDialog.showDialog("the resource does not exist", "404 not found", this);
        }else if(this.response.contains("500")){
            AlertDialog.showDialog("an internal server error occurred", "server error", this);
        }else{

            this.populateView();
            this.saveRequest();
        }
    }

    private void fetch(){
        this.request = new CustomRequest(this.intent.getStringExtra(Intent.EXTRA_TEXT), this);

        try {
            this.response = new CustomResponse(
                new RESTService().execute(
                    this.request.base,
                    this.request.method,
                    this.request.query).get()
            );

        } catch (InterruptedException e) {
            e.printStackTrace();
            AlertDialog.showDialog("network error", "error", this);
        } catch (ExecutionException e) {
            e.printStackTrace();
            AlertDialog.showDialog("Execution error", "execution error", this);
        }
    }


    private void populateView() {

        this.elements = this.response.getData();

        this.list.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.param_view,
                this.elements)
        );
    }


    private void saveRequest(){
        this.dbManager = new DBManager(this);
        this.dbManager.open();

        this.dbManager.insert(this.request.base, this.request.params);

        this.dbManager.close();
    }
}
