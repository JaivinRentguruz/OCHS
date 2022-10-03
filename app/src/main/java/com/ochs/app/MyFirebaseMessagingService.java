package com.ochs.app;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zoho.salesiqembed.ZohoSalesIQ;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Map extras = remoteMessage.getData();
        ZohoSalesIQ.Notification.handle(this.getApplicationContext(), extras, R.mipmap.ic_launcher);
    }

    @Override
    public void onNewToken(String token){
        ZohoSalesIQ.Notification.enablePush(token,true);
    }
}
