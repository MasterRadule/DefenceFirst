export interface Alarm {
  count: number;
  timespan: number;
  sourceIPRegex: string;
  severity: string;
  facility: string;
  messageRegex1: string;
  messageRegex2: string;
}
