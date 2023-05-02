package eus.ehu.txipironesmastodonfx.data_access;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.domain.Notification;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class NotificationSystem {
    MainWindowController master;

        /*
        Documentation :https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html
         */

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void activateNotifications(MainWindowController master) {

        this.master = master;
        AsyncUtils.asyncTask(() -> {//first I get the first notification to save the id
            List<Notification> notifications;
            notifications = APIAccessManager.getNewNotification(master.token);
            return notifications;
        }, notifications -> {
            master.lastNotification = notifications.get(0).id;
        });


        final Runnable toBeEjecuterperiodically = new Runnable() {
            public void run(){
                AsyncUtils.asyncTask(() -> {//first I get the notifications
                    List<Notification> notifications;
                    notifications = APIAccessManager.getNewNotification(master.token);
                    return notifications;
                }, notifications -> {
                    int cont = 0;
                    if(!master.lastNotification.equals(notifications.get(0).id)){
                         WindowNotificationSystem.trowNotificationWindow("New notification", "aaaa");
                         master.lastNotification = notifications.get(0).id;
                    }
                    //master.lastNotification
                });


            }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(toBeEjecuterperiodically, 10, 5, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
            }
        }, 60 * 60, SECONDS);


    }

}
