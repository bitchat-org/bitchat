package bitchat.bitchatnotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

import java.util.Calendar;

public class WebViewActivity extends AppCompatActivity
{
    public class WebViewController extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent serviceIntent = new Intent(WebViewActivity.this, AutoStartUp.class);
        this.startService(serviceIntent);

        final WebView webView = (WebView) findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewController());

        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        webView.loadUrl("https://bitchat.org/android.php");
    }

    public class WebAppInterface
    {
        Context context;

        WebAppInterface(Context c)
        {
            context = c;
        }

        @JavascriptInterface
        public void showToast (String toast)
        {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void setAddress (String address)
        {
            final SharedPreferences.Editor editor = context.getSharedPreferences("BitchatPreferences", Context.MODE_PRIVATE).edit();
            final SharedPreferences prefs = context.getSharedPreferences("BitchatPreferences", Context.MODE_PRIVATE);

            editor.putString("address", address);
            editor.commit();

            Toast.makeText(context, "Address updated: "+address, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void aboutActivity ()
        {
            context.startActivity(new Intent(WebViewActivity.this, AboutActivity.class));
        }
    }

    public void scheduleAlarm (Context context)
    {
        Toast.makeText(getApplicationContext(), "Alarm scheduled!", Toast.LENGTH_LONG).show();

        Intent intentAlarm = new Intent(context, AlarmReciever.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}