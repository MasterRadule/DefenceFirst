export interface SearchLogsDTO {
  messageRegex: string;
  hostname: string;
  hostIPRegex: string;
  severity: string;
  facility: string;
  startDate: string;
  endDate: string;
}
