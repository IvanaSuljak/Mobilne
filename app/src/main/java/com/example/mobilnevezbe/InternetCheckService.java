package com.example.mobilnevezbe;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * VEZBA 5/6: Servis za periodičnu provjeru internet konekcije.
 * VEZBA 6: Interval provjere se čita iz SharedPreferences (može biti 1, 15, 30 min ili nikad).
 */
public class InternetCheckService extends Service {

    private static final String TAG              = "InternetCheckService";
    private static final long   DEFAULT_INTERVAL = SharedPreferencesManager.SYNC_1_MIN;

    private Handler  handler;
    private Runnable checkRunnable;
    private boolean  isRunning    = false;
    private boolean  wasConnected = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");

        // VEZBA 6: Čitanje intervala iz SharedPreferences
        long interval = SharedPreferencesManager.getInstance(this).getSyncInterval();
        Log.d(TAG, "Interval sinhronizacije: " + interval + " ms");

        // Ako je interval NIKAD, ne pokrecemo periodic check
        if (interval == SharedPreferencesManager.SYNC_NEVER) {
            Log.d(TAG, "Sinhronizacija isključena (NIKAD).");
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!isRunning) {
            isRunning = true;
            checkInternetConnection(); // Inicijalna provjera odmah
            startPeriodicCheck(interval);
        }

        return START_STICKY;
    }

    private void startPeriodicCheck(long intervalMs) {
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    checkInternetConnection();
                    // VEZBA 6: Svaki put čitaj najnoviji interval (može se promijeniti u settings)
                    long currentInterval = SharedPreferencesManager.getInstance(
                            InternetCheckService.this).getSyncInterval();
                    if (currentInterval != SharedPreferencesManager.SYNC_NEVER) {
                        handler.postDelayed(this, currentInterval);
                    }
                }
            }
        };
        handler.postDelayed(checkRunnable, intervalMs);
    }

    private void checkInternetConnection() {
        boolean isConnected = isInternetConnected();
        Log.d(TAG, "Provjera konekcije: " + (isConnected ? "CONNECTED" : "DISCONNECTED"));

        // Šalji broadcast samo kada se stanje PROMIJENI
        if (wasConnected && !isConnected) {
            sendInternetStatusBroadcast(false);
        } else if (!wasConnected && isConnected) {
            sendInternetStatusBroadcast(true);
        }
        wasConnected = isConnected;
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void sendInternetStatusBroadcast(boolean isConnected) {
        Intent intent = new Intent("com.example.mobilnevezbe.INTERNET_STATUS");
        intent.putExtra("is_connected", isConnected);
        intent.putExtra("timestamp", System.currentTimeMillis());
        Log.d(TAG, "Broadcast: isConnected=" + isConnected);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");
        isRunning = false;
        if (handler != null && checkRunnable != null) {
            handler.removeCallbacks(checkRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, InternetCheckService.class));
        Log.d(TAG, "InternetCheckService pokrenut");
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, InternetCheckService.class));
        Log.d(TAG, "InternetCheckService zaustavljen");
    }
}
