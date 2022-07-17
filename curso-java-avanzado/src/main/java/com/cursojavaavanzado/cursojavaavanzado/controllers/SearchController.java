package com.cursojavaavanzado.cursojavaavanzado.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cursojavaavanzado.cursojavaavanzado.entities.WebPage;
import com.cursojavaavanzado.cursojavaavanzado.servicies.SearchServicies;

@RestController
public class SearchController {

	//Carga el objeto SearchService en memoria al momento de ejecutar (hace un new del obj automaticamente)
	@Autowired
	private SearchServicies service; 
	
	//@CrossOrigin("*") //Para que no haya error de CORS, (cuando la URL especificada desde la vista no existe)
	@RequestMapping(value = "api/search", method = RequestMethod.GET)
	public List<WebPage> search(@RequestParam Map<String,String> params) {
		String query = params.get("query");
		return this.service.search(query);
	}
}
