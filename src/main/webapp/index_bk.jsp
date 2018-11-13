<!doctype html>
<html lang="en">
	<head>
	    <meta charset="utf-8">
	    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	    <meta name="description" content="">
	    <meta name="author" content="">
	    <link rel="icon" href="assets/icon/favicon.ico">

	    <title>Cloud Docs Platform</title>

            
            <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="https://apis.google.com/js/platform.js" async defer></script>

<meta name="google-signin-scope" content="profile email">
<meta name="google-signin-client_id"
     content="247410889511-svsohn3f0vucpjvrueesdvv4v6srhjnh.apps.googleusercontent.com">
            
	    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
	    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.1/css/all.css" integrity="sha384-5sAR7xN1Nv6T6+dT2mhtzEpVJvfS3NScPQTrOxhwjIuvcA67KV2R5Jz6kr4abQsz" crossorigin="anonymous">
	    <link href="assets/css/index.css" rel="stylesheet">
  </head>

  	<body class="text-center">
	    <div class="cover-container d-flex h-100 p-3 mx-auto flex-column">
		    <header class="masthead mb-auto">
		    	<div class="inner">
		        	<h3 class="masthead-brand">
		        		<i class="fab fa-cloudversify"></i> CloudDocs
		        	</h3>
		        	<nav class="nav nav-masthead justify-content-center">
		            	<!--<a class="nav-link active" href="#">CloudDocs</a>-->
		            	<a class="nav-link" href="privacity.html">Condiciones de uso</a>
		        	</nav>
		        </div>
		    </header>

	      	<main role="main" class="inner cover">
	        	<h1 class="cover-heading">¡Firma tus documentos!</h1>
	        	<p class="lead">Plataforma de firma de documentos con dispositivos de firma en la nube. Sube tu firma a TrustedX y firma tus documentos en la nube.</p>
	        	<p class="lead">
	          		<div class="btn-group btn-group-lg" role="group" aria-label="Botones de Autentificacion">
				  		<button type="button" class="btn btn-secondary auth-button"><i class="fab fa-google-drive"></i> Google</button>
				  		<button type="button" class="btn btn-secondary auth-button"><i class="fab fa-dropbox"></i> Dropbox</button>
					</div>
	        	</p>
	      	</main>
	      	<footer class="mastfoot mt-auto">
	        	<div class="inner">
	        		<p>Proyecto fin de máster.</p>
	        	</div>
	    	</footer>
	    </div>
   
	<div class="g-signin2" data-onsuccess="onSignIn"></div>

  <script>
      //google callback. This function will redirect to our login servlet
      function onSignIn(googleUser) {
         var profile = googleUser.getBasicProfile();
         console.log('ID: ' + profile.getId());
         console.log('Name: ' + profile.getName());
         console.log('Image URL: ' + profile.getImageUrl());
         console.log('Email: ' + profile.getEmail());
         console.log('id_token: ' + googleUser.getAuthResponse().id_token);

         //do not post all above info to the server because that is not secure.
         //just send the id_token

         var redirectUrl = 'login';

         //using jquery to post data dynamically
         var form = $('<form action="' + redirectUrl + '" method="post">' +
                          '<input type="text" name="id_token" value="' +
                           googleUser.getAuthResponse().id_token + '" />' +
                                                                '</form>');
         $('body').append(form);
         form.submit();
      }

   </script>

		<!-- Latest compiled and minified JavaScript -->
		<script src="http://code.jquery.com/jquery-latest.min.js"
        type="text/javascript"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>


		<script type="text/javascript">
	    	$('.auth-button').click(function(){
	    		window.location.href = "nav.jsp";
	    		return false;
	    	});
	    </script>
	</body>
</html>
