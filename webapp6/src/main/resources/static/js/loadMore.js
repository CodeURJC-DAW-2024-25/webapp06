let page=0;

async function loadMore(){
    page++;
    let response = await fetch(`/moreProds?page=${page}`);
    let data = await response.text();
    document.getElementById("productsContainer").innerHTML += data;

}