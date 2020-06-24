package timejts.SIEMAgent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import timejts.SIEMCentre.dto.SignedLogsDTO;
import timejts.SIEMCentre.model.Facility;
import timejts.SIEMCentre.model.Log;
import timejts.SIEMCentre.model.Severity;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AgentMain implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    @Qualifier("restTemplateWithStrategy")
    private RestTemplate restTemplate;

    private Boolean firstTime = true;
    private String lastIndex = "";
    private long simulatorLineCounter = 0;

    private Thread osThread;
    private Thread simulatorThread;

    @Value("${app.real-time-mode-os}")
    private boolean osRealTimeMode;
    @Value("${app.batch-time-os}")
    private int batchTimeOs;
    @Value("${app.regex-os}")
    private String regexOs;
    @Value("${app.log-name}")
    private String logNameOs;

    @Value("${app.real-time-mode-simulator}")
    private boolean simulatorTimeMode;
    @Value("${app.batch-time-simulator}")
    private int batchTimeSimulator;
    @Value("${app.regex-simulator}")
    private String regexSimulator;
    @Value("${app.simulator-log-directory}")
    private String pathSimulator;

    @Value("${serialNumber}")
    private String serialNumber;

    @Value("${server.ssl.keystore}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${server.ssl.key-alias}")
    private String keyAlias;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (SystemUtils.IS_OS_WINDOWS) {
            osThread = new Thread(() -> {
                try {
                    windowsProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            osThread.start();

            //simulatorThread = new Thread(this::simulatorProcess);
            //simulatorThread.start();
        } else if (SystemUtils.IS_OS_LINUX) {

        }

    }

    private void windowsProcess() throws KeyStoreException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        ArrayList<Log> logList = new ArrayList<>();
        if (this.osRealTimeMode) {
            System.out.println(this.logNameOs);
            logList = filterLogs(readLogsPowerShell(this.logNameOs), regexOs);
            sendLogs(logList);
            this.firstTime = false;
            while (true) {
                if (readOnChanges(this.logNameOs)) {
                    logList = filterLogs(readLogsPowerShell(this.logNameOs), regexOs);
                    sendLogs(logList);
                }
            }
        } else {
            logList = filterLogs(readLogsPowerShell(this.logNameOs), regexOs);
            sendLogs(logList);
            this.firstTime = false;
            while (true) {
                try {
                    logList = filterLogs(readLogsPowerShell(this.logNameOs), regexOs);
                    sendLogs(logList);
                    Thread.sleep(this.batchTimeOs);
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

    private void simulatorProcess() throws KeyStoreException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        ArrayList<Log> logList = new ArrayList<>();
        String logName = "application_log-";//"application_log-2020-06-23.log";
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        logName = logName + sdf.format(d) + ".log";
        if (this.simulatorTimeMode) {

            logList = filterLogs(readSimulatorLog(this.pathSimulator + logName), regexSimulator);
            sendLogs(logList);
            WatchService watchService = null;
            while (true) {
                try {
                    watchService = FileSystems.getDefault().newWatchService();

                    Path path = Paths.get(this.pathSimulator);

                    path.register(
                            watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY);

                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                                logList = filterLogs(readSimulatorLog(this.pathSimulator + logName), regexSimulator);
                                sendLogs(logList);
                            } else if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                logName = event.context().toString();
                                this.simulatorLineCounter = 0;
                                logList = filterLogs(readSimulatorLog(this.pathSimulator + logName), regexSimulator);
                                sendLogs(logList);
                            }
                        }
                        key.reset();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            logList = filterLogs(readSimulatorLog(this.pathSimulator + logName), regexSimulator);
            sendLogs(logList);
            while (true) {
                logList = filterLogs(readSimulatorLog(this.pathSimulator + logName), regexSimulator);
                sendLogs(logList);
                try {
                    Thread.sleep(this.batchTimeSimulator);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private ArrayList<Log> readSimulatorLog(String logName) {
        ArrayList<Log> logs = new ArrayList<>();

        try {
            BufferedReader br = Files.newBufferedReader(Paths.get(logName));
            long counter = 0;
            String line;
            Log l;
            boolean firstTime = false;
            if (this.simulatorLineCounter == 0)
                firstTime = true;
            while ((line = br.readLine()) != null) {
                if (counter < this.simulatorLineCounter && !firstTime) {
                    counter++;
                    continue;
                }

                l = new Log();
                this.simulatorLineCounter++;
                // System.out.println(simulatorLineCounter);
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
                l.setSeverity(Severity.valueOf(split3[0]));
                l.setFacility(Facility.valueOf(split3[1]));
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

    private ArrayList<Log> filterLogs(ArrayList<Log> logs, String regex) {
        ArrayList<Log> filteredLogs = new ArrayList<>();
        for (Log l : logs) {
            if (l.toString().matches(regex)) {
                filteredLogs.add(l);
                System.out.println(l);
            }
        }
        return filteredLogs;
    }

    private void sendLogs(ArrayList<Log> logs) throws KeyStoreException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("serialNumber", serialNumber);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Log>>() {}.getType();
        String json = gson.toJson(logs, type);
        byte[] bytes = json.getBytes();

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        PrivateKey privKey = (PrivateKey) ks.getKey(keyAlias, keystorePassword.toCharArray());
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privKey);

        signature.update(bytes);
        byte[] digitalSignature = signature.sign();

        SignedLogsDTO signedLogsDTO = new SignedLogsDTO(logs, digitalSignature);
        HttpEntity<SignedLogsDTO> entity = new HttpEntity<>(signedLogsDTO, headers);

        ResponseEntity<String> response =
                this.restTemplate.postForEntity("https://localhost:8082/api/log", entity, String.class);
        System.out.println(response.getBody());
    }
}
