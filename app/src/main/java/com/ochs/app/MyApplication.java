package com.ochs.app;

import android.app.Application;

import com.zoho.salesiqembed.ZohoSalesIQ;

public class MyApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        //initialize SalesIQ
        ZohoSalesIQ.init(this,"%2FNJqdRRuhG8KjvpSe8HH3iYwEaKdY%2BUMZcKdJznlSlLNP9vcMie9aZmYP0etprRl","TYtI2ZH2%2BYRvVolZYqdothUsc9AY6MzqAoVqsfePrzcvWzsTl00%2B3suhteszZhLZKRE7EDU0HR4cXsgRQ%2FOZVaF2YNmN%2BPhMgYADr7lfKGKrA889%2FjwasHfdgwkiWf80ynsKU%2BBf%2BfROZ1106fCaMw%3D%3D");

        //by default launcher will be hidden can be enabled with following line.
        ZohoSalesIQ.showLauncher(true);
    }
}
