package family.doerflinger.martin.losungen.view;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import family.doerflinger.martin.losungen.R;
import family.doerflinger.martin.losungen.utils.AbstractDownloaderTask;
import family.doerflinger.martin.losungen.widget.MainWidgetProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String[] data; //stores Losungsdata
    private TextView tvDate, tvLosung, tvLosungVers, tvLehrtext, tvLehrtextVers;
    private Button btnRefresh;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        data = new String[5];
        for(int i=0;i<data.length;i++)
            data[i] = "";
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvLosung = (TextView) findViewById(R.id.tvLosung);
        tvLehrtext = (TextView) findViewById(R.id.tvLehrtext);
        tvLosungVers = (TextView) findViewById(R.id.tvLosungVers);
        tvLehrtextVers = (TextView) findViewById(R.id.tvLehrtextVers);
        btnRefresh.setOnClickListener(this);
        tvLosungVers.setOnClickListener(this);
        tvLehrtextVers.setOnClickListener(this);
        btnRefresh.performClick(); //simulate button press when opening Activity to update content
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_refresh:
                btnRefresh.performClick();
                break;
            case R.id.action_share_losung:
                if(!data[0].isEmpty() && !data[1].isEmpty() && !data[2].isEmpty() && !data[3].isEmpty() && !data[4].isEmpty()) {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain").putExtra(android.content.Intent.EXTRA_TEXT, data[1] + " - " + data[2] + "\n" + data[3] + " - " + data[4]);
                    startActivity(Intent.createChooser(i,getString(R.string.shareLosungLehrtext)));
                }
                break;
            case R.id.action_about:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_about);
                dialog.setTitle("Tageslosung");
                TextView heading = (TextView) dialog.findViewById(R.id.about_heading);
                heading.setText("Tageslosung");
                TextView text = (TextView) dialog.findViewById(R.id.about_text);
                text.setText("Version: v1.0.0\nWebseite: https://doerflinger.family/apps\n\nCopyright \u00a9 2018 Martin Dörflinger");
                Button dialogButton = (Button) dialog.findViewById(R.id.about_ok);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRefresh:
                DownloaderTask dTask = new DownloaderTask();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd. MMMM yyyy");
                Date currentDate = new Date();

                if(data[0].isEmpty() || !data[0].equals(dateFormat.format(currentDate))) { //load data because haven't been downloaded yet
                    dTask.execute();
                } else {
                    Toast.makeText(this.getBaseContext(), R.string.already_updated, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tvLosungVers:
            case R.id.tvLehrtextVers:
                try {
                    Intent webview = new Intent(MainActivity.this, WebViewActivity.class);
                    webview.putExtra("url", "https://www.bibleserver.com/text/LUT/" + URLEncoder.encode(((TextView)v).getText().toString(), "UTF-8")); //Optional parameters
                    startActivity(webview);
                } catch (UnsupportedEncodingException e) { }
                break;
            default:
                break;
        }
    }

    private class DownloaderTask extends AbstractDownloaderTask {
        public void onPostExecute(String[] result) {
            if(result != null  && (!result[0].equals("null") || !result[1].equals("null") || !result[2].equals("null") || !result[3].equals("null") || !result[4].equals("null"))) {
                //Update app
                tvDate.setText(result[0]);
                tvLosung.setText(result[1]);
                tvLosungVers.setText(result[2]);
                tvLehrtext.setText(result[3]);
                tvLehrtextVers.setText(result[4]);
                for(int i=0; i<5; i++)
                    data[i] = new String(result[i]); //real copy; not a reference

                //Update widget
                Context context = MainActivity.this;
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.losungen_widget);
                ComponentName thisWidget = new ComponentName(context, MainWidgetProvider.class);
                remoteViews.setTextViewText(R.id.tvLosungAndLehrtext, result[1] + " - " + result[2] + "\n" + result[3] + " - " + result[4]);
                remoteViews.setOnClickPendingIntent(R.id.tvLosungAndLehrtext, pendingIntent);
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                Toast.makeText(context, R.string.updated, Toast.LENGTH_SHORT).show();
            }
        }
    }
}