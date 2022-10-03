package com.ochs.app.adapters;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ochs.app.R;

public class CustomToast
{

    public static void showToast(Activity activity, String msg, int type)
    {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) activity.findViewById(android.R.id.content), false);

        if(type == 1)
            layout.setBackground(activity.getDrawable(R.drawable.curve_box_error));
        TextView tv = (TextView) layout.findViewById(R.id.txtMsg);
        tv.setText(msg);
        Toast toast = new Toast(activity.getBaseContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.show();
    }

}
