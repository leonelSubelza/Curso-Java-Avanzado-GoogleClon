package com.cursojavaavanzado.cursojavaavanzado.controllers;

import java.util.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cursojavaavanzado.cursojavaavanzado.entities.WebPage;

@RestController
public class SearchController {

	@RequestMapping(value = "api/search", method = RequestMethod.GET)
	public List<WebPage> search() {
		List<WebPage> results = new ArrayList<WebPage>();
		return results;
	}
}
