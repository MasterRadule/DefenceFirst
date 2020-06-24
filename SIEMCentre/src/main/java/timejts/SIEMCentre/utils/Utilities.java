package timejts.SIEMCentre.utils;

import org.apache.maven.shared.invoker.*;
import org.springframework.data.util.Pair;
import timejts.SIEMCentre.model.Alarm;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.RaisedAlarm;
import timejts.SIEMCentre.model.Severity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Utilities {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            //startDateStr = startDateParam.replace(" ", "+");
            //endDateStr = endDateParam.replace(" ", "+");
            startDate = formatter.parse(startDateParam);
            endDate = formatter.parse(endDateParam);

            return Pair.of(startDate, endDate);
        }

        return null;
    }

    public static void preprocessAlarm(Alarm a) {
        a.setId(null);

        if (a.getSourceIPRegex() == null || a.getSourceIPRegex().equals("")) {
            a.setSourceIPRegex("NA");
        }
        if (a.getSeverity() == null) {
            a.setSeverity(Severity.NA);
        }
        if (a.getFacility() == null) {
            a.setFacility(Facility.NA);
        }
        if (a.getMessageRegex1() == null || a.getMessageRegex1().equals("")) {
            a.setMessageRegex1("NA");
        }
        if (a.getMessageRegex2() == null || a.getMessageRegex2().equals("")) {
            a.setMessageRegex2("NA");
        }
    }

    public static void preprocessRaisedAlarm(RaisedAlarm raisedAlarm) {
        raisedAlarm.setId(null);

        if (raisedAlarm.getSourceIP() == null || raisedAlarm.getSourceIP().equals("")) {
            raisedAlarm.setSourceIP("NA");
        }
        if (raisedAlarm.getSeverity() == null) {
            raisedAlarm.setSeverity(Severity.NA);
        }
        if (raisedAlarm.getFacility() == null) {
            raisedAlarm.setFacility(Facility.NA);
        }
        if (raisedAlarm.getMessage1() == null || raisedAlarm.getMessage1().equals("")) {
            raisedAlarm.setMessage1("NA");
        }
        if (raisedAlarm.getMessage2() == null || raisedAlarm.getMessage2().equals("")) {
            raisedAlarm.setMessage2("NA");
        }
    }
}
