package com.github.kvac.examplesandsuggestions.maven.resolve.jar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class Init {

    public static void main(String[] args) throws IOException {
        File mavenrepo = new File(
                new File(
                        System.getProperty("user.home"), ".m2"
                ), "repository"
        );
        File jarFile = new File("/home/jdcs_dev/minecraft/LIBS/github.com.AuthMe.AuthMeReloaded/target/AuthMe-5.6.0-SNAPSHOT.jar");

        try (ZipFile zipFile = new ZipFile(jarFile)) {
            Enumeration zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                String fileName = ((ZipEntry) zipEntries.nextElement()).getName();
                if (fileName.endsWith("pom.properties")) {
                    //get data
                    String contents = readZipFile(jarFile.getAbsolutePath(), fileName);

                    //get strings
                    String[] stArr = contents.split("\n");
                    ArrayList<String> linesList = new ArrayList<>();
                    linesList.addAll(Arrays.asList(stArr));

                    String groupId = null;
                    String artefactId = null;
                    String version = null;
                    //SET PARAMS
                    for (String line : linesList) {
                        if (line.startsWith("groupId")) {
                            groupId = StringUtils.substringAfter(line, "=");
                            groupId = groupId.replaceAll("\n", "").trim();
                        }
                        if (line.startsWith("artifactId")) {
                            artefactId = StringUtils.substringAfter(line, "=");
                            artefactId = artefactId.replaceAll("\n", "").trim();
                        }
                        if (line.startsWith("version")) {
                            version = StringUtils.substringAfter(line, "=");
                            version = version.replaceAll("\n", "").trim();
                        }
                    }

                    if (groupId != null && artefactId != null && version != null) {
                        String newGroupId = groupId.replaceAll("\\.", File.separator);
                        File fullPath = new File(new File(
                                new File(
                                        new File(
                                                mavenrepo, newGroupId),
                                        artefactId),
                                version), artefactId + "-" + version + ".jar");

                        //  String downloadCMD = "mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get -DartifactId=" + artefactId + " -DgroupId=" + groupId + " -Dversion=" + version;
                        //  System.err.println(downloadCMD);
                        // ADD TO RESOLVE DEPENDENCY
                        // System.err.println(fullPath);
                    }
                }
            }
        }
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
