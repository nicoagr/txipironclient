package eus.ehu.txipironesmastodonfx.data_access;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.domain.Notification;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import static java.lang.Thread.getDefaultUncaughtExceptionHandler;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

public class NotificationSystem {
    public volatile boolean shutdown = false;
    public Thread mytread;

    WindowNotificationSystem WindowNotificationSystem;
    MainWindowController master;

        /*
        Documentation :https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html
         */

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void  activateNotifications(MainWindowController master) throws InterruptedException {



        this.master = master;
        WindowNotificationSystem = new WindowNotificationSystem(master);
        AsyncUtils.asyncTask(() -> {//first I get the first notification to save the id
            List<Notification> notifications;
            notifications = APIAccessManager.getNewNotification(master.token);
            return notifications;
        }, notifications -> {
            master.lastNotification = notifications.get(0).id;
        });

        final Runnable toBeEjecuterperiodically = new Runnable() {



            public void run() {

                while (!shutdown) {

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("se interrumpio");
                        shutdown = true;

                    }
                    AsyncUtils.asyncTask(() -> {//first I get the notifications
                        System.out.println("Borrame Marcos");
                        System.out.println(shutdown);

                        List<Notification> notifications;
                        notifications = APIAccessManager.getNotificationSinceip(master.token, master.lastNotification);
                        return notifications;
                    }, notifications -> {
                        int cont = 0;
                        if (!notifications.isEmpty()) {

                            if (!notifications.isEmpty() && !master.lastNotification.equals(notifications.get(0).id)) {
                                switch (notifications.get(0).type) {
                                    case "mention":
                                        WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + " has mentioned you");
                                        break;
                                    case "status":
                                        WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + " has posted an toot");
                                        ;
                                        break;
                                    case "reblog":
                                        WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + " has rebloged a toot");
                                        ;

                                        break;
                                    case "follow":
                                        WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + " has follow you");
                                        ;

                                        break;
                                    case "favourite":
                                        WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + " has liked your toot");
                                        ;
                                        break;
                                    default:
                                        WindowNotificationSystem.trowNotificationWindow("Something cool has hapened");
                                }
                                master.lastNotification = notifications.get(0).id;
                            }

                        }

                    });


                }
            }
        };



System.out.println("1");

        this.mytread = new Thread(toBeEjecuterperiodically);
        this.mytread.start();

    }
    public void deactivateNotification(){
        //System.out.println(mytread.isAlive());
        this.mytread.interrupt();
        System.out.println("We are on the deactivate Method");
        System.out.println(shutdown);

    }
}
