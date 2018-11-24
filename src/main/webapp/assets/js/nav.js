var nav_obj_hash;

var nav_bar;
var file_servlet;
var type_file_servlet;

//cert
function force_sign(ev){
    ev.preventDefault();
    $('#modal_sign_title').text(ev.target.getAttribute('data-filename'));
    list_cert();
    $('#modal_pdf_ultimate_sign_btn').attr("data-route", ev.target.getAttribute('data-route'));
    $('#modal_sign').modal('show');
    
    //modal_sign_cert
}

//file
function file_launch(ev){
    load_file(ev.target.getAttribute('data-route'), 
    ev.target.getAttribute('data-filename'), 
    ev.target.getAttribute('data-id'));
}
function force_download(ev){
    ev.preventDefault();
    download_file(ev.target.getAttribute('data-route'));
}

// navegable
function navigator_launch(ev){
    load_data(ev.target.getAttribute('data-route'));
}
function navigator_launch_with_trace(ev){
    /*if(ev.target.getAttribute('data-index')!==null && ev.target.getAttribute('data-title')<nav_obj_hash.length-1){
        console.log(JSON.stringify(nav_obj_hash))
        console.log(nav_obj_hash.length);
        console.log(ev.target.getAttribute('data-index'));
        for(var i=0; i<=(nav_obj_hash.length-ev.target.getAttribute('data-index'));i=i+1 ){
            console.log("remove");
            nav_obj_hash.splice(-1,1);
        }
        console.log("size: "+nav_obj_hash.length);
        load_data(ev.target.getAttribute('data-route'));
        console.log(JSON.stringify(nav_obj_hash));
    }else{
        if(ev.target.getAttribute('data-route')!==""){
            nav_obj_hash.push({title : ev.target.getAttribute('data-title'), route:ev.target.getAttribute('data-route')});
        }else{
            clear_navbar();
            nav_obj_hash = [];
        }
        load_data(ev.target.getAttribute('data-route'));
    }*/
    load_data(ev.target.getAttribute('data-route'));
}

function set_navbar(jsondata){
    $("#nav_list ol").append(
        window["append_navbar_type_"+jsondata['origin']]()
    );
    var data = jsondata['path'].split("/");
    
    if(data.length > 1){
        for (var i = 1; i < data.length-1; i++){
            $("#nav_list ol").append(
                append_navbar_route(data[i], compose_route(i, data))
            );
        }
        $("#nav_list ol").append(
            append_navbar_route_last(data[data.length-1])
        );
    }
    $('.nav-btn').click('click', function (ev) {
        ev.preventDefault();
        navigator_launch(ev);
    });
}
function set_navbar_by_hash(jsondata){
    
    
    if(nav_obj_hash.length > 0){
        $("#nav_list ol").prepend(
            append_navbar_route_last(nav_obj_hash[0]['title'])
        );
        
        for (var i = 1; i < nav_obj_hash.length; i++){
            $("#nav_list ol").prepend(
                append_navbar_route(nav_obj_hash[i]['title'], nav_obj_hash[i]['route'], i+1)
            );
        }
        
    }
    $("#nav_list ol").prepend(
        window["append_navbar_type_"+jsondata['origin']]()
    );
    
    $('.nav-btn').click('click', function (ev) {
        ev.preventDefault();
        navigator_launch_with_trace(ev);
    });
}
function compose_route(index, data){
    str = "";
    for (var i = 1; i <= index; i++){
        str +="/"+data[i];
    }
    return str;
}
function clear_navbar(){
    $("#nav_list ol").html("");
}
function append_navbar_type_drive(){
    str = '<li class="breadcrumb-item"><a href="" class="nav-btn" data-route=""><i class="fab fa-google-drive"></i> Google Drive</a></li>';
    return str;
}
function append_navbar_type_dropbox(){
    str = '<li class="breadcrumb-item"><a href="" class="nav-btn" data-route=""><i class="fab fa-dropbox"></i> Dropbox</a></li>';
    return str;
}
function append_navbar_route(title, route, i=null){
    str = '<li class="breadcrumb-item" class="nav-btn"><a href="" class="nav-btn" data-route="'+route+'" data-index="'+i+'"><i class="far fa-folder-open"></i> '+title+'</a></li>';
    return str;
}
function append_navbar_route_last(title){
    str = '<li class="breadcrumb-item active" aria-current="page"><i class="far fa-folder-open"></i> '+title+'</li>';
    return str;
}

// fila tablas
function append_cer(id, title, route){
    str = '<tr><td class="cert-btn" data-id="'+id+'" data-title="'+title+'"><i class="fas fa-certificate text-danger"></i> '+title+'</td></tr>';
    return str;
}
function append_folder(id, title, route){
    str = '<tr><td class="folder-btn" data-id="'+id+'" data-title="'+title+'" data-route="'+route+'"><i class="far fa-folder"></i> '+title+'</td></tr>';
    return str;
}
function append_pdf(id, title, route){
    str = '<tr><td class="file-btn" data-type="pdf" data-toggle="modal" data-target="#modal_pdf" data-filename="'+title+'" data-id="'+id+'" data-title="'+title+'" data-route="'+route+'"><i class="far fa-file-pdf"></i> '+title+'</td></tr>';
    return str;
}
function append_file(id, title, route){
    str = '<tr><td class="file-btn" data-type="other" data-toggle="modal" data-target="#modal_pdf" data-filename="'+title+'" data-id="'+id+'" data-title="'+title+'" data-route="'+route+'"><i class="far fa-file"></i> '+title+'</td></tr>';
    return str;
}
function append_cert_select(id, title){
    str = '<option value="'+id+'">'+title+'</option>';
    return str;
}
function set_loader(){
    $("#nav_list ol").html(
        '<tr><td><i class="fas fa-sync fa-spin"></i> Cargando</td></tr>'
    );
}

function clear_list(){
    $("#list_file tbody").html("");
}
			
function compose_list(jsondata, destiny="#list_file tbody"){
    for (var i = 0; i < jsondata['data'].length; i++){
        id = jsondata['data'][i]['id'];
        title = unescape(jsondata['data'][i]['title']);
        route = jsondata['data'][i]['route'];
        $(destiny).append(
            window["append_"+jsondata['data'][i]['type']](id, title, route)
        );
    }
    if(type_file_servlet==="hash"){
        $('.folder-btn').click('click', function (ev) {
            navigator_launch_with_trace(ev);
        });
    }else{
        $('.folder-btn').click('click', function (ev) {
           navigator_launch(ev);
        });
    }
    $('.file-btn').click('click', function (ev) {
        file_launch(ev);
    });
    
};

function resetModalPDF(){
    var modal = $('#modal_pdf');
    modal.find('.modal-title').html('<i class="fas fa-sync fa-spin"></i> Cargando... ');
    modal.find("#viewer-details").css('background-image', '');      
    modal.find("#viewer-details").removeClass("preview-thumbnail");
    modal.find("#viewer-details").removeClass("no-preview-thumbnail");
    modal.find("#viewer-details").addClass("load-preview-thumbnail");
    modal.find("#viewer-details").html("");
}
       
// HTTP request
function load_data(path, servlet=null, type=null){
    if(servlet !== null){
        file_servlet = servlet;
    }
    if(type !== null){
        type_file_servlet = type;
    }
    set_loader();
    $.ajax( file_servlet+"?a=nav&path="+path )
        .done(function(data) {
            clear_navbar();
            if(type_file_servlet === "hash"){
                if(JSON.parse(data)['parent']!==null && JSON.parse(data)['parent'].length > 0){
                   nav_obj_hash = JSON.parse(data)['parent'];
                }
                set_navbar_by_hash(JSON.parse(data));
            }else{
                set_navbar(JSON.parse(data));
            }
            
            clear_list();
            compose_list(JSON.parse(data));
        })
        .fail(function(data) {
            if(data['status']==401){
                window.location.href = "index.jsp";
            }else{
                alert( JSON.stringify(data));
            }
        })
        .always(function() {
            //al cargar todos los archivos
        });
}
function load_file(route, filename, id){
    resetModalPDF();
    $.ajax( file_servlet+"?a=nav&path="+route )
        .done(function(data) {
            var json = JSON.parse(data)
            var modal = $('#modal_pdf')
            modal.find('.modal-title').text('Documento: ' + filename);
            
            $('#modal_pdf_download_btn').prop("disabled", false);
            $('#modal_pdf_download_btn').attr("data-id", id);
            $('#modal_pdf_download_btn').attr("data-route", route);
            $('#modal_pdf_download_btn').attr("data-filename", filename);
            
            $('#modal_pdf_sign_btn').prop("disabled", false);
            $('#modal_pdf_sign_btn').attr("data-id", id);
            $('#modal_pdf_sign_btn').attr("data-route", route);
            $('#modal_pdf_sign_btn').attr("data-filename", filename);
            
            if(json['ccd'] == "210"){
                modal.find("#viewer-details").removeClass("load-preview-thumbnail");
                modal.find("#viewer-details").addClass("preview-thumbnail");
                modal.find("#viewer-details").css('background-image', 'url(/prefile?' + (new Date()).getTime() + ')');
            }else if(json['ccd'] == "215"){
                modal.find("#viewer-details").removeClass("load-preview-thumbnail");
                modal.find("#viewer-details").addClass("no-preview-thumbnail");
            }else{
               alert( "Error al recuperar el fichero" );
               return;
            }
            
            
        })
        .fail(function(data) {
            if(data['status']==401){
                window.location.href = "index.jsp";
            }else{
                alert( JSON.stringify(data));
            }
        })
        .always(function() {
            //al cargar todos los archivos
        });
}
function download_file(route){
    $('#modal_pdf_download_btn').prop("disabled", true);
    $.ajax( file_servlet+"?a=download&path="+route )
        .done(function(data) {
            var json = JSON.parse(data);
            if(json['ccd'] == "200"){
                window.open(json['url']);
            }else{
               alert( "Error al descargar el fichero" );
               return;
            }
        })
        .fail(function(data) {
            if(data['status']==401){
                window.location.href = "index.jsp";
            }else{
                alert( JSON.stringify(data));
            }
        })
        .always(function() {
            $('#modal_pdf_download_btn').prop("disabled", false);
        });
}
function sign_file(route,id){
    $('#modal_pdf_ultimate_sign_btn').html('<i class="fas fa-sync fa-spin"></i> Firmando... ');
    download_to_sign(route, function(filename){
        
        var call = "";
        if(type_file_servlet==="hash"){
            call = nav_obj_hash[0]['route'];
        }else{
            call = route;
        }
        
        $('#modal_pdf_sign_btn').prop("disabled", true);
        $.ajax( "sign?a=clientsign&callback="+call+"&id="+id+"&fileid="+route+"&filename="+filename )
            .done(function(data) {
                var json = JSON.parse(data);
                if(json['ccd'] == "200"){
                    window.location.href = json['url'];
                }else{
                   alert( "Error al firmar el fichero" );
                   return;
                }
            })
            .fail(function(data) {
                if(data['status']==401){
                    window.location.href = "index.jsp";
                }else{
                    alert( JSON.stringify(data));
                }
            })
            .always(function() {
                $('#modal_pdf_sign_btn').prop("disabled", false);
            });
    });
}
function list_cert(){
    $("#modal_sign_cert").html("");
    $('#modal_pdf_ultimate_sign_btn').prop("disabled", true);
    $.ajax("sign?a=list")
        .done(function(data) {
            var json = JSON.parse(data)
            if(json['ccd'] == "200"){
                for(i=0;i<json['data'].length;i++){
                    $("#modal_sign_cert").append(append_cert_select(json['data'][i]['id'], json['data'][i]['description']));
                }
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
    
            $('#modal_pdf_ultimate_sign_btn').prop("disabled", false);
        });
}
function download_to_sign(route, callback){
    $.ajax(file_servlet+"?a=downloadToSign&path="+route)
        .done(function(data) {
            var json = JSON.parse(data);
            if(json['ccd'] === '200'){
                callback(json['data']['filename']);
            }else{
               alert( "Error al firmar el fichero" );
               return;
            }
        })
        .fail(function(data) {
            if(data['status']==401){
                window.location.href = "index.jsp";
            }else{
                alert( JSON.stringify(data));
                return;
            }
        });
}

function init(route, servlet, type){
    nav_obj_hash = [];
    $('#modal_pdf_download_btn').click(function(ev){
        ev.preventDefault();
        force_download(ev);
    });
    $('#modal_pdf_sign_btn').click(function(ev){
        ev.preventDefault();
        force_sign(ev);
    });
    $('#modal_pdf_ultimate_sign_btn').click(function(ev){
        ev.preventDefault();
        var route = ev.target.getAttribute('data-route');
        var identity = $('#modal_sign_cert').val();
        sign_file(route,identity);
    });
    load_data(route,servlet, type);
}