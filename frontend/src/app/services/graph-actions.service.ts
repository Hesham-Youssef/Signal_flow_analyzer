import { Injectable } from '@angular/core';
import { Layer } from 'konva/lib/Layer';
import { Stage } from 'konva/lib/Stage';
import { Rect } from 'konva/lib/shapes/Rect';

@Injectable({
  providedIn: 'root'
})
export class GraphActionsService {
  branchFlag: boolean = false;
  currBranch: any[] = [];
  constructor() { }

  mouseEventListeners(stage: Stage, layer: Layer, selectioRec: Rect){
    this.branchFlag = !this.branchFlag;
    if(!this.branchFlag)
      this.currBranch = [];
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
          if(validvalue){
            this.currBranch.push(event.target.id);
            this.currBranch.push(value++);
            
          }
        }
      }
      
    });
  }
}
