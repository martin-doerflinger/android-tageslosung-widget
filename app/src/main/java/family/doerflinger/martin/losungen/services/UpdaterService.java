package family.doerflinger.martin.losungen.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import family.doerflinger.martin.losungen.R;
import family.doerflinger.martin.losungen.utils.AbstractDownloaderTask;
import family.doerflinger.martin.losungen.view.MainActivity;
import family.doerflinger.martin.losungen.widget.MainWidgetProvider;

/*
 * This Services runs periodically because the AutoStart Service starts it after every device
 * reboot or boot. It runs once a day with a very low precision at 00:03. Also, the Update Service
 * starts when a widget gets created on the home screen of the app. The data will be downloaded
 * asynchronously and will update the widget. However, the data stored inside the app gets updated
 * every time when the App gets opened. Reason: The MainActivity will also update the Widget
 * to prevent situations where the update service don't get any data (example if the internet connection is lost,
 * it won't try it again - in that scenario, the user has to open the app to manually update the widget)
 *
 * Please note that the UpdaterService will run in its own process, this is to reduce memory usage because
 * the real application doesn't have to run in the background (which will be paused or something like that,
 * as long as user doesn't open the app)
 */
public class UpdaterService extends Service {
    private DownloaderTask dTask;
    private String date = "";

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd. MMMM yyyy");
        Date currentDate = new Date();

        if (date.isEmpty() || !date.equals(dateFormat.format(currentDate))) {
            Toast.makeText(this, R.string.service_started, Toast.LENGTH_LONG).show();
            dTask = new DownloaderTask();
            dTask.execute();
        }
        return Service.START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_LONG).show();
    }

    private class DownloaderTask extends AbstractDownloaderTask {
        public void onPostExecute(String[] result) {
            if (result != null && (!result[0].equals("null") || !result[1].equals("null") || !result[2].equals("null") || !result[3].equals("null") || !result[4].equals("null"))) {
                date = result[0]; //set date
                Context context = UpdaterService.this.getApplicationContext();
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                RemoteViews view = new RemoteViews(getPackageName(), R.layout.losungen_widget);
                view.setTextViewText(R.id.tvLosungAndLehrtext, result[1] + " - " + result[2] + "\n" + result[3] + " - " + result[4]);
                view.setOnClickPendingIntent(R.id.tvLosungAndLehrtext, pendingIntent);
                ComponentName theWidget = new ComponentName(context, MainWidgetProvider.class);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                manager.updateAppWidget(theWidget, view);
            }
        }
    }
}