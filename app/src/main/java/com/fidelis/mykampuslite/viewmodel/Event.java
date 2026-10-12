package com.fidelis.mykampuslite.viewmodel;

/**
 * Pembungkus data "sekali pakai" untuk LiveData (Toast, clear form, dsb),
 * agar tidak terpicu ulang saat layar diputar.
 */
public class Event<T> {
    private final T content;
    private boolean handled = false;

    public Event(T content) {
        this.content = content;
    }

    /** Mengembalikan isi hanya pada pemanggilan pertama; selanjutnya null. */
    public T getContentIfNotHandled() {
        if (handled) return null;
        handled = true;
        return content;
    }

    public T peekContent() {
        return content;
    }
}
