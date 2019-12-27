package com.example.towaquiz.viewcontrollers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;

import com.example.towaquiz.delegates.CustomRequest;
import com.example.towaquiz.delegates.DBHelper;
import com.example.towaquiz.delegates.DBManager;
import com.example.towaquiz.R;
import com.example.towaquiz.delegates.utils.AlertDialog;

public class RequestForm extends AppCompatActivity {

    private AutoCompleteTextView url;
    private EditText params;
    private RadioButton getbtn;
    private RadioButton postbtn;
    private CustomRequest request;


    final String[] from = new String[] {DBHelper.URL};

    final int[] to = new int[] {R.id.suggestion};

    public RequestForm () {
        super();
    }

    private void initView(){
        setContentView(R.layout.request_view);

        this.url = findViewById(R.id.url_input);
        this.params = findViewById(R.id.petition_input);
        this.getbtn = findViewById(R.id.get);
        this.postbtn = findViewById(R.id.post);

        this.setAutocomplete();

        url.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        params.requestFocus();
                    return true;
                }
                return false;
            }
        });

        //on url selected lose focus and fill params
        this.url.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = ((SimpleCursorAdapter)url.getAdapter()).getCursor();
                c.moveToPosition(position);

                params.setText(c.getString(c.getColumnIndex(DBHelper.PARAMS)));

                params.requestFocus();
            }
        });
    }

    private void setAutocomplete(){
        final DBManager dbManager = new DBManager(this);;

        dbManager.open();

        Cursor cursor = dbManager.fetch();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.autocomplete_view, cursor, from, to, 0);

        //config cursorAdapter
        adapter.setStringConversionColumn(cursor.getColumnIndexOrThrow(DBHelper.URL));

        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String partialItemName = null;
                if (constraint != null) {
                    partialItemName = constraint.toString();
                }
                return dbManager.suggestItemCompletions(partialItemName);
            }
        });

        this.url.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initView();
    }

    public void makeRequest(View view){

        this.request = new CustomRequest(
                String.valueOf(this.url.getText()),
                this.getRequestMethod(),
                String.valueOf(this.params.getText()),
                this
            );

        if(this.isMethodSelected() && this.request.isValid()) {

            Intent intent = new Intent(this, ResponseForm.class);

            intent.putExtra(Intent.EXTRA_TEXT, this.request.toString());
            startActivity(intent);
        }
    }

    private boolean isMethodSelected(){
        boolean result = true;

        if(this.request.method.isEmpty()){
            AlertDialog.showDialog("please select a request method", "method error", this);
            result = false;
        }
        return result;
    }

    public String getRequestMethod(){

        if (getbtn.isChecked()) {
            return CustomRequest.GET_METHOD;
        }else if (postbtn.isChecked()) {
            return CustomRequest.POST_METHOD;
        }else{
            return "";
        }
    }

    public void methodSelected(View view){

        this.url.requestFocus();
    }
}
