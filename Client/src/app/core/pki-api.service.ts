import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CaCertificateCreation} from '../model/caCertificateCreation';
import {NonCACreationData} from '../model/nonCACertificateCreation';

@Injectable({
  providedIn: 'root'
})
export class PkiApiService {

  constructor(private http: HttpClient) {
  }

  submitCSR(csrData: Int8Array) {
    return this.http.post('certificates/csr', csrData);
  }

  createNonCACertificate(certificateData: NonCACreationData) {
    return this.http.post('certificates/non-ca', certificateData, {responseType: 'text'});
  }

  createCACertificate(certAuth: CaCertificateCreation) {
    return this.http.post('certificates/ca', certAuth, {responseType: 'text'});
  }

  getCertificateSigningRequests() {
    return this.http.get('certificates/csr');
  }

  getCertificates() {
    return this.http.get('certificates', { withCredentials: true });
  }

  getCertificate(serialNumber: string) {
    return this.http.get(`certificates/${serialNumber}`);
  }

  revokeCertificate(serialNumber: string) {
    return this.http.put('certificates/revoked', serialNumber, {responseType: 'text'});
  }

  getRevokedCertificates() {
    return this.http.get('certificates/revoked');
  }

  getCACertificates() {
    return this.http.get('certificates/ca');
  }

  rejectCSR(serialNumber: string) {
    return this.http.delete(`certificates/csr/${serialNumber}`, {responseType: 'text'});
  }
}
