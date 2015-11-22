package bitchat.bitchatnotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AutoStartUp extends Service
{
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public static final String MY_PREFS_NAME = "BitchatPreferences";

    @Override
    public void onCreate()
    {
        super.onCreate();

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        final SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        editor.putString("console", prefs.getString("console", "")+"\n"+sdf.format(new java.util.Date())+" Alarm scheduled from DB!");
        editor.commit();

        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30000, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}