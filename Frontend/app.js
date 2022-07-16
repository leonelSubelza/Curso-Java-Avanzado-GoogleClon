const buscador = document.getElementById('text');

$(document).ready(function() {

    let url = document.location.href;

    //split divide un String dependiendo de que se le pase. Ej "Hola".split("o") -> ['H','la'];
	let aux = url.split('?query=');

    //escribimos en el buscador lo que busco el usuario
    buscador.value = query[1];


    
});



