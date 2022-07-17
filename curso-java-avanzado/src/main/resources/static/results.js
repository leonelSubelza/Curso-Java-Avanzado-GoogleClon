const buscador = document.getElementById('text');

jQuery(document).ready(function($){
    let url = document.location.href;

    //split divide un String dependiendo de que se le pase. Ej "Hola".split("o") -> ['H','la'];
	let aux = url.split('?query=');
    let query  = aux[1];
    //escribimos en el buscador lo que busco el usuario
    buscador.value = query;
    console.log(query)
    fetch('http://localhost:8080/api/search?query='+query)
    .then(response => response.json())
    .then(json => {
        console.log(json);

        /*
	var newDiv = document.createElement("div");
	newDiv.id = "span";
	var respuesta = document.getElementById("respuestas");
	respuesta.appendChild(newDiv);
        */



        let contenedor = document.getElementById('results');
        for(let resultSearch of json){
            let notice = getResultSearch(resultSearch);
            contenedor.innerHTML += notice;
        }
        //document.getElementById('results').outerHTML += html;
    });


function getResultSearch(resultSearch){
    return `
    <div class='notice'>
        <h3><a href='${resultSearch.url}' target='_blank'>${resultSearch.title}</a></h3>
        <span>${resultSearch.description}</span>
    </div>`;
}

});
