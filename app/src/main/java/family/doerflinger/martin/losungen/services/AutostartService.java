package family.doerflinger.martin.losungen.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

/*
 * After every reboot or boot of the device, the AlarmManager doesn't have any updating events anymore.
 * This means, it is not enough to start the UpdaterService every time when a widget gets added to the
 * homescreen. You also want to start the UpdaterService after every Intent that the boot/reboot
 * has been completed. To add this functionality, you can use this class. Don't forget to set the
 * right permissions in your manifest-File :) Happy updating :)
 */
public class AutostartService extends BroadcastReceiver {
    private AlarmManager manager;
    private Intent intent;
    private PendingIntent service;

    public void onReceive(Context context, Intent intent) {
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
}
