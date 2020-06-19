package timejts.SIEMCentre.utils;

import org.apache.maven.shared.invoker.*;
import org.springframework.data.util.Pair;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Utilities {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static void mavenCleanAndInstall() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("../SIEMCentreRules/pom.xml"));
        request.setGoals(Arrays.asList("clean", "install"));

        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
    }

    public static Pair<Date, Date> parseDates(String startDateParam, String endDateParam) throws ParseException {
        Date startDate;
        Date endDate;
        String startDateStr;
        String endDateStr;
        if (!startDateParam.equals("") && !endDateParam.equals("")) {
            startDateStr = startDateParam.replace(" ", "+");
            endDateStr = endDateParam.replace(" ", "+");
            startDate = formatter.parse(startDateStr);
            endDate = formatter.parse(endDateStr);

            return Pair.of(startDate, endDate);
        }

        return null;
    }
}
