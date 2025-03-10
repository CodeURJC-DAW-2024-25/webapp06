let pageType = 0;


async function loadMoreTypes(type) {
    pageType++;
    let response = await fetch(`/moreProdsTypes?page=${pageType}&type=${type}`);
    let data = await response.text();
    document.getElementById("productsContainer").innerHTML += data;

}