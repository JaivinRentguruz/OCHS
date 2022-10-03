package com.ochs.app.adapters;

import android.os.Bundle;

import com.ochs.app.model.InsuranceModel;

import java.util.Locale;

public class  Helper {

    public static Bundle bundle = new Bundle();
    public static InsuranceModel insuranceModel = new InsuranceModel();
    public static Boolean insuranceValue = false;
    public static Boolean VISIBLE = false;

    public static String currencySymbol;
    public static String currencyName;
    public static int fuel=1;

    public static String getAmtount(Double amt, Boolean symbol){
        String value = null;
        String amt2 = String.format(Locale.US,"%.2f",amt);
        if (symbol){
            value = currencyName + currencySymbol + amt2;
        } else {
            value = currencySymbol + amt2;
        }
        return value;
    }

    public static String getDistance(String value){
        if (fuel==1){
            return  "Kms "+ value;
        } else {
            return "MILES" + value;
        }
    }
}
