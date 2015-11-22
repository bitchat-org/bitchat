package bitchat.bitchatnotifier;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.Looper;
import android.os.Handler;

import android.content.Intent;

public class AlarmReciever extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        String time = DateFormat.getTimeInstance().format(new Date());

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                /*new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                public void run()
                {
                    final SharedPreferences.Editor editor = context.getSharedPreferences("BitchatPreferences", Context.MODE_PRIVATE).edit();
                    final SharedPreferences prefs = context.getSharedPreferences("BitchatPreferences", Context.MODE_PRIVATE);

                    Toast.makeText(context, "Timer executed "+prefs.getInt("tmp", 0), Toast.LENGTH_LONG).show();

                    editor.putInt("tmp", prefs.getInt("tmp", 0) + 1);
                    editor.commit();
                }
                });*/

                long time = System.currentTimeMillis();
                final SharedPreferences.Editor editor = context.getSharedPreferences("BitchatPreferences", Context.MODE_PRIVATE).edit();
                final SharedPreferences prefs = context.getSharedPreferences("BitchatPreferences", Context.MODE_PRIVATE);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://proxy.bitchat.org/last.php?user_id=1&address="+prefs.getString("address", ""));

                try
                {
                    List nameValuePairs = new ArrayList();
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);

                    final int result = tryParseInt(EntityUtils.toString(response.getEntity()));

                    //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                    //editor.putString("console", prefs.getString("console", "")+"\n"+sdf.format(new java.util.Date())+" Message ID: "+Integer.toString(result)+" ("+Long.toString(System.currentTimeMillis()-time)+" ms)");
                    //editor.putString("console", prefs.getString("console", "")+"\n"+sdf.format(new java.util.Date())+" Message ID: "+Integer.toString(result));
                    //editor.commit();

                    //new Handler(Looper.getMainLooper()).post(new Runnable()
                    //{
                        //public void run()
                        //{
                        //    Toast.makeText(context, "Updated!", Toast.LENGTH_LONG).show();
                        //}
                    //});

                    if (result != 0)
                    {
                        /*new Handler(Looper.getMainLooper()).post(new Runnable()
                        {
                            public void run()
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                                Toast.makeText(context, sdf.format(new java.util.Date())+" Result: "+result, Toast.LENGTH_LONG).show();
                            }
                        });*/

                        if (prefs.getInt("last_message_id", 0) != 0 && prefs.getInt("last_message_id", 0) != result)
                        {
                            //editor.putString("console", prefs.getString("console", "")+"\n"+sdf.format(new java.util.Date())+" New message!");
                            //editor.commit();

                            Notify (context);
                        }

                        editor.putInt("last_message_id", result);
                        editor.commit();
                    }
                }

                catch (IOException e)
                {
                }
            }
        });

        thread.start();
    }

    int tryParseInt (String value)
    {
        try
        {
            return Integer.parseInt(value);
        }

        catch(NumberFormatException nfe)
        {
            return 0;
        }
    }

    private void Notify (Context context)
    {
        Intent notificationIntent = new Intent(context, WebViewActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification n = new Notification.Builder(context)
                .setContentTitle("Bitchat")
                .setContentText("You have a new message")
                .setSmallIcon(R.drawable.fav)
                .setContentIntent(intent)
                .setAutoCancel(true).build();

        n.defaults |= Notification.DEFAULT_VIBRATE;
        n.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }
}