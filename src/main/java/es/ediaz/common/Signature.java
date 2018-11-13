/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.common;

import com.itextpdf.text.pdf.security.ExternalSignature;
import java.security.GeneralSecurityException;

class Signature implements ExternalSignature { 
    byte[] signedBytes; 

    Signature(byte[] signedBytes) { 
        this.signedBytes = signedBytes; 
    } 

    @Override 
    public String getHashAlgorithm() { 
        return "SHA-512"; 
    } 

    @Override 
    public String getEncryptionAlgorithm() { 
        return "RSA"; 
    } 

    @Override 
    public byte[] sign(byte[] bytes) throws GeneralSecurityException { 
        return signedBytes; 
    } 
} 