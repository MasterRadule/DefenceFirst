package timejts.PKI.dto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Value;

public class SubjectDTO {

    private String serialNumber;
    private String commonName;
    private String organization;
    private String organizationalUnit;
    private String city;
    private String state;
    private String country;
    private String email;

    public SubjectDTO() {
    }

    public SubjectDTO(String serialNumber, String commonName, String organization, String organizationalUnit,
                      String city, String state, String country, String email) {
        this.serialNumber = serialNumber;
        this.commonName = commonName;
        this.organization = organization;
        this.organizationalUnit = organizationalUnit;
        this.city = city;
        this.state = state;
        this.country = country;
        this.email = email;
    }

    public SubjectDTO(String serialNumber, X500Name subject, String root, String rootEmail) {
        this.serialNumber = serialNumber;
        this.commonName = subject.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString();
        this.organization = subject.getRDNs(BCStyle.O)[0].getFirst().getValue().toString();
        this.organizationalUnit = subject.getRDNs(BCStyle.OU)[0].getFirst().getValue().toString();
        this.city = subject.getRDNs(BCStyle.L)[0].getFirst().getValue().toString();
        this.state = subject.getRDNs(BCStyle.ST)[0].getFirst().getValue().toString();
        this.country = subject.getRDNs(BCStyle.C)[0].getFirst().getValue().toString();
        this.email = serialNumber.equals(root) ? rootEmail : subject.getRDNs(BCStyle.EmailAddress)[0].getFirst()
                .getValue().toString();
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}