package timejts.SIEMAgent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import timejts.SIEMAgent.configurations.ConfigProperties;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.model.Severity;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class AgentMain implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    @Qualifier("restTemplateWithStrategy")
    private RestTemplate restTemplate;

    @Autowired
    private ConfigProperties properties;

    private Boolean firstTime = true;
    private String lastIndex = "";
    private long simulatorLineCounter = 0;

   /* @PostConstruct
    public void proba() {
        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/Test", "Test connection", String.class);

        System.out.println(response.getBody());
    }*/

    //@PostConstruct
    // @Override

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        switch (properties.getAgentMode()) {
            case "Windows":
                this.simulatorProcess();
                break;
            case "Linux":
                break;
        }

    }

    private void windowsProcess() {
        ArrayList<Log> logList = new ArrayList<>();
        if (properties.getRealTimeMode()) {
            System.out.println(properties.getLogName());
            logList = readLogsPowerShell(properties.getLogName());
            this.firstTime = false;
            while (true) {
                if (readOnChanges(properties.getLogName())) {
                    logList = readLogsPowerShell(properties.getLogName());
                }
            }
        } else {
            logList = readLogsPowerShell(properties.getLogName());
            this.firstTime = false;
            while (true) {
                try {
                    logList = readLogsPowerShell(properties.getLogName());
                    Thread.sleep(properties.getBatchTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<Log> readLogsPowerShell(String name) {
        System.out.println("READ LOGS FROM: " + name);
        //String command = "powershell.exe Get-EventLog -LogName " + name + " | Select-Object -Property *";
        String command = "powershell.exe Get-EventLog -LogName " + name + " | Sort-Object -Property Index  | Select-Object -Property *";
        if (!this.firstTime)
            command = "powershell.exe Get-EventLog -LogName " + name + " | Where-object {$_.Index -gt '" + this.lastIndex + "'} | Sort-Object -Propert Index | Select-Object -Property *";
        ArrayList<Log> logs = new ArrayList<>();

        try {
            Process powerShell = Runtime.getRuntime().exec(command);
            powerShell.getOutputStream().close();
            InputStreamReader inputReader = new InputStreamReader(powerShell.getInputStream());
            BufferedReader br = new BufferedReader(inputReader);

            String line;
            String lastName = "";
            Map<String, ArrayList<String>> event = new HashMap<>();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("")) // blank line
                    continue;

                if (line.matches("^EventID\\s*:.*")) { //start of new log / save previous
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

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        switch (log.get("EntryType").get(0)) {
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
        switch (Integer.parseInt(log.get("CategoryNumber").get(0))) {
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

    private void simulatorProcess() {
        ArrayList<Log> logList = new ArrayList<>();
        if (properties.getRealTimeMode()) {
            logList = readSimulatorLog(properties.getSimulatorLogName());
            while (true) {
                logList = readSimulatorLog(properties.getSimulatorLogName());
                // java watcher
            }
        } else {

        }
    }

    private ArrayList<Log> readSimulatorLog(String logName) {
        ArrayList<Log> logs = new ArrayList<>();

        try {
            BufferedReader br = Files.newBufferedReader(Paths.get("E:\\Fakultet\\DefenceFirst\\Simulator\\logs\\" + logName)); // ??? path
            long counter = 0;
            String line;
            Log l;
            while ((line = br.readLine()) != null) {
                if (counter < this.simulatorLineCounter) {
                    counter++;
                    continue;
                }

                l = new Log();
                this.simulatorLineCounter++;
                //System.out.println(line);

                //System.out.println(this.simulatorLineCounter);
                String[] splitLine = line.split("\\s{5}");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                l.setTimestamp(sdf.parse(splitLine[0]));

                String[] split2 = splitLine[1].split("-");
                l.setHostname(split2[0] + split2[1]);
                l.setSystem(split2[2]);
                l.setHostIP(split2[3]);
                l.setSourceIP(split2[4]);

                String[] split3 = splitLine[2].split("-");

                switch (split3[0]) {
                    case "DEBUG":
                        l.setSeverity(Severity.DEBUG);
                        break;
                    case "INFORMATIONAL":
                        l.setSeverity(Severity.INFORMATIONAL);
                        break;
                    case "NOTICE":
                        l.setSeverity(Severity.NOTICE);
                        break;
                    case "WARNING":
                        l.setSeverity(Severity.WARNING);
                        break;
                    case "ERROR":
                        l.setSeverity(Severity.ERROR);
                        break;
                    case "CRITICAL":
                        l.setSeverity(Severity.CRITICAL);
                        break;
                    case "ALERT":
                        l.setSeverity(Severity.ALERT);
                        break;
                    case "EMERGENCY":
                        l.setSeverity(Severity.EMERGENCY);
                        break;
                }
                switch (split3[1]) {
                    case "KERN":
                        l.setFacility(Facility.KERN);
                        break;
                    case "USER":
                        l.setFacility(Facility.USER);
                        break;
                    case "MAIL":
                        l.setFacility(Facility.MAIL);
                        break;
                    case "DAEMON":
                        l.setFacility(Facility.DAEMON);
                        break;
                    case "AUTH":
                        l.setFacility(Facility.AUTH);
                        break;
                    case "SYSLOG":
                        l.setFacility(Facility.SYSLOG);
                        break;
                    case "LPR":
                        l.setFacility(Facility.LPR);
                        break;
                    case "NEWS":
                        l.setFacility(Facility.NEWS);
                        break;
                    case "UUCP":
                        l.setFacility(Facility.UUCP);
                        break;
                    case "CLOCK_DAEMON":
                        l.setFacility(Facility.CLOCK_DAEMON);
                        break;
                    case "AUTHPRIV":
                        l.setFacility(Facility.AUTHPRIV);
                        break;
                    case "FTP":
                        l.setFacility(Facility.FTP);
                        break;
                    case "NTP":
                        l.setFacility(Facility.NTP);
                        break;
                    case "LOGAUDIT":
                        l.setFacility(Facility.LOGAUDIT);
                        break;
                    case "LOGALERT":
                        l.setFacility(Facility.LOGALERT);
                        break;
                    case "CRON":
                        l.setFacility(Facility.CRON);
                        break;
                    case "LOCAL0":
                        l.setFacility(Facility.LOCAL0);
                        break;
                    case "LOCAL1":
                        l.setFacility(Facility.LOCAL1);
                        break;
                    case "LOCAL2":
                        l.setFacility(Facility.LOCAL2);
                        break;
                    case "LOCAL3":
                        l.setFacility(Facility.LOCAL3);
                        break;
                    case "LOCAL4":
                        l.setFacility(Facility.LOCAL4);
                        break;
                    case "LOCAL5":
                        l.setFacility(Facility.LOCAL5);
                        break;
                    case "LOCAL6":
                        l.setFacility(Facility.LOCAL6);
                        break;
                    case "LOCAL7":
                        l.setFacility(Facility.LOCAL7);
                        break;
                }
                l.setMessage(splitLine[3]);
                logs.add(l);
            }

            br.close();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return logs;
    }
}
