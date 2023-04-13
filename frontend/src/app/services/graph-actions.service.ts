import { Injectable } from '@angular/core';
import { Layer } from 'konva/lib/Layer';
import { Stage } from 'konva/lib/Stage';
import { Line } from 'konva/lib/shapes/Line';
import { Rect } from 'konva/lib/shapes/Rect';

@Injectable({
  providedIn: 'root'
})
export class GraphActionsService {
  branchFlag: boolean = false;
  currBranch: any[] = [];
  edges: any[][] = [];
  constructor() { }

  mouseEventListeners(stage: Stage, layer: Layer, selectioRec: Rect, shapes: any){
    stage.on('click', (event) => {
      if(!this.branchFlag){
        return;
      }

      if(event.target.hasName('node') ){
        alert("hello");
        if(this.currBranch.length == 0){
          this.currBranch.push(event.target.id);
        }else{
          alert("enter value");
          let value = 0;
          let validvalue = true; ///// change later
          if(!validvalue){
            return;
          }
          this.currBranch.push(event.target.id);
          this.currBranch.push(value++);
          this.edges.push(this.currBranch);
          let line = new Line({
            
          })
          layer.add(new Line({

          }))
        }
      }
      
    });
  }

  drawBranch(){
    this.branchFlag = !this.branchFlag;
    if(!this.branchFlag)
      this.currBranch = [];
  }
}
