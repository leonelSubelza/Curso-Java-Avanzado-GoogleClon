package com.cursojavaavanzado.cursojavaavanzado.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.cursojavaavanzado.cursojavaavanzado.entities.WebPage;

@Repository
public class SearchRepositoryImpl implements SearchRepository{

	//Carga el obj con la conexion a la bd
	@PersistenceContext
	EntityManager entityManager;//permite conectarse con la bd
	
	//Transactional sirve para la consulta sql, donde hace que todo query que se haga en el metodo se haga toda en una sola consulta
	@Transactional
	@Override
	public List<WebPage> search(String textSearch) {
		//La query es escrita en sql de Hibernate, la tabla debe coincidir con el nombre de la clase
		String query = "FROM WebPage WHERE description LIKE :textSearch";
		
		return this.entityManager
				.createQuery(query)
				.setParameter("textSearch","%"+textSearch+"%")
				.getResultList();
		
	}

	@Transactional
	@Override
	public void save(WebPage pagina) {
		//Hay que comparar que no exista este obj en la bd
//		if(exist(pagina.getUrl())) {
//			return;
//		}
//		
		//Se guarda la pagina en la bd o.O o si existe lo actualiza o.O
		this.entityManager.merge(pagina);
	}

	@Override
	public boolean exist(String link) {
		return getByUrl(link) != null;
	}

	@Transactional
	@Override	
	public WebPage getByUrl(String url) {
		String query = "FROM WebPage WHERE url = :url";
		List<WebPage> list = this.entityManager.createQuery(query).setParameter("url", url).getResultList();
		return list.size() == 0 ? null : list.get(0);
	}

	//El metodo tomara los links que hayan en la bd para actualizar su contenido.Ejecutará 100 por noche
	@Transactional
	@Override
	public List<WebPage> getLinksToIndex() {
		String query = "FROM WebPage WHERE description IS NULL AND title IS NULL";

		return this.entityManager.createQuery(query)
				.setMaxResults(100)
				.getResultList();
	}
	
}
