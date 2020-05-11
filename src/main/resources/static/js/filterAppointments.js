


//Read directly of the input, and change the list with keup
$(document).ready(function(){
    $("#appoSearch").on("keyup", function() {
        var value = $(this).val().toLowerCase();
        let path = window.location.pathname;
        if(path == "/doctor")
            filterAppointmentsName(value);
        else if (path == "/patient/appointment")
            filterAppointmentsMotive(value);
        else
            filterAppointmentsMotive(value,true);
    });
});

function filterAppointmentsMotive(text, bool=false){
    $('.pacients-list').find('.each').each(function(){
        let motive = $(this).find(".appointmen-title-span").text().toLocaleLowerCase();
        let id = $(this).attr("id");
        let appo = id.substring(4);
        if(motive.includes(text.toLocaleLowerCase())){
            if(bool) $("#appo" + appo).show();
            $(this).show();
        }
        else{
            if(bool) $("#appo" + appo).hide();
            $(this).hide();
        }
    });
}

function filterAppointmentsName(text){
    $('.pacients-list').find('.each').each(function(){
        let name = $(this).find(".name-patient-appointment").text().toLocaleLowerCase();
        if(name.includes(text.toLocaleLowerCase())){
            $(this).show();
        }
        else{
            $(this).hide();
        }
    });
}


