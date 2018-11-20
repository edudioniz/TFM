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
import java.io.ByteArrayInputStream;
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
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
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
    //private final static String ROUTE_TEMP="/tmp/";
    private final static String KEYSTORE="E:\\clouddocstruststore.jks";
    //private final static String KEYSTORE="/opt/clouddocstruststore.jks";
    private final static String PASSWORD="123456";
    
    private final static String REASONSIGN = "CloudDocs Sign Platform";
    private final static String LOCATIONSIGN = "Firmado electrónicamente con TrustedX";
    
    //String createPDF(String route, String binary) {
        /*String ret = "";
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
                appearance.setReason("CloudDocs Sign Platform"); 
                appearance.setLocation("Firmado electrónicamente con TrustedX"); 
                appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, "sig"); 

                ExternalSignature es = new Signature(Base64.getDecoder().decode(binary)); 
                
                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                ks.load(new FileInputStream(KEYSTORE), PASSWORD.toCharArray());*/
                
                /*PKIXParameters params = new PKIXParameters(ks);
                Iterator it = params.getTrustAnchors().iterator();
                while( it.hasNext() ) {
                    TrustAnchor ta = (TrustAnchor)it.next();
                    // Get certificate
                    X509Certificate cert = ta.getTrustedCert();
                    System.out.println(cert);
                }*/
                
                /*String alias = (String)ks.aliases().nextElement();
                System.out.println(alias+" certificado");
                Certificate cert = ks.getCertificate(alias);
                
                String alias2 = (String)ks.aliases().nextElement();
                System.out.println(alias2+" certificado");
                Certificate cert2 = ks.getCertificate(alias2);
                
                MakeSignature.signDetached(appearance, new BouncyCastleDigest(), es, new Certificate []{cert, cert2}, null, null, null, 0, CryptoStandard.CMS); */
                /*
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
        return ret;*/
                //return "";
    //}

    public String getHashFromPre(String route) {
        try{
            String file = ROUTE_TEMP+route;
            InputStream input = new FileInputStream(new File(file));
            MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[4096];
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

    public String signPDF(String filename, String identity, String token_nav, String token_sign) throws IOException, DocumentException, GeneralSecurityException {
        
        System.out.println("--sign--");
        System.out.println(filename);
        System.out.println("--------");
        
        
        String file = ROUTE_TEMP.concat(filename);
        String [] n = file.split("\\.");
        String n_comp = "";
        for (int i=0; i<n.length-1;i++) {
            n_comp+=n[i]+".";
        }
        String namefile = n_comp.substring(0, n_comp.length()-1);
        String extension = n[n.length-1];
        
        String signedfilename = namefile+"_sign."+extension;
                
        PdfReader reader = new PdfReader(file); 
        FileOutputStream os = new FileOutputStream(signedfilename); 
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0'); 

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance(); 
        appearance.setReason(REASONSIGN); 
        appearance.setLocation(LOCATIONSIGN); 
        appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, "sig"); 

        ExternalSignature es = new TrustedXSignature(identity,token_sign); 
        
        TrustedX trustedX = new TrustedX();
        trustedX.init();
        byte[] certbyte = trustedX.getCertificateByte(identity, token_nav);
        
        System.out.println("-------MI-CERTIFICADO---------");
        System.out.println(new String(Base64.getEncoder().encode(certbyte)));
        System.out.println(certbyte.length);
        System.out.println("----------------");
        
        InputStream is = new ByteArrayInputStream(certbyte);
        
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(is);
        
        ExternalDigest dig = new BouncyCastleDigest();
        
        MakeSignature.signDetached(appearance, dig, es, new Certificate[]{cert}, null, null, null, 0, CryptoStandard.CADES); 
        
        return signedfilename;
    }
    
    
    //return new String(Base64.getEncoder().encode(sha1.digest()));
}
