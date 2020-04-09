export interface Certificate {
  serialNumber: string;
  commonName: string;
  startDate: Date;
  endDate: Date;
  issuer: string;
  ca: boolean;
}

