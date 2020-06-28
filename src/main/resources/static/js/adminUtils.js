function sortListByName(){
    let list,button, link;
    list = $("#List > div.slotLista").get();
    button = document.getElementById("nameSortButton");
    link = button.getAttribute("href");

    if(link === "#orderByNameAsc"){
        list.sort(compareAsc);
        button.setAttribute("href", "#orderByNameDesc");
        button.getElementsByTagName("span")[0].innerHTML = "↑";
    }
    else if(link === "#orderByNameDesc") {
        list.sort(compareDesc);
        button.setAttribute("href", "#orderByNameAsc");
        button.getElementsByTagName("span")[0].innerHTML = "↓";
    }

    for (let i = 0; i < list.length; i++) {
        list[i].parentNode.appendChild(list[i]);
    }

}

function compareAsc(a, b){
    a = getName(a.innerHTML.toLowerCase());
    b = getName(b.innerHTML.toLowerCase());
    return a.localeCompare(b);
}

function compareDesc(a, b){
    a = getName(a.innerHTML.toLowerCase());
    b = getName(b.innerHTML.toLowerCase());
    return b.localeCompare(a);
}

function getName(s){
    let i, str;
    i = s.search(">");
    str= s.substr(i + 1);
    str = str.split("<")[0];
    return str;
}

function sortListBySurname(){
    let list,button, link;
    list = $("#List > div.slotLista").get();
    button = document.getElementById("surnameSortButton");
    link = button.getAttribute("href");

    if(link === "#orderBySurnameAsc"){
        list.sort(compareSurAsc);
        button.setAttribute("href", "#orderBySurnameDesc");
        button.getElementsByTagName("span")[0].innerHTML = "↑";
    }
    else if(link === "#orderBySurnameDesc") {
        list.sort(compareSurDesc);
        button.setAttribute("href", "#orderBySurnameAsc");
        button.getElementsByTagName("span")[0].innerHTML = "↓";
    }

    for (let i = 0; i < list.length; i++) {
        list[i].parentNode.appendChild(list[i]);
    }
}

function compareSurAsc(a, b){
    a = getSurname(a.innerHTML.toLowerCase());
    b = getSurname(b.innerHTML.toLowerCase());
    return a.localeCompare(b);
}

function compareSurDesc(a, b){
    a = getSurname(a.innerHTML.toLowerCase());
    b = getSurname(b.innerHTML.toLowerCase());
    return b.localeCompare(a);
}

function getSurname(s){
    let i, str;
    i = s.search(">");
    str= s.substr(i + 1);
    str = str.split("<")[0].split(" ")[1];
    return str;
}


//Function for filtrate doctors and patients
$(document).ready(function(){
    $(".input-search-admin").on("keyup", function() {
        var value = $(this).val().toLowerCase();
        let path = window.location.pathname;
        $('.listaUsuarios').find('.slotLista').each(function(){
            let name = $(this).find(".user-link").text().toLocaleLowerCase();
            if(name.includes(value.toLocaleLowerCase())){
                $(this).show();
            }
            else{
                $(this).hide();
            }
        });
    });
});