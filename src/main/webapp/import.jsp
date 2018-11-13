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
        <link href="assets/css/nav.css" rel="stylesheet"/>
           
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
	    
        <script src="assets/js/import.js"></script>
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
	        	<a class="nav-link" href="nav.jsp">Documentos</a>
	        	<a class="nav-link active" href="#">Mis firmas</a>
	    	</nav>
	    </div>
	    
	    <div class="container">
	      	<div class="row">
		        <div class="col-md-9 offset-md-1 main">
					<p class="lead">
		        	Mis firmas <button type="button" id="sign_btn" class="btn btn-success float-right" data-dismiss="modal" data-document="9812763"><i class="fas fa-plus"></i> Nueva firma PKCS#12</button>
		          	</p>
		          	<div class="table-responsive">
			            <table class="table table-hover  text-muted table-list-file">
			              	<tbody></tbody>
			            </table>
		          	</div>
		        </div>
	    	</div>
		</div>
		<!-- The Modal -->
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
					    <button type="button" id="modal_pdf_sign_btn" class="btn btn-default" data-document="9812763">No quiero eliminarlo</button>
					    <button type="button" id="modal_pdf_sign_btn" class="btn btn-danger" data-document="9812763"><i class="fas fa-trash"></i> Si estoy seguro de que quiero elimiarlo</button>
					</div>
				</div>
			</div>
		</div>
	    <script type="text/javascript">
	    	/*var jsondata = {
				"ccd": "200",
				"data": [
					{
						"type": "cer",
						"id": "19238",
						"title": "FNMT Eduardo Jesús Díaz Dioniz"
					},
					{
						"type": "cer",
						"id": "1231232",
						"title": "FNMT Empleado Público - Eduardo Jesús Díaz Dioniz"
					}
				]
			};

			var jsondata_2 = {
				"ccd": "200",
				"data": [
					{
						"type": "folder",
						"id": "19238",
						"title": "Otros documentos"
					},
					{
						"type": "pdf",
						"id": "1231232",
						"title": "inscripciones.pdf"
					}
				]
			};


	    	$('.folder-btn').click('click', function (event) {
	    		$(this).html("assdasd");
				alert(JSON.stringify($(this)));
			});

	    	$('#sign_btn').on('click', function (event) {
				var button = $(event.relatedTarget) // Button that triggered the modal
				var recipient = button.data('document') // Extract info from data-* attributes
				// If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
				// Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
				alert(JSON.stringify(event));
			});

			$('#modal_pdf').on('show.bs.modal', function (event) {
				var button = $(event.relatedTarget) // Button that triggered the modal
				var recipient = button.data('filename') // Extract info from data-* attributes
				// If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
				// Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
				var modal = $(this)
				modal.find('.modal-title').text('Documento: ' + recipient)
				modal.find('.modal-body input').val(recipient)
			});

	    	$('.disconnect-button').click(function(){
	    		window.location.href = "index.html";
	    		return false;
	    	});
	    	/*$('#myform').on('submit', function(ev) {
			    $('#my-modal').modal({
			        show: 'false'
			    }); 


			    var data = $(this).serializeObject();
			    json_data = JSON.stringify(data);
			    $("#results").text(json_data); 
			    $(".modal-body").text(json_data); 

			    // $("#results").text(data);

			    ev.preventDefault();
			});
			function append_cer(id, title){
				str = '<tr><td class="folder-btn" data-toggle="modal" data-target="#modal_delete" data-id="'+id+'"><i class="fas fa-certificate text-danger"></i> '+title+'<i class="fas fa-trash-alt text-muted float-right"></i></td></tr>';
				return str;
			}
			
			function compose_list(jsondata){
				for (var i = 0; i < jsondata['data'].length; i++){
					$(".table-list-file tbody").append(
						window["append_"+jsondata['data'][i]['type']](jsondata['data'][i]['id'], jsondata['data'][i]['title'])
					);
				}
			};*/

			
			list_cert();
	    </script>




	</body>
</html>



