/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.common;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;


/**
 *
 * @author Eduardo
 */
public class iText {
    
    private final static String ROUTE_TEMP="E:\\";
    private final static String KEYSTORE="E:\\clouddocstruststore.jks";
    private final static String PASSWORD="123456";
    
    String createPDF(String route, String binary) {
        String ret = "";
        try {
            String file = ROUTE_TEMP.concat(route);
            
            String [] n = file.split("\\.");
            String n_comp = "";
            for (int i=0; i<n.length-1;i++) {
                n_comp+=n[i]+".";
            }
            String namefile = n_comp.substring(0, n_comp.length()-1);
            String extension = n[n.length-1];
            
            if(extension.equals("pdf")){
                ret = namefile+"_sign."+extension;
                
                
                FileUtils.copyFile(new File(file), new File(ret));
                
                
                
                //FileUtils.copyFile(new File(file), new File(ret));
                
                PdfReader reader = new PdfReader(file); 
                FileOutputStream os = new FileOutputStream(ret); 
                PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0'); 

                // appearance 
                PdfSignatureAppearance appearance = stamper.getSignatureAppearance(); 
                appearance.setReason("External hash example"); 
                appearance.setLocation("Foobar"); 
                appearance.setVisibleSignature(new Rectangle(72, 732, 144, 780), 1, "sig"); 

                ExternalSignature es = new Signature(Base64.getDecoder().decode(binary)); 
                
                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                ks.load(new FileInputStream(KEYSTORE), PASSWORD.toCharArray());
                
                // This class retrieves the most-trusted CAs from the keystore
                PKIXParameters params = new PKIXParameters(ks);

                // Get the set of trust anchors, which contain the most-trusted CA certificates
                Iterator it = params.getTrustAnchors().iterator();
                while( it.hasNext() ) {
                    TrustAnchor ta = (TrustAnchor)it.next();
                    // Get certificate
                    X509Certificate cert = ta.getTrustedCert();
                    System.out.println(cert);
                }
                
                String alias = (String)ks.aliases().nextElement();
                System.out.println(alias+" certificado");
                System.out.println(alias+" certificado");
                System.out.println(alias+" certificado");
                
                Certificate cert = ks.getCertificate(alias);
                
                
                MakeSignature.signDetached(appearance, new BouncyCastleDigest(), es, new Certificate []{cert}, null, null, null, 0, CryptoStandard.CMS); 
                
            }else{
                ret = namefile+".pem";
                FileWriter fw=new FileWriter(ret);
                fw.write("-----BEGIN CERTIFICATE-----\n");
                fw.write(binary+"\n");
                fw.write("-----END CERTIFICATE-----");
                fw.flush();
                fw.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public String getHashFromPre(String route) {
        try{
            String file = ROUTE_TEMP+route;
            InputStream input = new FileInputStream(new File(file));
            MessageDigest sha1 = MessageDigest.getInstance("SHA-512");
            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            return new String(Base64.getEncoder().encode(sha1.digest()));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    
}
