package com.cuuw619.fuxapi.config;

public abstract class FuxSetting<T> {
    private final String name;
    private final T defaultValue;
    private T value;

    protected FuxSetting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String name() { return name; }
    public T get() { return value; }
    public void set(T value) { this.value = value; }
    public T defaultValue() { return defaultValue; }
    public void reset() { value = defaultValue; }
}
