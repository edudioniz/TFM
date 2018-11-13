<!DOCTYPE html>
<html>
<head>
	<title></title>
</head>
<body>

<script src="https://cdnjs.cloudflare.com/ajax/libs/forge/0.7.6/forge.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>



<input id="upload" type=file accept=".p12" size=30>
<textarea class="form-control" rows=35 cols=120 id="log"></textarea>

<script>
function handlePFXFile(e) {
    var p12Der = e.target.result;
    var p12Asn1 = forge.asn1.fromDer(p12Der);
    var p12 = forge.pkcs12.pkcs12FromAsn1(p12Asn1, 'trustedx');
         
    var keyBags = p12.getBags({bagType: forge.pki.oids.pkcs8ShroudedKeyBag});
    var bag = keyBags[forge.pki.oids.pkcs8ShroudedKeyBag][0];
    var privateKey = bag.key;
        
    var asn1 = forge.pki.privateKeyToAsn1(privateKey);
    var der = forge.asn1.toDer(asn1);
    var b64key = forge.util.encode64(der.getBytes()); //same with PEM above, but without headers.
        
    console.log(b64key);    
}

function otra(a){
    console.log(window.btoa(a.target.result));  
}

function handleFileSelect(evt) {
    var reader = new FileReader();
    var files = evt.target.files;
    f = files[0];
    
        reader.onload = (function(theFile) {
        return function(e) {
            //handlePFXFile(e);
            otra(e);
        };
    })(f);
      reader.readAsBinaryString(f);
}
document.getElementById('upload').addEventListener('change', handleFileSelect, false);

</script>
</body>
</html>