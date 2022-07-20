const botonBusqueda = document.getElementById('boton-search');
const botonLucky = document.getElementById('boton-lucky');
const text = document.getElementById('text');

botonBusqueda.addEventListener('click', () => {
    realizarBusqueda();
});

botonLucky.addEventListener('click', () => {
    realizarBusqueda();
});


function realizarBusqueda(){
    let textSearch = text.value;
    document.location.href = 'results.html?query='+textSearch;
}