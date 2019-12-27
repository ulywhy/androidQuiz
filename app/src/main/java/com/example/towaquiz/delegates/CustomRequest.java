package com.example.towaquiz.delegates;


import android.content.Context;

import com.example.towaquiz.delegates.utils.ParamMatcher;
import com.example.towaquiz.delegates.utils.UrlMatcher;

public class CustomRequest {

    public static final String TOKEN = ";";
    public static final String PARAM_TOKEN = "&";

    public static final String POST_METHOD = "POST";
    public static final String GET_METHOD = "GET";

    private static final int BASE_IDX = 0;
    private static final int METHOD_IDX = 1;
    private static final int QUERY_IDX = 2;
    private static final int PARAMS_IDX = 3;

    public String method;
    public String base;
    public String query;
    public String params;
    private Context context;

    public CustomRequest(String base, String method, String params, Context context) {
        this.context = context;
        this.method = method;
        this.base = base;
        this.params = params;
        this.setQuery(params);
    }

    public CustomRequest(String string, Context context){
        this.context = context;
        String[] attrs = string.split(TOKEN);

        this.base = attrs[BASE_IDX];
        this.method = attrs[METHOD_IDX];
        this.params = attrs.length > PARAMS_IDX ? attrs[PARAMS_IDX] : "";
        this.query = attrs.length > QUERY_IDX ? attrs[QUERY_IDX] : "";
    }

    private void setQuery(String params) {

        String[] paramList = params.trim().split(",\\s*\\v*");

        for (int idx = 0; idx < paramList.length; idx++) {
            params += paramList[idx] + (idx == paramList.length - 1 ? "" : PARAM_TOKEN);
        }

        this.query = params;
    }

    public boolean isValid() {
        return UrlMatcher.isUrlValid(this.base, this.context) &&
                ParamMatcher.isParamsValid(this.params, this.context);
    }

    @Override
    public String toString() {
        return this.base + TOKEN + this.method + TOKEN + this.query + TOKEN + this.params;
    }
}