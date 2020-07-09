const NAME_ATTR = "name";
const SURNAME_ATTR = "surname";

function sort(attribute) {
    let list, button, link;
    list = $("#List > div.slotLista").get();
    button = document.getElementById(`${attribute}SortButton`);
    link = button.getAttribute("href");

    list.sort(((a, b) => compare(a, b,
        attribute === NAME_ATTR ? getName : getSurname,
        link.includes("Desc")))
    );

    button.setAttribute("href",
        link.includes("Asc") ? link.replace("Asc", "Desc") :
            link.replace("Desc", "Asc"));

    button.getElementsByTagName("span")[0].innerHTML = link.includes("Desc") ? "↑" : "↓";

    for (let i = 0; i < list.length; i++) {
        list[i].parentNode.appendChild(list[i]);
    }
}

function compare(a, b, getFunction, isDescending) {
    a = getFunction(a.innerHTML.toLowerCase());
    b = getFunction(b.innerHTML.toLowerCase());

    return isDescending ? b.localeCompare(a) : a.localeCompare(b);
}

function getName(s){
    let i, str;
    i = s.search(">");
    str= s.substr(i + 1);
    str = str.split("<")[0];
    return str;
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