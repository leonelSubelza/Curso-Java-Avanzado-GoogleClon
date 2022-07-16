package com.cursojavaavanzado.cursojavaavanzado.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cursojavaavanzado.cursojavaavanzado.entities.WebPage;

public interface SearchRepository {

	List<WebPage> search(String textSearch);
	
}
