package timejts.SIEMAgent.configurations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import timejts.SIEMCentre.dto.SignedLogsDTO;
import timejts.SIEMCentre.model.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.reflect.Type;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Component
public class TestClass {

    @Autowired
    @Qualifier("restTemplateWithStrategy")
    RestTemplate restTemplate;

    @Value("${serialNumber}")
    private String serialNumber;

    @Value("${server.ssl.keystore}")
    private String keystorePath;

    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    @Value("${server.ssl.key-alias}")
    private String keyAlias;

    @PostConstruct
    public void alarms() throws InterruptedException, KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, SignatureException, InvalidKeyException {

        /*System.out.println("Alarms creating");

        HttpHeaders headers = new HttpHeaders();
        headers.add("serialNumber", serialNumber);

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        PrivateKey privKey = (PrivateKey) ks.getKey(keyAlias, keystorePassword.toCharArray());
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privKey);

        Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "message", "Application");
        System.out.println(l1.getTimestamp());
        Thread.sleep(2000);
        Log l2 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "message", "Application");
        System.out.println(l2.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);
        logs.add(l2);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Log>>() {}.getType();
        String json = gson.toJson(logs, type);
        byte[] bytes = json.getBytes();

        signature.update(bytes);
        byte[] digitalSignature = signature.sign();

        SignedLogsDTO signedLogsDTO = new SignedLogsDTO(logs, digitalSignature);
        HttpEntity<SignedLogsDTO> entity = new HttpEntity<>(signedLogsDTO, headers);

        ResponseEntity<String> response =
                this.restTemplate.postForEntity("https://localhost:8082/api/log", entity, String.class);
        System.out.println(response.getBody());*/


        /*ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/api/Test", "Message", String.class);
        System.out.println(response.getBody());*/

        /*######## EXCEEDED NUMBER OF REQUESTS  ########*/
        /*Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "message", "Application");
        System.out.println(l1.getTimestamp());
        Thread.sleep(2000);
        Log l2 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "message", "Application");
        System.out.println(l2.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);
        logs.add(l2);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/log", logs, String.class);
        System.out.println(response.getBody());


        Alarm alarm = new Alarm(null, 2, 10, "", Severity.INFORMATIONAL, Facility.AUTH, "", "");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.EXCEEDED_NUMBER_OF_REQUESTS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());*/


        /*######## SUSPICIOUS BEHAVIOUR  ########*/
        /*Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "200", "Application");
        System.out.println(l1.getTimestamp());
        Thread.sleep(2000);
        Log l2 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.INFORMATIONAL,
                Facility.AUTH, "503", "Application");
        System.out.println(l2.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);
        logs.add(l2);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/log", logs, String.class);
        System.out.println(response.getBody());


        Alarm alarm = new Alarm(null, 2, 2, "", Severity.INFORMATIONAL, Facility.AUTH, "\"200\"", "\"503\"");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.SUSPICIOUS_BEHAVIOUR);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());*/


        /*######## SEVERITY ALARM  ########*/
        /*Log l1 = new Log(new Date(), "hostname", "hostIP", "hostIP", Severity.ERROR,
                Facility.AUTH, "200", "Application");
        System.out.println(l1.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/log", logs, String.class);
        System.out.println(response.getBody());


        Alarm alarm = new Alarm(null, 2, 2, "", Severity.ERROR, Facility.AUTH, "\"200\"", "\"503\"");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.SEVERITY_ALARM);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());*/


        /*######## MALICIOUS IP ADDRESS  ########*/
        /*Log l1 = new Log(new Date(), "hostname", "192.168.8.1", "192.168.8.1", Severity.ERROR,
                Facility.AUTH, "200", "Application");
        System.out.println(l1.getTimestamp());
        ArrayList<Log> logs = new ArrayList<>();
        logs.add(l1);

        ResponseEntity<String> response =
                restTemplate.postForEntity("https://localhost:8082/log", logs, String.class);
        System.out.println(response.getBody());


        Alarm alarm = new Alarm(null, 2, 2, "\"192.168.8.1\"", Severity.ERROR, Facility.AUTH, "\"200\"", "\"503\"");
        AlarmDTO alarmDTO = new AlarmDTO(alarm, AlarmType.MALICIOUS_IP_ADDRESS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AlarmDTO> entity = new HttpEntity<>(alarmDTO, headers);

        ResponseEntity<String> response2 =
                restTemplate.postForEntity("https://localhost:8082/alarm", entity, String.class);
        System.out.println(response2.getBody());*/
    }

    /*@PostConstruct
    public void logs() {

        System.out.println("Logs");
        /*ResponseEntity<Object> response =
                restTemplate.getForEntity("https://localhost:8082/log/search?messageRegex&hostname" +
                        "&hostIPRegex&startDate=2020-06-18T13:58:03.732+00:00&endDate=2020-06-18T19:01:11.728+00:00&severity&facility&page=0&size=5", Object.class);
        System.out.println(response.getBody());*/

        /*ResponseEntity<Object> response =
                restTemplate.getForEntity("https://localhost:8082/log/report/system?startDate=2020-06-18T13:58:03.732+00:00" +
                        "&endDate=2020-06-18T19:01:11.728+00:00&system=Application&machine", Object.class);
        System.out.println(response.getBody());*/

        /*ResponseEntity<Object> response =
                restTemplate.getForEntity("https://localhost:8082/log/report/machine?startDate=2020-06-18T13:58:03.732+00:00" +
                        "&endDate=2020-06-18T19:01:11.728+00:00&system&machine=hostIP", Object.class);
        System.out.println(response.getBody());
    }*/

    /*@PostConstruct
    public void alarmsReport() {
        System.out.println("Alarms getting");

        ResponseEntity<Object> response =
                restTemplate.getForEntity("https://localhost:8082/alarm/report/severity?startDate=2020-06-18T12:59:55.639+00:00" +
                        "&endDate=2020-06-19T07:06:04.032+00:00&severity=INFORMATIONAL&facility&alarmType", Object.class);
        System.out.println(response.getBody());

        ResponseEntity<Object> response2 =
                restTemplate.getForEntity("https://localhost:8082/alarm/report/facility?startDate=2020-06-18T12:59:55.639+00:00" +
                        "&endDate=2020-06-19T07:06:04.032+00:00&severity&facility=AUTH&alarmType", Object.class);
        System.out.println(response2.getBody());

        ResponseEntity<Object> response3 =
                restTemplate.getForEntity("https://localhost:8082/alarm/report/alarm-type?startDate=2020-06-18T12:59:55.639+00:00" +
                        "&endDate=2020-06-19T07:06:04.032+00:00&severity&facility&alarmType=EXCEEDED_NUMBER_OF_REQUESTS", Object.class);
        System.out.println(response3.getBody());
    }*/
}
