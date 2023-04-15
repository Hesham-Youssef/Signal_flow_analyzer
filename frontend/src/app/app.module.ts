import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { KonvaModule } from 'ng2-konva';

import { AppComponent } from './app.component';

import { RouthComponent } from './components/routh/routh.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AppRoutingModule } from './app-routing.module';

import { GraphComponent } from './components/graph/graph.component';



@NgModule({
  declarations: [
    AppComponent,
    RouthComponent,
    GraphComponent
  ],

  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
  ],

  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
