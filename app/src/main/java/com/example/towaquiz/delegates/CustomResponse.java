package com.example.towaquiz.delegates;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class CustomResponse {

    private String content;
    public JSONObject object;
    public JSONArray array;

    public CustomResponse(String content){
        Log.i("content", content);
        this.content = content;
    }

    public boolean contains(String fragment){
        return this.content.contains(fragment);
    }

    public ArrayList<String> getData(){
        ArrayList<String> result = new ArrayList<>();
        Object json;

        try {
            JSONTokener tokenizer = new JSONTokener(this.content);

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
