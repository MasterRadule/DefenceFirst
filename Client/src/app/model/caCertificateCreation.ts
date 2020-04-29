import {Subject} from './subject';
import {CreationData} from './creationData';

export interface CaCertificateCreation {
  creationData: CreationData;
  certAuthData: Subject;
}
