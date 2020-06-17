package timejts.SIEMCentre.utils;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;

public class Utilities {

    public static void mavenCleanAndInstall() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("../SIEMCentreRules/pom.xml"));
        request.setGoals(Arrays.asList("clean", "install"));

        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
    }
}
