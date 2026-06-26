import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'sf-root',
  imports: [RouterOutlet],
  template: '<router-outlet />'
})
export class AppComponent {}
