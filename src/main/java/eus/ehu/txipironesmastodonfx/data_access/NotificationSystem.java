package eus.ehu.txipironesmastodonfx.data_access;

import eus.ehu.txipironesmastodonfx.TxipironClient;
import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.domain.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * This class creates a thread to call periodically the API to check for new notifications.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */

public class NotificationSystem {
    public volatile boolean shutdown = false;
    public Thread mytread;
    WindowNotificationSystem WindowNotificationSystem;
    private static final Logger logger = LogManager.getLogger("NotificationSystem");
    MainWindowController master;

    /**
     * This method checks activate the notifiactions for the user that is logged in the app. It does this by generating a thread which calls the api periodically
     *
     * @param master -the MainWindowController of the app, we need it to obtain data.
     */
    public void activateNotifications(MainWindowController master) {
        this.master = master;
        WindowNotificationSystem = new WindowNotificationSystem(this);
        AsyncUtils.asyncTask(() -> {
            List<Notification> notifications;
            notifications = APIAccessManager.getNewNotification(master.token);
            return notifications;
        }, notifications -> {
            logger.debug("Downloaded last notification: id" + notifications.get(0).id);
            master.lastNotification = notifications.get(0).id;
        });

        final Runnable toBeExecutedPeriodically = () -> {
            while (!shutdown) {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    shutdown = true;
                }
                AsyncUtils.asyncTask(() -> {
                    List<Notification> notifications;
                    notifications = APIAccessManager.getNotificationSinceip(master.token, master.lastNotification);
                    return notifications;
                }, notifications -> {
                    if (notifications.isEmpty() || master.lastNotification.equals(notifications.get(0).id)) {
                        return;
                    }
                    switch (notifications.get(0).type) {
                        case "mention" ->
                                WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + TxipironClient.s("Mention"));
                        case "status" ->
                                WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + TxipironClient.s("Status"));
                        case "reblog" ->
                                WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + TxipironClient.s("Reboot"));
                        case "follow" ->
                                WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + TxipironClient.s("FollowNotif"));
                        case "favourite" ->
                                WindowNotificationSystem.trowNotificationWindow(notifications.get(0).account.acct + TxipironClient.s("FavNotif"));
                        default ->
                                WindowNotificationSystem.trowNotificationWindow(TxipironClient.s("Notif"));
                    }
                    master.lastNotification = notifications.get(0).id;
                    logger.debug("Set last notification: id=" + master.lastNotification);
                });
            }
        };
        logger.info("Starting notification service (tray icon)");
        this.mytread = new Thread(toBeExecutedPeriodically);
        this.mytread.start();
    }


    /**
     * This method checks close the thread that was created which checks for notifications.
     */
    public void deactivateNotification() {
        this.mytread.interrupt();
        WindowNotificationSystem.removeTrayIcon();
        logger.info("Deactivated notification service");
    }
}
