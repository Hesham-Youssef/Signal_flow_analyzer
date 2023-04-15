import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RouthComponent } from './components/routh/routh.component';
import { GraphComponent } from './components/graph/graph.component';


const routes: Routes = [
  {path: '', component: RouthComponent},
  {path: 'graph', component: GraphComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
