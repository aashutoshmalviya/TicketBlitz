import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './component/nav-bar/nav-bar';
import { LoadingComponent } from './component/loading/loading';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, LoadingComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
})
export class App {}
