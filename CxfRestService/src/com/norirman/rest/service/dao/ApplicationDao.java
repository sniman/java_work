/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.norirman.rest.service.dao;

import com.norirman.rest.service.model.Version;

/**
 *
 * @author 608761624
 */
public class ApplicationDao {

    public Version getVersion() {
        Version version = new Version();
        version.setBuild("20160212");
        version.setAuthor("norirman");
        version.setVersion("1.0");
        version.setDescription("Simple rest skelaton");
        return version;
    }
    
    

}
