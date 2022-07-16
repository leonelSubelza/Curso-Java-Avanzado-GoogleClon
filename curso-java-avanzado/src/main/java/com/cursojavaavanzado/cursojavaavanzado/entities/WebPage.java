package com.cursojavaavanzado.cursojavaavanzado.entities;

import javax.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "webpage")
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class WebPage {
	
	@Id //indicamos que la columna será el id de la bd
	@GeneratedValue(strategy = GenerationType.IDENTITY) //El valor será auto-incremental
	@Column(name = "id")
	private Long id;
	
	@Column(name = "url")
	private String  url;
	
	@Column(name = "title")
    private String title;
    
	@Column(name = "description")
    private String description;
}
