/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kvac.examplesandsuggestions.maven.resolve.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 * @author jdcs_dev
 */
public class Resolvers {

    static MavenXpp3Reader reader = new MavenXpp3Reader();

    public static void pomXmlResolve(ArrayList<Dependency> deps, File jarFile, String fileName) throws IOException, XmlPullParserException {
        String initialString = Utils.readZipFile(jarFile.getAbsolutePath(), fileName);

        InputStream targetStream = IOUtils.toInputStream(initialString, StandardCharsets.UTF_8);

        Model model = reader.read(targetStream);

        Properties props = model.getProperties();
        ArrayList<Dependency> dependencys = new ArrayList<>();

        List<Dependency> rawDepends = model.getDependencies();
        for (Dependency rawDepend : rawDepends) {
            //FIXME
            if (rawDepend.getVersion() != null) {
                dependencys.add(rawDepend);
            }
        }

        Dependency deadDependency = null;
        String deadVersion = null;
        try {
            for (Dependency dependency : dependencys) {
                deadDependency = dependency;
                deadVersion = dependency.getVersion();

                if (dependency.getVersion().startsWith("${")) {
                    String curProp = StringUtils.substringBetween(dependency.getVersion(), "${", "}");
                    String normalVersion = (String) props.get(curProp);
                    dependency.setVersion(normalVersion);
                }
            }
        } catch (Exception e) {
            System.err.println("EXP:" + deadDependency + "  VERSION:" + deadVersion);
            System.err.println("----------------------------------------------------");
            System.err.println(initialString);
            System.err.println("----------------------------------------------------");
            e.printStackTrace();
            System.exit(1);
        }
        for (Dependency dependency : dependencys) {
            if (Utils.containsDepInDeps(deps, dependency)) {
                deps.add(dependency);
            }
        }
    }

    public static void pomPropertiesResolve(ArrayList<Dependency> deps, File jarFile, String fileName) { //get data
        String contents = Utils.readZipFile(jarFile.getAbsolutePath(), fileName);

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
            if (!Utils.containsInDeps(deps, groupId, artefactId, version)) {
                Dependency dependency = new Dependency();
                dependency.setGroupId(groupId);
                dependency.setArtifactId(artefactId);
                dependency.setVersion(version);
                deps.add(dependency);
            }
        }

    }
}
