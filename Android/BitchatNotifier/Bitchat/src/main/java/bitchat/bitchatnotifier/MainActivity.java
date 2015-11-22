package bitchat.bitchatnotifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.view.Window;

import android.webkit.WebView;

import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import java.text.DateFormat;
import java.util.Date;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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

import java.text.NumberFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import android.os.Bundle;

import java.util.Calendar;
import android.app.AlarmManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    public static final String MY_PREFS_NAME = "BitchatPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timer timer = new Timer();

        final Button button2 = (Button) findViewById(R.id.button2);
        final Button button3 = (Button) findViewById(R.id.button3);
        final Button button4 = (Button) findViewById(R.id.button4);
        final Button button5 = (Button) findViewById(R.id.button5);
        final EditText log = (EditText) findViewById(R.id.log);

        final SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        class UpdateTimeTask extends TimerTask
        {
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        log.setText(prefs.getString("console", "").trim());
                    }
                });
            }
        }

        timer.schedule(new UpdateTimeTask(), 0, 1000);

        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        //editor.putString("console", prefs.getString("console", "")+"\n"+sdf.format(new java.util.Date())+" User ID: "+Integer.toString(prefs.getInt("user_id", 0)));
        //editor.commit();

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editor.putString("console", "");
                editor.commit();

                //Toast.makeText(getApplicationContext(), "Log cleaned!", Toast.LENGTH_LONG).show();
            }
        });

        button5.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                scheduleAlarm (MainActivity.this);
            }
        });

        button4.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                editor.putInt("user_id", 0);
                editor.commit();

                editor.putInt("last_message_id", 0);
                editor.commit();

                Intent goToActivityBIntent = new Intent(MainActivity.this, LoginActivity.class);
                goToActivityBIntent.putExtra("bitchat.bitchatnotifier", "logout");
                startActivity(goToActivityBIntent);

                finish();
            }
        });
    }

    public void scheduleAlarm (Context context)
    {
        Toast.makeText(getApplicationContext(), "Alarm scheduled!", Toast.LENGTH_LONG).show();

        Intent intentAlarm = new Intent(context, AlarmReciever.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed()
    {
        finish();
    }
}
