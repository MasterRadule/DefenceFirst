// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  baseURL: 'https://localhost:8443/api',
  initOptionsKeycloak: {
    url: 'http://localhost:8081/auth', realm: 'defence-first', clientId: 'defence-first-client'
  },
  keycloakLogoutURL : `http://localhost:8081/auth/realms/defence-first/protocol/openid-connect/logout?redirect_uri=${document.baseURI}`
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
