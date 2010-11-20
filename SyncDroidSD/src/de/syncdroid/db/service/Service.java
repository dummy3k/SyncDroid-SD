package de.syncdroid.db.service;

import java.util.List;

public interface Service<T> {
	T findById(Long id);
	List<T> list();
	
	void save(T obj);
	void update(T obj);
	
	void saveOrUpdate(T obj);
	void delete(T obj);
}
