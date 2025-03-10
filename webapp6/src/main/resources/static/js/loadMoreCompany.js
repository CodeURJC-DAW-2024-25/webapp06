let pageCompany = 0;


async function loadMoreCompany(companyName) {
    pageCompany++;
    let response = await fetch(`/moreProdsCompany?page=${pageCompany}&company=${companyName}`);
    let data = await response.text();
    document.getElementById("productsContainer").innerHTML += data;

}