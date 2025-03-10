let pageType = 0;


async function loadMoreTypes() {
    pageType++;
    let response = await fetch(`/moreProdsTypes?page=${pageType}`);
    let data = await response.text();
    document.getElementById("productsContainer").innerHTML += data;

}