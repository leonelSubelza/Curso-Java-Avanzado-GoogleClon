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

}
