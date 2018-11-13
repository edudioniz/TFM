/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.core;

/**
 *
 * @author Eduardo
 */
abstract class SignProvider {
    abstract void login();
    abstract void insertCertificates();
    abstract void delCertificates();
    abstract void listCertificates();
    abstract void sign();
}
