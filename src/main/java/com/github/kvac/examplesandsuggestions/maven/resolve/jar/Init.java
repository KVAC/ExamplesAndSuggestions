package com.github.kvac.examplesandsuggestions.maven.resolve.jar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.maven.model.Dependency;

public class Init {

    static File mavenrepo = new File(
            new File(
                    System.getProperty("user.home"), ".m2"
            ), "repository"
    );

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("first arg - mode (1 show cmd for dependency download, 2 show path for dependency,3 check file exist)\nsecond arg - path to jar file");
            System.exit(1);
        }
        int mode = Integer.parseInt(args[0]);
        File jarFile = new File(args[1]);

        if (!jarFile.exists()) {
            throw new FileNotFoundException(jarFile.getAbsolutePath());
        }

        ArrayList<Dependency> deps = new ArrayList();

        try (ZipFile zipFile = new ZipFile(jarFile)) {
            Enumeration zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                String fileName = ((ZipEntry) zipEntries.nextElement()).getName();

                if (fileName.endsWith("pom.properties") || fileName.endsWith("pom.xml")) {
                    try {
                        if (fileName.endsWith("pom.properties")) {
                            Resolvers.pomPropertiesResolve(deps, jarFile, fileName);
                        }
                        if (fileName.endsWith("pom.xml")) {
                            Resolvers.pomXmlResolve(deps, jarFile, fileName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //TODO SHOW
        for (Dependency dep : deps) {
            File fullPath = Utils.generatePath(mavenrepo, dep.getGroupId(), dep.getArtifactId(), dep.getVersion());
            if (mode == 1) {
                String downloadCMD = "mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get -DartifactId=" + dep.getArtifactId() + " -DgroupId=" + dep.getGroupId() + " -Dversion=" + dep.getVersion();
                System.out.println(downloadCMD);
            } else if (mode == 2) {
                // ADD TO RESOLVE DEPENDENCY
                System.out.println(fullPath);
            } else if (mode == 3) {
                System.out.println(fullPath.exists() + ":" + fullPath);
            }
        }
    }
}
