import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { provideRouter } from '@angular/router';
import { provideHttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './app/core/auth/token.interceptor';
import { routes } from './app/app.routes';

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),                       // <-- routing
    provideHttpClient(),                         // <-- HttpClient provider required
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true } // <-- interceptor
  ]
}).catch(err => console.error(err));
