package timejts.PKI.dto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class CertAuthorityDTO {

    private String commonName;
    private String organization;
    private String organizationalUnit;
    private String city;
    private String state;
    private String country;
    private String email;

    public CertAuthorityDTO(String commonName, String organization, String organizationalUnit,
                            String city, String state, String country, String email) {
        this.commonName = commonName;
        this.organization = organization;
        this.organizationalUnit = organizationalUnit;
        this.city = city;
        this.state = state;
        this.country = country;
        this.email = email;
    }

    public CertAuthorityDTO(X500Name subject) {
        this.commonName = subject.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString();
        this.organization = subject.getRDNs(BCStyle.O)[0].getFirst().getValue().toString();
        this.organizationalUnit = subject.getRDNs(BCStyle.OU)[0].getFirst().getValue().toString();
        this.city = subject.getRDNs(BCStyle.L)[0].getFirst().getValue().toString();
        this.state = subject.getRDNs(BCStyle.ST)[0].getFirst().getValue().toString();
        this.country = subject.getRDNs(BCStyle.C)[0].getFirst().getValue().toString();
        this.email = subject.getRDNs(BCStyle.EmailAddress)[0].getFirst().getValue().toString();
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
