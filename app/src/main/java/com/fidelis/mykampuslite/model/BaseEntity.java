package com.fidelis.mykampuslite.model;

import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * [INHERITANCE - Level 1] Root hierarki model. WAJIB abstract.
 * Field: id, createdAt, updatedAt (semua private - [ENCAPSULATION]).
 *
 * Catatan Room: field milik superclass otomatis menjadi kolom di tabel
 * subclass @Entity selama punya getter/setter.
 */
public abstract class BaseEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long createdAt;
    private long updatedAt;

    protected BaseEntity() {
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** Perbarui penanda waktu perubahan. */
    public void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    // equals() & hashCode() -> dipakai DiffUtil (BONUS +10)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id == that.id
                && createdAt == that.createdAt
                && updatedAt == that.updatedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt);
    }
}
