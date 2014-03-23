package ru.moto64.rescue_notifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class IncidentListActivity extends Activity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "ru.moto64.rescue.ID";
    int NOTIFICATION_ID = 101;

    /**
     * Tag used on log messages.
     */
    static final String TAG = "ru.moto64.rescue";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;

    ArrayAdapter<String> incidentAdapter;
    IncidentDataBase incident_db_helper;
    private SQLiteDatabase sqdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_incident_list);


        // Инициализируем наш класс-обёртку
        incident_db_helper = new IncidentDataBase(this);

        // База нам нужна для записи и чтения
        sqdb = incident_db_helper.getWritableDatabase();

        // FIXME month > incidents
        List<Incident> incidents = incident_db_helper.getAllIncidents();
        List<String> month = new ArrayList<String>();

        while (incidents.iterator().hasNext()) {
            month.add(incidents.iterator().next().toString());
        }


        incidentAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, month);

        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(incidentAdapter);


        context = getApplicationContext();

        // Check device for Play Services APK.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private void registerInBackground() {
        // TODO Auto-generated method stub

    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSend:
                sendNotification();
                break;
            case R.id.buttonFill:
                fillWithSampleData();
                break;
        }
    }

    private void fillWithSampleData() {

    }

    private void sendNotification() {
        Context context = getApplicationContext();

        // запускаем URL

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://moto64.ru/topic7905.html"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);


        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.moto64)
                        // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.moto64))
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("ДТП 18/09/2013 Suzuki SV400 vs Приора (Новоастраханское ш.с. / ул. Политехническая)")
                .setWhen(System.currentTimeMillis()) // java.lang.System.currentTimeMillis()
                .setAutoCancel(true)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("ДТП 18/09/2013 Suzuki SV400 vs Приора (Новоастраханское ш.с. / ул. Политехническая)")
                        //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Позвонил друг и сказал что напротив автосалона Mitsubishi авария suzuki и приора, у приоры ободрана левая сторона и оторвано зеркало."); // Текст уведомленимя



        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.getNotification());
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private int getAppVersion(Context context2) {
        // TODO FIXME Auto-generated method stub
        return 0;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(IncidentListActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

}
