package com.fidelis.mykampuslite.data.repository;

/**
 * Exception netral (tidak bergantung pada SQLite) untuk menandai data kembar.
 * Repository menerjemahkan SQLiteConstraintException -> exception ini,
 * sehingga ViewModel tidak perlu tahu detail database ([ABSTRACTION]).
 */
public class DuplicateDataException extends RuntimeException {
    public DuplicateDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateDataException(String message) {
        super(message);
    }
}
