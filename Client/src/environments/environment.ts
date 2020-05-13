// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  baseURL: 'https://localhost:9092/api',
  client_id: 'Defence First Client',
  client_secret: 'f838e554-6ed4-4935-af58-bf68ccd514a7',
  grant_type: 'password',
  realm: 'Defence First Realm',
  keycloak_port: 8180
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
