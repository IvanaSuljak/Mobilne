package com.example.mobilnevezbe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * VEZBA 5: Helper klasa za notifikacije
 * Kreira kanale, notifikacije i upravlja njihovim prikazom
 */
public class NotificationHelper {
    
    // VEZBA 5: Kanal ID-i za različite tipove notifikacija
    public static final String CHANNEL_USER_REGISTRATION = "user_registration_channel";
    public static final String CHANNEL_INTERNET_STATUS = "internet_status_channel";
    
    // VEZBA 5: Notifikacija ID-i
    public static final int NOTIFICATION_USER_REGISTERED = 1001;
    public static final int NOTIFICATION_INTERNET_OFFLINE = 1002;
    
    private Context context;
    private NotificationManagerCompat notificationManager;
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        
        // VEZBA 5: Kreiraj kanale pri prvoj instanci
        createNotificationChannels();
    }
    
    /**
     * VEZBA 5: Kreiranje kanala za notifikacije
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // VEZBA 5: Kanal za registraciju korisnika
            NotificationChannel userChannel = new NotificationChannel(
                CHANNEL_USER_REGISTRATION,
                "Registracija korisnika",
                NotificationManager.IMPORTANCE_HIGH
            );
            userChannel.setDescription("Notifikacije o novim registrovanim korisnicima");
            userChannel.enableLights(true);
            userChannel.enableVibration(true);
            
            // VEZBA 5: Kanal za internet status
            NotificationChannel internetChannel = new NotificationChannel(
                CHANNEL_INTERNET_STATUS,
                "Internet konekcija",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            internetChannel.setDescription("Obaveštenja o statusu internet konekcije");
            internetChannel.enableLights(true);
            internetChannel.enableVibration(false);
            
            // VEZBA 5: Registruj kanale
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(userChannel);
                manager.createNotificationChannel(internetChannel);
            }
        }
    }
    
    /**
     * VEZBA 5: Notifikacija o novom korisniku
     */
    public void showUserRegisteredNotification(String userName) {
        // VEZBA 5: Intent za akciju "Prikaži"
        Intent showIntent = new Intent(context, HomeScreenActivity.class);
        showIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showIntent.putExtra("FROM_NOTIFICATION", true);
        showIntent.putExtra("USER_NAME", userName);
        
        PendingIntent showPendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            showIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // VEZBA 5: Kreiranje notifikacije
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_USER_REGISTRATION)
            .setSmallIcon(android.R.drawable.ic_dialog_email) // VEZBA 5: Ikona za notifikaciju
            .setContentTitle("Novi korisnik registrovan!")
            .setContentText("Korisnik " + userName + " se uspešno registrovao")
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText("Novi korisnik " + userName + " se upravo registrovao u aplikaciji. Kliknite da vidite detalje."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false) // VEZBA 5: Ne uklanjaj automatski
            .addAction(android.R.drawable.ic_menu_view, "Prikaži", showPendingIntent) // VEZBA 5: Akcija dugme
            .setContentIntent(showPendingIntent);
        
        // VEZBA 5: Prikaz notifikacije
        notificationManager.notify(NOTIFICATION_USER_REGISTERED, builder.build());
    }
    
    /**
     * VEZBA 5: Notifikacija o offline statusu
     */
    public void showInternetOfflineNotification() {
        // VEZBA 5: Intent za akciju "Podešavanja"
        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(
            context, 
            1, 
            settingsIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // VEZBA 5: Kreiranje notifikacije
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_INTERNET_STATUS)
            .setSmallIcon(android.R.drawable.stat_notify_error) // VEZBA 5: Ikona za grešku
            .setContentTitle("Nema internet konekcije")
            .setContentText("Proverite internet konekciju i pokušajte ponovo")
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText("Aplikacija nije povezana na internet. Kliknite da otvorite podešavanja i uključite Wi-Fi ili mobilne podatke."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false) // VEZBA 5: Ne uklanjaj automatski
            .addAction(android.R.drawable.ic_menu_preferences, "Podešavanja", settingsPendingIntent) // VEZBA 5: Akcija dugme
            .setOngoing(true); // VEZBA 5: Ongoing notifikacija
        
        // VEZBA 5: Prikaz notifikacije
        notificationManager.notify(NOTIFICATION_INTERNET_OFFLINE, builder.build());
    }
    
    /**
     * VEZBA 5: Uklanjanje notifikacije o internet statusu kada je konekcija vraćena
     */
    public void hideInternetOfflineNotification() {
        notificationManager.cancel(NOTIFICATION_INTERNET_OFFLINE);
    }
    
    /**
     * VEZBA 5: Provera da li su notifikacije dozvoljene
     */
    public boolean areNotificationsEnabled() {
        return notificationManager.areNotificationsEnabled();
    }

    // VEZBA 9: Notifikacija za shake događaj
    public void showShakeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_INTERNET_STATUS)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Shake detektovan!")
                .setContentText("Uređaj je protresan.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        notificationManager.notify(1003, builder.build());
    }
}
