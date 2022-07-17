package com.cursojavaavanzado.cursojavaavanzado.servicies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

/*
 * Esta clase se encargará de hacer el web scrapping, es decir, decidira entre todas las páginas que se encuentren cuál mostrar
 * */

@Service
public class SpiderService {

	
	//Procesa una página web
	public void indexWebPage() {
		String url = "https://www.bbc.com/";
		String content = getWebContent(url);
		/*Se debe procesar la pag para saber que devolverle al front (title, description*/
	}
	
	
	//Dado un link obtiene todo su contenido html
	private String getWebContent(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String encoding = conn.getContentEncoding();

            InputStream input = conn.getInputStream();

            Stream<String> lines = new BufferedReader(new InputStreamReader(input))
                    .lines();

            return lines.collect(Collectors.joining());
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
        return "";
    }
	
}
