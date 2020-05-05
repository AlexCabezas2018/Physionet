function sortListByName(){
    let list,button, i , switching, b, shouldSwitch, dir, switchCount = 0;
    list = document.querySelector('[id$="List"]');
    button = document.getElementById("nameSortButton");
    button.getElementsByTagName("span")[0].innerHTML = "↑";
    switching = true;
    dir = "asc";
    while(switching) {
        switching = false;
        b = list.getElementsByTagName("a");
        for(i = 0; i < b.length-1; i++) {
            shouldSwitch = false;
            if(dir === "asc") {
                if (b[i].innerHTML.toLowerCase() > b[i + 1].innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            }
            else if(dir === "desc") {
                if (b[i].innerHTML.toLowerCase() < b[i + 1].innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if(shouldSwitch) {
            b[i].parentNode.parentNode.insertBefore(b[i + 1].parentNode, b[i].parentNode);
            switching=true;
            switchCount++;
        }
        else {
            if (switchCount === 0 && dir === "asc") {
                dir = "desc";
                button.getElementsByTagName("span")[0].innerHTML = "↓";
                switching = true;
            }
        }
    }

}

function sortListBySurname(){
    let list,surnameList=[],button, i , switching, b, shouldSwitch, dir, switchCount = 0;
    list = document.querySelector('[id$="List"]');
    button = document.getElementById("surnameSortButton");
    button.getElementsByTagName("span")[0].innerHTML = "↑";
    switching = true;
    dir = "asc";
    while(switching) {
        switching = false;
        b = list.getElementsByTagName("a");
        for(j = 0; j < b.length; j++){
            surnameList[j] = b[j].innerHTML.split(" ");
        }
        for(i = 0; i < b.length-1; i++) {
            shouldSwitch = false;
            if(dir === "asc") {
                if (surnameList[i][1].toLowerCase() > surnameList[i + 1][1].toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            }
            else if(dir === "desc") {
                if (surnameList[i][1].toLowerCase() < surnameList[i + 1][1].toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if(shouldSwitch) {
            b[i].parentNode.parentNode.insertBefore(b[i + 1].parentNode, b[i].parentNode);
            switching=true;
            switchCount++;
        }
        else {
            if (switchCount === 0 && dir === "asc") {
                dir = "desc";
                button.getElementsByTagName("span")[0].innerHTML = "↓";
                switching = true;
            }
        }
    }
}