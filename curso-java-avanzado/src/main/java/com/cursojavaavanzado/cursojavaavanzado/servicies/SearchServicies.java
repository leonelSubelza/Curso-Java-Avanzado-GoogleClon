package com.cursojavaavanzado.cursojavaavanzado.servicies;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursojavaavanzado.cursojavaavanzado.entities.WebPage;
import com.cursojavaavanzado.cursojavaavanzado.repository.SearchRepository;

@Service
public class SearchServicies {

	@Autowired
	private SearchRepository repository;
	
	public List<WebPage> search(String textSearch) {
		return this.repository.search(textSearch);
	}

	public void save(WebPage pagina) {
		this.repository.save(pagina);
	}

	public boolean exist(String link) {
		return this.repository.exist(link);
	}
	
	public List<WebPage> getLinksToIndex(){
		return this.repository.getLinksToIndex();
	}
	
}
