package com.kansus.kstrainer.repository;

import java.util.List;

public interface Repository<T> {

    List<T> getAll();

    T getById(int id);
}
