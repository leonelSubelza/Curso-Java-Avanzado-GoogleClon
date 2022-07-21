package com.cursojavaavanzado.cursojavaavanzado.servicies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursojavaavanzado.cursojavaavanzado.entities.WebPage;

import static org.hibernate.internal.util.StringHelper.isBlank;

/*
 * Esta clase se encargará de hacer el web scrapping, es decir, decidira entre todas las páginas que se encuentren cuál mostrar
 * */

@Service
public class SpiderService {

	
	@Autowired
	private SearchServicies searchServicies;
	
	//Procesa las páginas web,
	public void indexWebPages() {
		
		System.out.println("indexando paginas");
		
		//Obtenemos todas las paginas para procesar
		List<WebPage> linksToIndex = this.searchServicies.getLinksToIndex();
		
//		System.out.println("cantidad de links para indexar: "+linksToIndex.size());
		
		//Por cada link guardamos su contenido
		linksToIndex.stream().parallel().forEach(webPage -> {
			try {
				indexPage(webPage);	
			}catch(Exception e) {
				System.out.println("Error en indexWebPages "+e.getMessage());
			}
			
		});
	}

	//Procesa una pagina web. La guarda en la bd
	private void indexPage(WebPage webPage) throws Exception{
		//Obtenemos el contenido de la pag
//		System.out.println("obteniendo cont de una pag... ");
		String content = getWebContent(webPage.getUrl());
		String domain = getDomain(webPage.getUrl());
		
		//Si la página está en blanco
		if(isBlank(content)) {
			return ;
		}
		
		//Cuanndo obtenemos la pag la actualizamos ya que lo tenemos en la bd 
		indexAndSaveWebPage(webPage,content);
		saveLinks(domain,content);
	}
	
	//Obtenemos el dominio de la pagina
	//Ej "https://www.bbc.com/asdf/".split("/"); -> ['https:', '', 'www.bbc.com', 'asdf', '']
	private String getDomain(String url) {
		String[] aux = url.split("/");
		return aux[0] + "//" + aux[2];
	}


	public void saveLinks(String domain, String content){
		//Obtiene todos los links que hayan en un html
//		System.out.println("se obtienen todos los links...");
//		List<String> links = getLinks(domain,content);
		List<String> links = new ArrayList<>();
		try {
			links = Arrays.asList( getLinks(content,10));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("se obtuvieron todos los links");
		//El map retorna algo, luego con el forEach es por cada valor devuelto por el map hace algo
		
		//Por cada link obtenido se guarda/actualiza el objeto WebPage con solo el atributo de url
		links.stream()
			.filter(link -> !this.searchServicies.exist(link) && link.length()<100)
			
			.map(link -> new WebPage(link))
			.forEach(webPage -> {
//				System.out.println("guardando link :"+webPage.getUrl());
				this.searchServicies.save(webPage);
				
			});
		
	}
	
	//Obtiene todos los links de la pagina
	public List<String> getLinks(String domain, String content){
		List<String> links = new ArrayList<>();
		
		/*
		 * Obtenenmos todo el html y vamos obteniendo todo lo que sigue despues de un href, luego en el foreach cortamos 
		 * cuando aparece un '"' y nos quedaria un href, asi con todos
		 
		 * */
		
		if(content == null || content.equals("")) {
			return new ArrayList<String>();
		}
		String[] splitHref = content.split("href=\"");
		List<String> listHref1 = Arrays.asList(splitHref);
		List<String> listHref = new ArrayList<>();
		
//		System.out.println("Cant de links para guardar: "+listHref1.size());
		int cantLinks=0;
		while(listHref1.size()>cantLinks || cantLinks<10) {
			listHref.add(listHref1.get(cantLinks));
			cantLinks++;
		}
		
		
//		listHref.remove(0);
//		System.out.println("paso el while");
		listHref.stream().forEach(strHref -> {
			String[] aux = strHref.split("\"");
			links.add(aux[0]);
		});
		
		return cleanLinks(domain,links);
	}
	
	
	//Como la pag html tendrá href con archivos .css, .js., etc se filtraran solo aquellos que no tengan esta extension (solo quedaran los html)
	private List<String> cleanLinks(String domain, List<String> links) {
		String[] excludeExtensions = new String[] {"css","js","json","jpg","png","woff2"};
		
		/*Por cada link filtramos aquellos links que no contengan algun valor del array excludedExtensions. 
		 * Arrays.stream... recorre un arreglo
		 * noneMatch devuelve los valores que no contengan algun valor del array.
		 * Luego se usa la expresion link::endsWith que es una forma abrevidada de usar un metodo estatico se le llama 'referencia de metodo',
		 * una forma similar seria escribir .noneMatch(extension -> link.endsWith(extension)) 
		 * Luego se guarda en una lista todos los elementos filtrados
		 */
		List<String> resultLinks = links.stream().filter(link -> Arrays.stream(excludeExtensions).noneMatch(link::endsWith))
											.map(link -> link.startsWith("/") ? domain : link)
											.filter(link -> link.startsWith("https"))
											.collect(Collectors.toList());
		
		//Para que no se guarden elementos repetidos se pasa la lista a un hash y luego a un arraylist
		List<String> uniqueLinks = new ArrayList<String>();
		uniqueLinks.addAll(new HashSet<>(resultLinks));
		return uniqueLinks;
		
	}


	private String getTitle(String content) {
		
		//Dado todo el html, lo cortamos en dos donde diga <title>, split funciona como: "hola".split("o") => ["h","la"], o "holaholahola".split("o") => ["h","lah","lah","la"]
		//Tendremos dos partes, una con todo el comienzo, la otra tendra lo que sigue despues de la etiqueta <title>
		String[] textoCortadoPor = content.split("<title>");
		
		//Tendremos todo el contenido de <title> en el primer indice del array
		String[] aux = textoCortadoPor[1].split("</title>");

		return aux[0];
	}

	private String getDescription(String content) {
		String[] textoCortadoPor = content.split("<meta name=\"description\" content=\"");
		
		String[] aux = textoCortadoPor[1].split("\">");

		return aux[0];
	}
	
	//Guarda una pagina en la bd dado un url y su contenido
	public void indexAndSaveWebPage(WebPage webPage, String content) {
		String title = getTitle(content);
		String description = getDescription(content);
		
		//Guardamos la página encontrada
		if(description.length()>=200) {
			description = description.substring(0, 50);
		}
		webPage.setDescription(description);
		webPage.setTitle(title);
		
		this.searchServicies.save(webPage);
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
        	System.out.println("Error en getWebCOntent "+e.getMessage());
        }
        return "";
    }

	
	//Si no existen paginas en la bd, se buscaran en la web y se guardaran en la bd
	public void searchPagesInWeb(String query) {
		String domain;
		String content;
		try {
			domain = getURLSearch(query);
//			System.out.println("domain: "+domain);
			content = getPageFromGoogle(query);
			saveLinks(domain,content);
			
			indexWebPages();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	public String getURLSearch(String key) throws UnsupportedEncodingException {
		String url="http://www.google.com/search?q=";
		String charset="UTF-8";
//		String key="java";
		String query = String.format("%s",URLEncoder.encode(key, charset));
		return url+query;
	}
	
	public String getPageFromGoogle(String key) throws MalformedURLException, IOException{

		String url="http://www.google.com/search?q=";
		String charset="UTF-8";
//		String key="jkanime";
		String query = String.format("%s",URLEncoder.encode(key, charset));
		URLConnection con = new URL(url+ query).openConnection();
		//next line is to trick Google who is blocking the default UserAgent
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
		String pag="";
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
//			System.out.println(inputLine);	
			pag = pag+inputLine;
		}

		in.close();
		return pag;
	}
	
	public static String[] getLinks(String wholeThing,int n) throws MalformedURLException, IOException {
	    List<String> strings = new ArrayList<String>();
	    String search = "<a href=\"/url?q=";
	    int stringsFound = 0;
	    int searchChar = search.length();
	    while(stringsFound < n && searchChar <= wholeThing.length()) {
	        if(wholeThing.substring(searchChar - search.length(), searchChar).equals(search)) {
	            int endSearch = 0;
	            while(!wholeThing.substring(searchChar + endSearch, searchChar + endSearch + 4).equals("&amp")) {
	                endSearch++;
	            }
	            strings.add(wholeThing.substring(searchChar, searchChar + endSearch));
	            stringsFound++;
	        }
	        searchChar++;
	    }
	    String[] out = new String[strings.size()];
	    for(int i = 0; i < strings.size(); i++) {
	        out[i] = strings.get(i);
	    }
	    return out;
	}
	
	
}
