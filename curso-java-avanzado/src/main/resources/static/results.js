const buscador = document.getElementById('text');
const logo = document.getElementById('logo');
const botonBorrar = document.getElementById('boton-borrar');
const botonBuscar = document.getElementById('boton-buscar');

jQuery(document).ready(function($) {
	realizarBusqueda();
});

buscador.addEventListener('keydown',() => {
	if(buscador.value.length>0){
		botonBorrar.classList.add('activo');
		return;
	}
	if(buscador.value==''){
		botonBorrar.classList.remove('activo');
		return;
	}
	
});

botonBorrar.addEventListener('click', () => {
	buscador.value = '';
	botonBorrar.classList.remove('activo');
});

botonBuscar.addEventListener('click', () => {
	let textSearch = text.value;
    document.location.href = 'results.html?query='+textSearch;

});

logo.addEventListener('click',() => {
	document.location.href = 'index.html';
})

const realizarBusqueda = function(){
	let url = document.location.href;

	//split divide un String dependiendo de que se le pase. Ej "Hola".split("o") -> ['H','la'];
	let aux = url.split('?query=');
	let query = aux[1];
	//escribimos en el buscador lo que busco el usuario
	buscador.value = query;	
	console.log(query)
	fetch('http://localhost:8080/api/search?query=' + query)
		.then(response => response.json())
		.then(json => {
			console.log(json);

			let contenedor = document.getElementById('results');
			for (let resultSearch of json) {
				let notice = getResultSearch(resultSearch);
				contenedor.innerHTML += notice;
			}
		});
}

function getResultSearch(resultSearch) {
	let titulo = '';
	let descr = '';

	if (resultSearch.title == null) {
		titulo = resultSearch.url;
		descr = '';
	} else {
		titulo = resultSearch.title;
		descr = resultSearch.description;
	}

	return `
    <div class='notice'>
        <h3><a href='${resultSearch.url}' target='_blank'>${titulo}</a></h3>
        <span>${descr}</span>
    </div>`;
}