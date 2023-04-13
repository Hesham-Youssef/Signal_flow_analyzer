import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { KonvaModule } from 'ng2-konva';

import { AppComponent } from './app.component';
import { RouthComponent } from './components/routh/routh.component';
import {FormsModule} from "@angular/forms";
import { GraphComponent } from './components/graph/graph.component';
import { AppRoutingModule } from './app-routing.module';


@NgModule({
  declarations: [
    AppComponent,
    RouthComponent,
    GraphComponent
  ],
    imports: [
        BrowserModule,
        FormsModule,
        AppRoutingModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
