package com.example.towaquiz.delegates.utils;

import android.content.Context;

import java.util.regex.Pattern;

public class ParamMatcher {

    private ParamMatcher(){}

    public static Boolean isParamsValid(String paramsString, Context context){
        String[] params = paramsString.trim().split(",\\v*");
        return paramsMatch(params, context);
    }

    private static Boolean paramsMatch(String [] params, Context context){
        String paramRegex = "(\\s*[a-zA-Z]\\w*\\s*=\\s*\\w+\\s*)*";
        String lastParamRegex = "\\s*[a-zA-Z]\\w*\\s*=\\s*\\w+\\s*";

        Pattern paramsPattern = Pattern.compile(paramRegex);
        Pattern lastParamPattern = Pattern.compile(lastParamRegex);

        Boolean result = true;


        for(int idx = 0; idx < params.length; idx++,
                paramsPattern = idx == params.length - 1 ? lastParamPattern : paramsPattern){

            result = result & paramsPattern.matcher(params[idx]).matches();

            if(!result){
                AlertDialog.showDialog("param " + (idx+1) + " is wrong\n syntax is :" +
                                (idx == params.length - 1 ? lastParamRegex : paramRegex),
                        "param error", context);
                break;
            }
        }
        return result;
    }
}

