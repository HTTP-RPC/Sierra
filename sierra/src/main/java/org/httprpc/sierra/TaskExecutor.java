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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

/**
 * Executes tasks in the background and notifies result handlers on the UI
 * thread.
 */
public class TaskExecutor {
    private ExecutorService executorService;

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
     * @param callable
     * The task to execute.
     *
     * @param consumer
     * The result handler.
     */
    public <T> void execute(Callable<T> callable, BiConsumer<T, Exception> consumer) {
        if (callable == null && consumer == null) {
            throw new IllegalArgumentException();
        }

        executorService.submit(() -> {
            try {
                var result = callable.call();

                SwingUtilities.invokeLater(() -> consumer.accept(result, null));
            } catch (Exception exception) {
                SwingUtilities.invokeLater(() -> consumer.accept(null, exception));
            }
        });
    }
}
