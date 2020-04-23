function validateUsername(element, feedback, field) {
    element.setCustomValidity(""); // limpia validaciones previas
    if (element.value.length < 1) {
        element.setCustomValidity("El "+ field +" debe estar relleno");
        feedback.innerHTML ='<span class="badge badge-danger">El '+ field +' debe estar relleno</span>';
        feedback.style.display = "block";
    } else {
        element.setCustomValidity("");
        feedback.style.display = "none"
    }
}

function validateText(element, feedback, field) {
    element.setCustomValidity(""); // limpia validaciones previas
    if (element.value.length < 1) {
        element.setCustomValidity("El "+ field +" debe estar relleno");
        feedback.innerHTML ='<span class="badge badge-danger">El '+ field +' debe estar relleno</span>';
        feedback.style.display = "block";
    } else if (!/^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+$/.test(element.value)) {
        element.setCustomValidity("El "+ field +" debe contener solo letras");
        feedback.innerHTML = '<span class="badge badge-danger">El '+ field +' debe contener solo letras</span>';
        feedback.style.display = "block";
    } else {
        element.setCustomValidity("");
        feedback.style.display = "none"
    }
}

function checkUsername() {
    // TODO revisar llamada allax (ahora mismo no funciona)
    const username = document.getElementById('username');
    let params = {username: username.value};
    console.log(go("/admin/username?username="+ username.value, 'GET', params)
        .then(d => {console.log("usuario creado"); return true;})
        .catch(() => {console.log("usuario duplicado"); return false}));

}

function validatePass() {

    const password = document.getElementById('password');
    const pass2 = document.getElementById('pass2');
    const feedbackPass = document.getElementById('feedbackPass');

    if (pass2.value.localeCompare(password.value) === 0){
        feedbackPass.style.display = "none";
        return true;
    } else {
        feedbackPass.innerHTML = '<span class="badge badge-danger">Las dos contraseñas deben coincidir</span>';
        feedbackPass.style.display = "block";
        return false;
    }
    return false;
}

function validateForm(){
    const feedbackUsername = document.getElementById('feedbackUsername');
    let retUser = false;

    if (!checkUsername()){
        feedbackUsername.innerHTML = '<span class="badge badge-danger">El nombre de usuario ya existe</span>';
        feedbackUsername.style.display = "block";
    } else
        retUser = true;
    return validatePass() && retUser;
}

document.addEventListener("DOMContentLoaded", () => {

    //--------------------------------------------------------------
    // validación de los campos del formulario de añadir usuario
    //--------------------------------------------------------------

    const username = document.getElementById('username');
    const name = document.getElementById('name');
    const surname = document.getElementById('surname');

    const feedbackUsername = document.getElementById('feedbackUsername');
    const feedbackName = document.getElementById('feedbackName');
    const feedbackSurname = document.getElementById('feedbackSurname');

    username.oninput = () => validateUsername(username, feedbackUsername, "Nombre de usuario");
    name.oninput = () => validateText(name, feedbackName, "Nombre");
    surname.oninput = () => validateText(surname, feedbackSurname, "Apellido");

});

// envía json, espera json de vuelta; lanza error si status != 200
function go(url, method, data = {}) {
    let params = {
        method: method, // POST, GET, POST, PUT, DELETE, etc.
        headers: {
            "Content-Type": "application/json; charset=utf-8",
        },
        body: JSON.stringify(data)
    };
    if (method === "GET") {
        // GET requests cannot have body
        delete params.body;
    }
    console.log("sending", url, params)
    return fetch(url, params).then(response => {
        if (response.ok) {
            return data = response.json();
        } else {
            response.text().then(t => {throw new Error(t + ", at " + url)});
        }
    })
}