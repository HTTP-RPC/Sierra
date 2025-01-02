/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.httprpc.sierra;

import javax.swing.SwingUtilities;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

/**
 * Executes tasks in the background and notifies result handlers on the UI
 * thread.
 */
public class TaskExecutor {
    private ExecutorService executorService;

    private int count = 0;
    private List<Runnable> listeners = new LinkedList<>();

    /**
     * Constructs a new task executor.
     *
     * @param executorService
     * The executor service to which tasks will be submitted.
     */
    public TaskExecutor(ExecutorService executorService) {
        if (executorService == null) {
            throw new IllegalArgumentException();
        }

        this.executorService = executorService;
    }

    /**
     * Executes a task.
     *
     * @param <T>
     * The result type.
     *
     * @param task
     * The task to execute.
     *
     * @param handler
     * The result handler.
     */
    public <T> void execute(Callable<T> task, BiConsumer<T, Exception> handler) {
        if (task == null || handler == null) {
            throw new IllegalArgumentException();
        }

        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException();
        }

        count++;

        executorService.submit(() -> {
            try {
                var result = task.call();

                SwingUtilities.invokeLater(() -> {
                    handler.accept(result, null);

                    complete();
                });
            } catch (Exception exception) {
                SwingUtilities.invokeLater(() -> {
                    handler.accept(null, exception);

                    complete();
                });
            }
        });
    }

    private void complete() {
        if (--count == 0 && !listeners.isEmpty()) {
            for (var listener : listeners) {
                listener.run();
            }

            listeners.clear();
        }
    }

    /**
     * Adds a completion listener.
     *
     * @param listener
     * A callback that will be notified when all pending tasks are complete.
     */
    public void notify(Runnable listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }

        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException();
        }

        listeners.add(listener);
    }
}
