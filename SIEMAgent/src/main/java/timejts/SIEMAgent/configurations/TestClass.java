package timejts.SIEMAgent.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.model.Severity;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class TestClass {

    @Autowired
    @Qualifier("restTemplateWithStrategy")
    RestTemplate restTemplate;

    @Value("${real.time.mode}")
    private boolean realTimeMode;

    @Value("${log.name}")
    private String logName;

    @Value("${batch.time}")
    private int timeSleep;

    private long skipChar = 0;
    private String lastIndex = "";


   /* @PostConstruct
    public void proba() {
        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/Test", "Test connection", String.class);

        System.out.println(response.getBody());
    }*/

    @PostConstruct
    private void process() {
        ArrayList<Log> logList = new ArrayList<>();
        if (this.realTimeMode) {
            //real time sistem
            System.out.println(this.logName);
            logList = readLogsPowerShell(this.logName, this.skipChar);
            /*for(Log l : logList){
                System.out.println("EventId: " + l.getId());
                System.out.println("TimeStamp: " + l.getTimestamp());
                System.out.println("Message: " + l.getMessage());
            }*/
            while (true) {
                if (readOnChanges(this.logName)) {
                    logList = readLogsPowerShell(this.logName, this.skipChar);
                   /* for(Log l : logList){
                        System.out.println("EventId: " + l.getId());
                        System.out.println("TimeStamp: " + l.getTimestamp());
                        System.out.println("Message: " + l.getMessage());
                    }*/
                }
            }
        } else {
            while (true) {
                try {
                    logList = readLogsPowerShell(this.logName, this.skipChar);
                    /*for(Log l : logList){
                        System.out.println("EventId: " + l.getId());
                        System.out.println("TimeStamp: " + l.getTimestamp());
                        System.out.println("Message: " + l.getMessage());
                    }*/
                    Thread.sleep(this.timeSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<Log> readLogsPowerShell(String name, long skip) {
        System.out.println("READ LOGS FROM: " + name);
        //String command = "powershell.exe Get-EventLog -LogName " + name + " | Select-Object -Property *";
        String command = "powershell.exe Get-EventLog -LogName " + name + " | Sort-Object -Property Index | Select-Object -Property *";

        ArrayList<Log> logs = new ArrayList<>();

        try {
            Process powerShell = Runtime.getRuntime().exec(command);
            powerShell.getOutputStream().close();
            InputStreamReader inputReader = new InputStreamReader(powerShell.getInputStream());
            if (skip > 0)
                inputReader.skip(skip);
            BufferedReader br = new BufferedReader(inputReader);

            String line;
            String lastName = "";
            Map<String, ArrayList<String>> event = new HashMap<>();
            boolean start = false;
            while ((line = br.readLine()) != null) {
                if (!start && !line.matches("EventID\\s*:.*")) { //find start of new log if skip
                    continue;
                } else {
                    start = true;
                }
                skip += line.length();// count skip
                line = line.trim();
                if (line.equals("")) // blank line
                    continue;

                if (line.matches("EventID\\s*:.*")) { //start of new log / save previous
                    if (!event.isEmpty()) {
                        //System.out.println(event);
                        this.lastIndex = event.get("Index").get(0);
                        System.out.println("Index changed: " + this.lastIndex);
                        logs.add(createLog(event));
                    }
                    event = new HashMap<>();
                }

                if (line.matches(".*\\s:.*")) { //detect field and value
                    String[] rez = line.split(":", 2);
                    ArrayList<String> values = new ArrayList<>();
                    if (rez[1].matches("\\s*"))
                        rez[1] = "null";
                    values.add(rez[1].trim());
                    String key = rez[0].trim();
                    event.put(key, values);
                    lastName = key;

                } else {
                    event.get(lastName).add(line); //add value to last field
                }

            }

            br.close();
            //last log in stack
            if (!event.isEmpty()) {
                logs.add(createLog(event));
                this.lastIndex = event.get("Index").get(0);
                System.out.println("Index changed: " + this.lastIndex);
            }


            System.out.println("Last index was: " + this.lastIndex);
            //System.out.println(skip);

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.skipChar = skip;
        return logs;
    }

    private boolean readOnChanges(String name) {
        while (true) {
            try {
                String command = "powershell.exe Get-EventLog -LogName " + name + " -Newest 1| Select-Object -Property Index";
                Process powerShell = Runtime.getRuntime().exec(command);
                powerShell.getOutputStream().close();
                InputStreamReader inputReader = new InputStreamReader(powerShell.getInputStream());
                BufferedReader br = new BufferedReader(inputReader);
                String line;
                Long lastIndex = Long.parseLong(this.lastIndex.trim());
                while ((line = br.readLine()) != null) {
                    if (line.matches(".*\\d.*")) {
                        Long index = Long.parseLong(line.trim());
                        if (index > lastIndex) {
                            System.out.println("Index: " + index + " lastIndex: " + lastIndex);
                            br.close();
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private Log createLog(Map<String, ArrayList<String>> log) {
        Log l = new Log();

        l.setSystem(System.getProperty("os.name"));
        l.setHostname(log.get("MachineName").get(0));
        l.setId(new BigInteger(log.get("EventID").get(0)));
        StringBuilder sb = new StringBuilder();
        for (String s : log.get("Message"))
            sb.append(s);
        l.setMessage(sb.toString());
        try {
            l.setHostIP(InetAddress.getLocalHost().getHostAddress());
            l.setSourceIP(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            l.setTimestamp(sdf.parse(log.get("TimeGenerated").get(0)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Severity;
        switch (log.get("EntryType").get(0)){
            case "Error":
                l.setSeverity(Severity.ERROR);
                break;
            case "FailureAudit":
                l.setSeverity(Severity.ALERT);
                break;
            case "Information":
                l.setSeverity(Severity.INFORMATIONAL);
                break;
            case "SuccessAudit":
                l.setSeverity(Severity.NOTICE);
                break;
            case "Warning":
                l.setSeverity(Severity.WARNING);
                break;
            default:
                l.setSeverity(Severity.DEBUG);

        }

        //facility
        switch(Integer.parseInt(log.get("CategoryNumber").get(0))){
            case 0:
                l.setFacility(Facility.KERN);
                break;
            case 1:
                l.setFacility(Facility.USER);
                break;
            case 2:
                l.setFacility(Facility.MAIL);
                break;
            case 3:
                l.setFacility(Facility.DAEMON);
                break;
            case 4:
                l.setFacility(Facility.AUTH);
                break;
            case 5:
                l.setFacility(Facility.SYSLOG);
                break;
            case 6:
                l.setFacility(Facility.LPR);
                break;
            case 7:
                l.setFacility(Facility.NEWS);
                break;
            case 8:
                l.setFacility(Facility.UUCP);
                break;
            case 9:
                l.setFacility(Facility.CLOCK_DAEMON);
                break;
            case 10:
                l.setFacility(Facility.AUTHPRIV);
                break;
            case 11:
                l.setFacility(Facility.FTP);
                break;
            case 12:
                l.setFacility(Facility.NTP);
                break;
            case 13:
                l.setFacility(Facility.LOGAUDIT);
                break;
            case 14:
                l.setFacility(Facility.LOGALERT);
                break;
            case 15:
                l.setFacility(Facility.CRON);
                break;
            default:
                l.setFacility(Facility.LOCAL0);
        }

        return l;
    }
}
