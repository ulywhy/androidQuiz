package com.example.towaquiz.viewcontrollers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;

import com.example.towaquiz.delegates.DBHelper;
import com.example.towaquiz.delegates.DBManager;
import com.example.towaquiz.R;
import com.example.towaquiz.delegates.AlertDialog;
import com.example.towaquiz.delegates.ParamMatcher;
import com.example.towaquiz.delegates.UrlMatcher;

public class RequestForm extends AppCompatActivity {

    private AutoCompleteTextView url;
    private EditText params;
    private String method;

    protected static final String POST_METHOD = "POST";
    protected static final String GET_METHOD = "GET";


    private DBManager dbManager;
    private SimpleCursorAdapter adapter;

    final String[] from = new String[] {
      DBHelper.URL
    };

    final int[] to = new int[] {
            R.id.suggestion
    };

    public RequestForm () {
        super();
        this.method = "";
    }

    private void initViews(){
        this.url = findViewById(R.id.url_input);
        this.params = findViewById(R.id.petition_input);
    }

    private void setAutocomplete(){
        this.dbManager = new DBManager(this);
        this.dbManager.open();

        Cursor cursor = this.dbManager.fetch();

        this.adapter = new SimpleCursorAdapter(this, R.layout.autocomplete_view, cursor, from, to, 0);

        this.adapter.setStringConversionColumn(cursor.getColumnIndexOrThrow(DBHelper.URL));

        this.adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String partialItemName = null;
                if (constraint != null) {
                    partialItemName = constraint.toString();
                }
                return dbManager.suggestItemCompletions(partialItemName);
            }
        });

        this.url.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = ((SimpleCursorAdapter)url.getAdapter()).getCursor();
                c.moveToPosition(position);

                params.setText(c.getString(c.getColumnIndex(DBHelper.PARAMS)));

                params.requestFocus();
            }
        });
        this.url.setAdapter(this.adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_view);

        this.initViews();

        this.setAutocomplete();

    }

    public void makeRequest(View view){
        if(this.isMethodSelected() &&
                UrlMatcher.isUrlValid(String.valueOf(this.url.getText()), this) &&
                ParamMatcher.isParamsValid(String.valueOf(this.params.getText()), this)) {

            Intent intent = new Intent(this, ResponseForm.class);

            intent.putExtra(Intent.EXTRA_TEXT,
                    this.url.getText() +
                          ";" + this.method + ";" +
                          this.params.getText());
            startActivity(intent);

        }
    }

    private Boolean isMethodSelected(){
        Boolean result = true;

        this.getRequestMethod();
        if(this.method.isEmpty()){
            AlertDialog.showDialog("please select a request method", "method error", this);
            result = false;
        }
        return result;
    }

    public void getRequestMethod(){

        RadioButton getbtn = findViewById(R.id.get);
        RadioButton postbtn = findViewById(R.id.post);


        if (getbtn.isChecked()) {
            this.method = this.GET_METHOD;
        }else if (postbtn.isChecked()) {
            this.method = this.POST_METHOD;
        }else{
            this.method = "";
        }

    }

    public void methodSelected(View view){
        this.url.requestFocus();
    }
}
