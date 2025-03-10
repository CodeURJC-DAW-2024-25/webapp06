let page=0;

async function loadMoreAll(){
    page++;
    let response = await fetch(`/moreProdsAll?page=${page}`);
    let data = await response.text();
    document.getElementById("productsContainer").innerHTML += data;

}