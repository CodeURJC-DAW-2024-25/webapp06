let page=0;

async function loadMoreAll(){
    page++;

    document.getElementById("loadMoreBtn").style.display = "block";


    let response = await fetch(`/moreProdsAll?page=${page}`);
    let data = await response.text();
    document.getElementById("productsContainer").innerHTML += data;

    if (data.includes("<!--HasMoreElements-->")){
        document.getElementById("loadMoreBtn").style.display = "block";
    }else{
        document.getElementById("loadMoreBtn").style.display = "none";
    }
}