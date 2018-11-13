var nav_bar;

function append_cer(id, title){
    str = '<tr><td class="folder-btn" data-toggle="modal" data-target="#modal_delete" data-id="'+id+'"><i class="fas fa-certificate text-danger"></i> '+title+'<i class="fas fa-trash-alt text-muted float-right"></i></td></tr>';
    return str;
}
			
function compose_list(jsondata){
    for (var i = 0; i < jsondata['data'].length; i++){
        
	$(".table-list-file tbody").append(
            window["append_cer"](jsondata['data'][i]['id'], jsondata['data'][i]['description']+" "+JSON.stringify(jsondata['data'][i]['labels']))
	);
    }
    $('.delete-btn').click('click', function (ev) {
        delete_launch(ev);
    });
    $('.cert-btn').click('click', function (ev) {
        cert_launch(ev);
    });
};

function resetCertList(){
    /*var modal = $('#modal_pdf');
    modal.find('.modal-title').html('<i class="fas fa-sync fa-spin"></i> Cargando... ');
    modal.find("#viewer-details").css('background-image', '');      
    modal.find("#viewer-details").removeClass("preview-thumbnail");
    modal.find("#viewer-details").removeClass("no-preview-thumbnail");
    modal.find("#viewer-details").addClass("load-preview-thumbnail");
    modal.find("#viewer-details").html("");*/
}

function list_cert(){
    resetCertList();
    $.ajax("sign?a=list")
        .done(function(data) {
            var json = JSON.parse(data)
            if(json['ccd'] == "200"){
                compose_list(json);
            }else if(json['ccd'] == "215"){
                alert( "Error al recuperar los certificados" );
            }else{
                alert( "Error al recuperar los certificados" );
                return;
            }
        })
        .fail(function(data) {
            if(data['ccd']==401){
                window.location.href = "index.jsp";
            }else{
                alert( JSON.stringify(data));
            }
        })
        .always(function() {
            //al cargar todos los archivos
        });
}