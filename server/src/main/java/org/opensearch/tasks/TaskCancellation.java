/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.tasks;

import org.opensearch.ExceptionsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskCancellation is a wrapper for a task and its cancellation reasons.
 *
 * @opensearch.internal
 */
public class TaskCancellation implements Comparable<TaskCancellation> {
    private final CancellableTask task;
    private final List<Reason> reasons;
    private final List<Runnable> onCancelCallbacks;

    public TaskCancellation(CancellableTask task, List<Reason> reasons, List<Runnable> onCancelCallbacks) {
        this.task = task;
        this.reasons = reasons;
        this.onCancelCallbacks = onCancelCallbacks;
    }

    public CancellableTask getTask() {
        return task;
    }

    public List<Reason> getReasons() {
        return reasons;
    }

    public String getReasonString() {
        return reasons.stream().map(Reason::getMessage).collect(Collectors.joining(", "));
    }

    /**
     * Cancels the task and invokes all onCancelCallbacks.
     */
    public void cancel() {
        if (isEligibleForCancellation() == false) {
            return;
        }

        task.cancel(getReasonString());

        List<Exception> exceptions = new ArrayList<>();
        for (Runnable callback : onCancelCallbacks) {
            try {
                callback.run();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        ExceptionsHelper.maybeThrowRuntimeAndSuppress(exceptions);
    }

    /**
     * Returns the sum of all cancellation scores.
     *
     * A zero score indicates no reason to cancel the task.
     * A task with a higher score suggests greater possibility of recovering the node when it is cancelled.
     */
    public int totalCancellationScore() {
        return reasons.stream().mapToInt(Reason::getCancellationScore).sum();
    }

    /**
     * A task is eligible for cancellation if it has one or more cancellation reasons, and is not already cancelled.
     */
    public boolean isEligibleForCancellation() {
        return (task.isCancelled() == false) && (reasons.size() > 0);
    }

    @Override
    public int compareTo(TaskCancellation other) {
        return Integer.compare(totalCancellationScore(), other.totalCancellationScore());
    }

    /**
     * Represents the cancellation reason for a task.
     */
    public static class Reason {
        private final String message;
        private final int cancellationScore;

        public Reason(String message, int cancellationScore) {
            this.message = message;
            this.cancellationScore = cancellationScore;
        }

        public String getMessage() {
            return message;
        }

        public int getCancellationScore() {
            return cancellationScore;
        }
    }
}
