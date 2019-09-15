package family.doerflinger.martin.losungen.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.RemoteViews;

import java.util.Calendar;

import family.doerflinger.martin.losungen.R;
import family.doerflinger.martin.losungen.services.UpdaterService;

public class MainWidgetProvider extends AppWidgetProvider {
    private AlarmManager manager;
    private Intent intent;
    private PendingIntent service;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 3);
        calendar.set(Calendar.SECOND, 0);

        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, UpdaterService.class);
        if (service == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                service = PendingIntent.getForegroundService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            } else {
                service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            }
        }
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, service); //once a day
        context.startService(intent);
    }

    public void onDeleted(Context context, int[] appWidgetIds) { super.onDeleted(context, appWidgetIds); }
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) { super.onRestored(context, oldWidgetIds, newWidgetIds); }
    public void onReceive(Context context, Intent intent) { super.onReceive(context, intent); }
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) { super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions); }
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}