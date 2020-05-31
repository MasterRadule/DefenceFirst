// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  baseURL: 'https://localhost:9092/api',
  auth: {
    clientID: 'd0gkxBZS7W7P3nzSIG5x7v0ri26KHPSa',
    domain: 'dev-6w-2hyw1.eu.auth0.com',
    audience: 'https://localhost:9092',
    redirect: 'https://localhost:4200/dashboard',
    logout: 'https://localhost:4200/dashboard',
    scope: 'openid profile email read:certificates write:certificates'
  }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
