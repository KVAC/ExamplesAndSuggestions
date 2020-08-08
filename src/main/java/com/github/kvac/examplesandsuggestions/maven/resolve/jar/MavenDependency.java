/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kvac.examplesandsuggestions.maven.resolve.jar;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jdcs_dev
 */
public class MavenDependency {

    @Getter
    @Setter
    private String groupid;
    @Getter
    @Setter
    private String artefactid;
    @Getter
    @Setter
    private String version;
}
