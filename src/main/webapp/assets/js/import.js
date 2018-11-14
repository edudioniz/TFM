var nav_bar;
var pkcs_upload;

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
    $("#table_sign tbody").html('<i class="fas fa-sync fa-spin"></i> Cargando... ');
}


function fileselect(e) {
    var reader = new FileReader();
    var files = e.target.files;
    f = files[0];
    
    reader.onload = (function(file) {
        return function(e) {
            pkcs_upload = window.btoa(e.target.result);
            $('#data-pkcs').html(pkcs_upload.substring(0, 45)+"...");
            $('#modal_add').modal('toggle');
        };
    })(f);
    reader.readAsBinaryString(f);
}

function add_cert(){
    var params = 
        { pkcs12: pkcs_upload, 
            labels: JSON.stringify($('#labelselect').val().split(',')),
                password: $('#passwordcert').val()};
    $('#inputfile').val("");
    $('#passwordcert').val("");
    $.post("sign?a=add", params)
        .done(function(data) {
            $("#table_sign tbody").html("");
            var json = JSON.parse(data);
            if(json['ccd'] === "200"){
                list_cert();
            }else if(json['ccd'] === "215"){
                alert( "Error al guardar el certificado" );
                list_cert();
            }else if(json['ccd'] === "400"){
                alert( "Hay errores en los datos introducidos" );
                list_cert();
            }else{
                alert( "Error al guardar el certificado" );
                return;
            }
        })
        .fail(function(data) {
            if(data['ccd']===401){
                window.location.href = "index.jsp";
            }else{
                alert( JSON.stringify(data));
            }
        })
        .always(function() {
            pkcs_upload="";
        });
}

function list_cert(){
    resetCertList();
    $.ajax("sign?a=list")
        .done(function(data) {
            $("#table_sign tbody").html("");
            var json = JSON.parse(data);
            if(json['ccd'] === "200"){
                compose_list(json);
            }else if(json['ccd'] === "215"){
                alert( "Error al recuperar los certificados" );
            }else{
                alert( "Error al recuperar los certificados" );
                return;
            }
        })
        .fail(function(data) {
            if(data['ccd']===401){
                window.location.href = "index.jsp";
            }else{
                alert( JSON.stringify(data));
            }
        })
        .always(function() {
            //al cargar todos los archivos
        });
}