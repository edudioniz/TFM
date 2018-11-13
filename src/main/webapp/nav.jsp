<%@page import="es.ediaz.core.*"%>

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
  	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
  	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
        <link href="assets/css/nav.css" rel="stylesheet"/>
        
        <script src="assets/js/nav.js"></script>
    </head>

    <body>
  	<nav class="navbar navbar-expand-lg navbar-dark bg-dark justify-content-between">
            <a class="navbar-brand" href="#">
		<i class="fab fa-cloudversify"></i> CloudDocs
            </a>
            <button class="btn btn-outline-danger my-2 my-sm-0 disconnect-button" type="submit">Desconectar</button>
	</nav>
	<div class="nav-scroller bg-white shadow-sm">
            <nav class="nav nav-underline">
	       	<a class="nav-link active" href="nav.jsp">Documentos</a>
	       	<a class="nav-link" href="import.jsp">Mis firmas</a>
	    </nav>
	</div>
	    
	<div class="container">
            <div class="row">
	        <div class="col-md-9 offset-md-1 main">
                    <nav aria-label="breadcrumb" id="nav_list">
		  	<ol class="breadcrumb"></ol>
                    </nav>
		    <div class="table-responsive">
		        <table class="table table-hover  text-muted table-list-file" id="list_file">
                            <tbody></tbody>
		        </table>
                    </div>
		</div>
	    </div>
        </div>

	<!-- The Modal PDF viewer -->
        <div class="modal" id="modal_pdf">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="modal_pdf_title"></h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body" style="background-color: #333">
                        <div id="viewer-details"></div>
                        <div id="cert-details"></div>
                    </div>
                    <div class="modal-footer" id="modal_pdf_footer">
                        <button type="button" id="modal_pdf_download_btn" class="btn btn-success" data-id="" disabled data-dismiss="modal"><i class="fas fa-cloud-download-alt"></i> Descargar</button>
                        <button type="button" id="modal_pdf_sign_btn" class="btn btn-primary" data-id="" disabled data-dismiss="modal"><i class="fas fa-signature"></i> Firmar con TrustedX</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- The Modal SIGNER-->
	<div class="modal" id="modal_sign">
            <div class="modal-dialog modal-lg">
		<div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="modal_pdf_title">Proceso de firmado</h4>
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="document">Documento a firmar</label>
                            <div class="alert alert-secondary" role="list"><i class="far fa-file"></i> <span id="modal_sign_title"></span></div>
                        </div>
                        <hr>
                        <div class="form-group">
                            <label for="sign">Firma</label>
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <label class="input-group-text" for="certificate">Certificado:</label>
                                </div>
                                <select class="custom-select" id="modal_sign_cert"></select>
                            </div>
                        </div>

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                        <button type="button" id="modal_pdf_ultimate_sign_btn" class="btn btn-danger"><i class="fas fa-signature"></i> Firmar con TrustedX</button>
                    </div>
		</div>
            </div>
	</div>
        
        <script src="assets/js/common.js"></script>
	<script type="text/javascript">
            <%
                String callback = "";
                if(request.getParameter("callback")!=null){
                    callback = request.getParameter("callback");
                }
            %>
            init("<%= callback %>", "<%= request.getSession(false).getAttribute("store_servlet") %>");
	</script>
        

    </body>
</html>



