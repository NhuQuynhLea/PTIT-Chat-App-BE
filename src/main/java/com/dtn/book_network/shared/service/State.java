package com.dtn.book_network.shared.service;

public class State<T, V> {
    private T value;
    private V error;
    private StatusNotification statusNotification;

    public State(StatusNotification statusNotification, T value, V error) {
        this.value = value;
        this.error = error;
        this.statusNotification = statusNotification;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public V getError() {
        return error;
    }

    public void setError(V error) {
        this.error = error;
    }

    public StatusNotification getStatusNotification() {
        return statusNotification;
    }

    public void setStatusNotification(StatusNotification statusNotification) {
        this.statusNotification = statusNotification;
    }

    public static <T, V> StateBuilder<T, V> builder() {
        return new StateBuilder<>();
    }
}
