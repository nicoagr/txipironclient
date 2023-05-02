package eus.ehu.txipironesmastodonfx.data_access;

import javafx.application.Platform;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * This class will help us perform
 * asynchronous tasks. That way, the ui isn't blocked
 * while the task is being performed.
 *
 * @author Juanan Pereira
 */
public final class AsyncUtils {

    private AsyncUtils() {
    }

    @FunctionalInterface
    public interface Consumer<T> {
        void apply(T t) throws IOException, AWTException;
    }

    @FunctionalInterface
    public interface ProducerWithThrow<R> {
        R apply() throws Throwable;
    }

    @FunctionalInterface
    public interface ConsumerWithThrow<T> {
        void apply(T t) throws Throwable;
    }

    /**
     * Create and run an async task using the provided function as the asynchronous operation,
     * and the callback as the success operation. Error are ignored and returned as null values.
     *
     * @param asyncOperation The asynchronous operation.
     * @param callback       The success callback.
     * @param <V>            The type of value produced asynchronously and provided to the callback as a result.
     */
    public static <V> void asyncTask(ProducerWithThrow<V> asyncOperation, Consumer<V> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return asyncOperation.apply();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
            }
        }).thenAcceptAsync(v -> {
            if (callback != null)
                Platform.runLater(() -> {
                    try {
                        callback.apply(v);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (AWTException e) {
                        throw new RuntimeException(e);
                    }
                });
        });
    }
}
