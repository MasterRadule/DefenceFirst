package timejts.SIEMCentreRules;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import timejts.SIEMCentreRules.model.Facility;
import timejts.SIEMCentreRules.model.Log;
import timejts.SIEMCentreRules.model.RaisedAlarm;
import timejts.SIEMCentreRules.model.Severity;

import java.util.Date;

public class App {

    public static void main(String[] args) throws InterruptedException {
        KieSession kieSession = getKieSession("rules-session");
        Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "message", "Application");
        Thread.sleep(1000);
        Log l2 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "message", "Application");

        kieSession.insert(l1);
        kieSession.insert(l2);
        kieSession.getAgenda().getAgendaGroup("MAIN").setFocus();
        kieSession.fireAllRules();

        QueryResults results = kieSession.getQueryResults("Get new raised alarms");
        if (results.size() == 0) {
            return;
        }

        RaisedAlarm ra;
        for (QueryResultsRow queryResult : results) {
            ra = (RaisedAlarm) queryResult.get("$a");
            System.out.println(ra.getAlarmType().toString());
        }

        kieSession.getAgenda().getAgendaGroup("raise-alarm").setFocus();
        kieSession.fireAllRules();

        kieSession.getAgenda().getAgendaGroup("MAIN").setFocus();
        QueryResults results2 = kieSession.getQueryResults("Get new raised alarms");
        if (results2.size() == 0) {
            System.out.println("No alarms");
        }


    }

    private static KieSession getKieSession(String sessionName) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        return kContainer.newKieSession(sessionName);
    }
}
