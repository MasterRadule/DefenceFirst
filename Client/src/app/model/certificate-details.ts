import {Subject} from "./subject";

export interface CertificateDetails {
  subjectData: Subject;
  startDate: Date;
  endDate: Date;
  issuer: string;
  ca: boolean;
}
