import {Subject} from './subject';
import {CaCertificateCreation} from './caCertificateCreation';

export interface CreationData {
  creationData: CaCertificateCreation;
  certAuthData: Subject;
}
