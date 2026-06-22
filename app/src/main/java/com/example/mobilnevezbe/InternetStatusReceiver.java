package com.example.mobilnevezbe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * VEZBA 5: Broadcast Receiver za praćenje statusa internet konekcije
 * Prima broadcast-ove od InternetCheckService i prikazuje notifikacije
 */
public class InternetStatusReceiver extends BroadcastReceiver {
    
    private static final String TAG = "InternetStatusReceiver";
    private NotificationHelper notificationHelper;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("com.example.mobilnevezbe.INTERNET_STATUS")) {
                handleInternetStatusChange(context, intent);
            }
        }
    }
    
    /**
     * VEZBA 5: Obrada promene statusa internet konekcije
     */
    private void handleInternetStatusChange(Context context, Intent intent) {
        boolean isConnected = intent.getBooleanExtra("is_connected", false);
        long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
        
        Log.d(TAG, "Received internet status: " + (isConnected ? "CONNECTED" : "DISCONNECTED"));
        
        // VEZBA 5: Inicijalizuj NotificationHelper ako je potrebno
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper(context);
        }
        
        if (isConnected) {
            // VEZBA 5: Internet je vraćen - ukloni notifikaciju
            notificationHelper.hideInternetOfflineNotification();
            Log.d(TAG, "Internet restored - notification hidden");
        } else {
            // VEZBA 5: Internet je izgubljen - prikaži notifikaciju
            notificationHelper.showInternetOfflineNotification();
            Log.d(TAG, "Internet lost - notification shown");
        }
    }
    
    /**
     * VEZBA 5: Registracija receiver-a u aplikaciji
     */
    public static void register(Context context) {
        IntentFilter filter = new IntentFilter("com.example.mobilnevezbe.INTERNET_STATUS");
        context.registerReceiver(new InternetStatusReceiver(), filter);
        Log.d(TAG, "InternetStatusReceiver registered");
    }
    
    /**
     * VEZBA 5: Deregistacija receiver-a
     */
    public static void unregister(Context context) {
        InternetStatusReceiver receiver = new InternetStatusReceiver();
        try {
            context.unregisterReceiver(receiver);
            Log.d(TAG, "InternetStatusReceiver unregistered");
        } catch (IllegalArgumentException e) {
            // VEZBA 5: Receiver nije bio registrovan - ignoriši
            Log.w(TAG, "Receiver was not registered");
        }
    }
}
