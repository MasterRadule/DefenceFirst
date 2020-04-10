import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Subject} from '../model/subject';

@Injectable({
  providedIn: 'root'
})
export class PkiApiService {

  constructor(private http: HttpClient) {
  }

  submitCSR(csrData: Int8Array) {
    return this.http.post('certificates/csr', csrData);
  }

  createNonCACertificate(serialNumber: string, caSerialNumber: string) {
    return this.http.post('certificates/non-ca', {serialNumber, caSerialNumber});
  }

  createCACertificate(certAuth: Subject) {
    return this.http.post('certificates/ca', certAuth);
  }

  getCertificateSigningRequests() {
    return this.http.get('certificates/csr');
  }

  getCertificates() {
    return this.http.get('certificates');
  }

  getCertificate(serialNumber: string) {
    return this.http.get(`certificates/${serialNumber}`);
  }

  revokeCertificate(serialNumber: string) {
    return this.http.put('certificates/revoked', serialNumber);
  }

  getRevokedCertificates() {
    return this.http.get('certificates/revoked');
  }
}
