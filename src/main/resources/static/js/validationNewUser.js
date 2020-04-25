function checkUsername(username) {
    // TODO revisar llamada allax (ahora mismo no funciona)
    let params = {username: username};
    (go("/admin/username?username="+ username, 'GET', params)
        .then(d => {console.log("usuario creado"); return true;})
        .catch(() => {console.log("usuario duplicado"); return false}));

}

document.addEventListener("DOMContentLoaded", () => {

    //--------------------------------------------------------------
    // validación de los campos del formulario de añadir usuario
    //--------------------------------------------------------------

    const username = document.getElementById('username');
    const name = document.getElementById('name');
    const surname = document.getElementById('surname');
    const password = document.getElementById('password');
    const pass2 = document.getElementById('pass2');

    const feedbackUsername = document.getElementById('feedbackUsername');
    const feedbackName = document.getElementById('feedbackName');
    const feedbackSurname = document.getElementById('feedbackSurname');
    const feedbackPass = document.getElementById('feedbackPass');

    addInputEvents(username, feedbackUsername);
    addInputEvents(name, feedbackName);
    addInputEvents(surname, feedbackSurname);
    addInputEvents(pass2, feedbackPass);
    addInvalidEvents(username, name, surname, password, pass2, feedbackUsername, feedbackName, feedbackSurname, feedbackPass);

});

const addInputEvents = (element, feedback) => {
    element.addEventListener('input', () => {
        element.setCustomValidity('');
        feedback.hasChildNodes() ? feedback.firstElementChild.style.display = "none" : "";
        element.checkValidity();
    });
}

const addInvalidEvents = (username, name, surname, password, pass2, feedbackUsername, feedbackName, feedbackSurname, feedbackPass) => {
    username.addEventListener('invalid', () => {
        if(username.value === '') {
            username.setCustomValidity('El Nombre de Usuario debe estar relleno');
            feedbackUsername.innerHTML = errorBadge('El Nombre de Usuario debe estar relleno');
        } else {
            checkUsername(username.value);
            console.log("COMPROBANDO USER");
        }
    });

    username.addEventListener('focusout', () => {
        checkUsername(username.value);
        console.log("COMPROBANDO USER");
    });

    name.addEventListener('invalid', () => {
        if(name.value === '') {
            name.setCustomValidity('El Nombre debe estar relleno');
            feedbackName.innerHTML = errorBadge('El Nombre debe estar relleno');
        } else {
            name.setCustomValidity('El Nombre solo puede contener letras');
            feedbackName.innerHTML = errorBadge('El Nombre solo puede contener letras');
        }
    });

    surname.addEventListener('invalid', () => {
        if(surname.value === '') {
            surname.setCustomValidity('El Apellido debe estar relleno');
            feedbackSurname.innerHTML = errorBadge('El Apellido debe estar relleno');
        } else {
            surname.setCustomValidity('El Apellido solo puede contener letras');
            feedbackSurname.innerHTML = errorBadge('El Apellido solo puede contener letras');
        }
    });

    pass2.addEventListener('focusout', () => {
        if (pass2.value.localeCompare(password.value) !== 0){
            pass2.setCustomValidity('Las contraseñas deben coincidir');
            feedbackPass.innerHTML = errorBadge('Las contraseñas deben coincidir');
        }
    });
}

const errorBadge = (text) => {
    return ('<span class="badge badge-danger">'+text+'</span>');
}

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