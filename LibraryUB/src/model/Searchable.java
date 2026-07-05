package model;

import java.util.List;

/**
 * Interface Searchable - untuk fitur pencarian
 * OOP Concept: Interface (multiple interface implementation)
 */
public interface Searchable<T> {
    List<T> cari(String keyword);
}
