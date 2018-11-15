<!DOCTYPE html>
<html lang="es">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">  
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="">
        <link rel="icon" href="assets/icon/favicon.ico">
        <title>Cloud Docs Platform</title>
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.1/css/all.css" integrity="sha384-5sAR7xN1Nv6T6+dT2mhtzEpVJvfS3NScPQTrOxhwjIuvcA67KV2R5Jz6kr4abQsz" crossorigin="anonymous">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
        <link href="assets/css/import.css" rel="stylesheet"/>
           
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
	    
        <script src="assets/js/import.js"></script>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark justify-content-between">
            <a class="navbar-brand" href="#"><i class="fab fa-cloudversify"></i> CloudDocs</a>
            <button class="btn btn-outline-danger my-2 my-sm-0 disconnect-button" type="submit">Desconectar</button>
        </nav>
        <div class="nav-scroller bg-white shadow-sm">
            <nav class="nav nav-underline">
                <a class="nav-link" href="nav.jsp">Documentos</a>
                <a class="nav-link active" href="#">Mis firmas</a>
            </nav>
        </div>

        <div class="container">
            <div class="row">
                <div class="col-md-9 offset-md-1 main">
                    <input id="inputfile" class="d-none" type=file accept=".p12"/>
                    <p class="lead">Mis firmas <button type="button" id="add_sign" class="btn btn-success float-right"><i class="fas fa-plus"></i> Nueva firma PKCS#12</button></p>
                    <div class="table-responsive">
                        <table class="table table-hover  text-muted table-list-file" id="table_sign" ><tbody></tbody></table>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal" id="modal_delete">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="modal_pdf_title">Borrar certificado</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <p class="lead"> ¿Está seguro que desea enviar la petición a TrustedX para eliminar el certificado?</p>
                        <p class="lead"> Este paso será irreversible, recuerde tener una copia de su certificado antes de borrarlo.</p>
                    </div>
                    <div class="modal-footer" id="modal_pdf_footer">
                        <button type="button" id="modal_pdf_sign_btn" class="btn btn-default" data-dismiss="modal">No quiero eliminarlo</button>
                        <button type="button" id="modal_pdf_sign_btn" class="btn btn-danger" onclick="delete_cert()"><i class="fas fa-trash"></i> Si estoy seguro de que quiero elimiarlo</button>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="modal" id="modal_add">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="modal_pdf_title">Añadir certificado</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <p class="lead"> Se procederá a enviar el siguiente certificado y etiquetas:</p>
                        <p><small id="data-pkcs" class="pkcs-text text-muted">No se ha podido cargar</small></p>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <label class="input-group-text">Etiqueta</label>
                            </div>
                            <select class="custom-select" id="labelselect">
                                <option value='uoc,student'>UOC - Estudiante</option>
                            </select>
                        </div>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                              <span class="input-group-text">Contraseña</span>
                            </div>
                            <input type="password" id="passwordcert" class="form-control" placeholder="Contraseña" aria-label="password"/>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                        <button type="button" id="modal_add_sign_btn" class="btn btn-success" data-dismiss="modal" onclick="add_cert()"><i class="far fa-plus-square"></i> Añadir</button>
                    </div>
                </div>
            </div>
        </div>
        <script src="assets/js/common.js"></script>
        <script type="text/javascript">
            list_cert();
            //document.getElementById('inputfile').addEventListener('change', fileselect, false);
            $("#inputfile").change(function(e){
                fileselect(e);
            });
            $('#add_sign').click(function(){
                $('#inputfile').click();
            });
        </script>
    </body>
</html>



