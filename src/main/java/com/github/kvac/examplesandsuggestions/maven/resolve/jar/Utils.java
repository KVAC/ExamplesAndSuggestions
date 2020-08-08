/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kvac.examplesandsuggestions.maven.resolve.jar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Dependency;

/**
 *
 * @author jdcs_dev
 */
public class Utils {

    public static boolean containsInDeps(ArrayList<Dependency> depsForCheck, String groupId, String artefactId, String version) {
        return depsForCheck.stream().filter(dep -> (dep.getGroupId().equals(groupId))).filter(dep -> (dep.getArtifactId().equals(artefactId))).anyMatch(dep -> (dep.getVersion().equals(version)));
    }

    public static boolean containsDepInDeps(ArrayList<Dependency> depsForCheck, Dependency dependency) {
        for (Dependency dep : depsForCheck) {
            if (dep.getGroupId().equals(dependency.getGroupId())) {
                if (dep.getArtifactId().equals(dependency.getArtifactId())) {
                    if (dep.getVersion().equals(dependency.getVersion())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static File generatePath(File repoFile, String groupId, String artefactId, String version) {
        String newGroupId = groupId.replaceAll("\\.", File.separator);
        return new File(
                new File(
                        new File(
                                new File(
                                        repoFile, newGroupId),
                                artefactId),
                        version),
                artefactId + "-" + version + ".jar");
    }

    public static String readZipFile(String zipFilePath, String relativeFilePath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                // if the entry is not directory and matches relative file then extract it
                if (!entry.isDirectory() && entry.getName().equals(relativeFilePath)) {
                    BufferedInputStream bis = new BufferedInputStream(
                            zipFile.getInputStream(entry));
                    String fileContentsStr = IOUtils.toString(bis, "UTF-8");

                    bis.close();
                    return fileContentsStr;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
