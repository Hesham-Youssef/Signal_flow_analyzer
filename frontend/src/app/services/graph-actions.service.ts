import { Shape, ShapeConfig, shapes } from 'konva/lib/Shape';
import { Injectable } from '@angular/core';
import { Layer } from 'konva/lib/Layer';
import { Stage } from 'konva/lib/Stage';
import { Arrow } from 'konva/lib/shapes/Arrow';
import { Line } from 'konva/lib/shapes/Line';
import { Rect } from 'konva/lib/shapes/Rect';
import Konva from 'konva';
import { Circle } from 'konva/lib/shapes/Circle';
import { cot } from 'mathjs';

@Injectable({
  providedIn: 'root'
})
export class GraphActionsService {
  branchFlag: boolean = false;
  deleteFlag: boolean = false;
  currBranch: any[] = [];
  points: number[] = [];
  edges: number[][] = [];
  isSubmitted: boolean = false;
  value = 0;
  holdingNode: boolean = false;
  currNode!: Shape;
  selected!: Circle | null;
  constructor() {
  }

  mouseEventListeners(stage: Stage, layer: Layer, selectioRec: Rect, arrows: Konva.Arrow[], gains: Konva.Text[], nodes: Shape[]) {

    stage.on('mousedown touchstart', (event) => {
      if (event.target.hasName('node')) {
        this.holdingNode = true;
        this.currNode = event.target as Shape;
      }
      console.log(stage.getPointerPosition);

    })

    stage.on('mouseup touchend', (event) => {
      this.holdingNode = false;
    })

    stage.on('click', async (event) => {
      console.log(event.target.name());


      if(!this.deleteFlag && (event.target.hasName('branch') || event.target.hasName('text'))){
        console.log(this.edges);

        document.getElementById('modal')!.style.display = 'block';
        await this.waitUntil(() => this.isSubmitted);
        this.isSubmitted = false;
        let index = -1
        if(event.target.hasName('branch')){
          index = arrows.indexOf(event.target as Arrow);
        }else if(event.target.hasName('text')){
          index = gains.indexOf(event.target as Konva.Text)
        }
        this.edges[index][2] = this.value;
        gains[index].setText(this.value.toString());
      }

      if (this.deleteFlag) {
        let toBeDeleted: any[] = []
        if (event.target.hasName('node')) {
          this.edges.forEach((edge, i) => {
            if (edge[0] == event.target._id || edge[1] == event.target._id) {
              console.log(arrows, i);
              gains[i].remove();
              arrows[i].remove();
              toBeDeleted.push(i);
            }
          })
          toBeDeleted.forEach((i) => {
            arrows.splice(i, 1);
            this.edges.splice(i, 1);
          })

          toBeDeleted = [];
          nodes.forEach((node, i) => {
            if (node == event.target) {
              node.remove();
              toBeDeleted.push(i);
            }
          });
          toBeDeleted.forEach((i) => {
            nodes.splice(i, 1);
          })
        }

        if (event.target.hasName('branch')) {
          let index = arrows.indexOf(event.target as Konva.Arrow);
          arrows[index].remove();
          arrows.splice(index, 1);
          this.edges.splice(index, 1);
        }

        console.log(nodes, this.edges, arrows);

        return;
      }

      if (!this.branchFlag) {
        return;
      }

      if (event.target.hasName('stage')) {
        console.log('deselected');
        this.currBranch = [];
        this.points = [];
        this.selected!.fill('rgba(0, 0, 0, 1)')
        this.selected = null;
        return;
      }


      if (event.target.hasName('node')) {
        if (this.currBranch.length == 0) {
          this.selected = event.target as Circle;
          (event.target as Circle).fill('rgba(100, 100, 255, 1)');
          this.currBranch.push(event.target._id);
          this.points.push(event.target.getPosition().x);
          this.points.push(event.target.getPosition().y);
        } else {
          let res = this.edges.filter((edge: number[]) => {
            return (edge[0] == this.currBranch[0] && edge[1] == event.target._id)
          }).length;

          if (res != 0) {
            alert("an edge already exists between those nodes");
            this.currBranch = [];
            this.points = [];
            this.selected!.fill('rgba(0, 0, 0, 1)');
            this.selected = null;
            return;
          }
          console.log(this.edges);
          document.getElementById('modal')!.style.display = 'block';
          await this.waitUntil(() => this.isSubmitted);
          this.isSubmitted = false;
          this.currBranch.push(event.target._id);
          this.currBranch.push(Number(this.value));
          console.log(this.value);
          this.edges.push(this.currBranch);
          let x = event.target.getPosition().x;
          let y = event.target.getPosition().y;
          if (this.currBranch[0] == this.currBranch[1]) {
            this.points = this.points.concat([x - 20, y - 50, x + 20, y - 50, x, y]);
          } else {
            this.points = this.getConnectorPoints({x:this.points[0], y:this.points[1]}, event.target.position(), (event.target as Circle).radius());
          }
          console.log(this.points);

          let arrow = new Arrow({
            points: this.points,
            name: 'branch',
            stroke: 'blue',
            tension: 0.5,
            strokeWidth: 5,
          });
          let text = new Konva.Text({
            x: this.points[2] - 10,
            y: this.points[3] - 25,
            text: this.value.toString(),
            fontSize: 20,
            fontFamily: 'Calibri',
            fill: 'green',
            name: 'text'
          });
          layer.add(arrow);
          layer.add(text);
          arrows.push(arrow);
          gains.push(text);
          arrow.moveToBottom();
          text.moveToTop();
          layer.draw();
          this.currBranch = [];
          this.points = [];
          this.selected!.fill('rgba(0, 0, 0, 1)')
          this.selected = null;
        }
        return;
      }

    });

    stage.on('dragmove touchmove', (event) => {
      event.evt.preventDefault();
      if (!this.holdingNode) {
        return;
      }

      const pos: any = event.target.position();
      this.edges.filter((edge: number[]) => (edge[0] == this.currNode._id))
        .forEach((edge: number[]) => {
          this.updateArrows(arrows, edge, pos, gains, nodes.filter((node:Shape) => node._id == edge[1])[0].position(), (event.target as Circle).radius())
        });

        this.edges.filter((edge: number[]) => (edge[1] == this.currNode._id))
        .forEach((edge: number[]) => {
          this.updateArrows(arrows, edge, nodes.filter((node:Shape) => node._id == edge[0])[0].position(), gains, pos, (event.target as Circle).radius())
        });

    });

  }


  updateArrows(arrows: Arrow[], edge: number[], fromPos: {x:number, y:number}, gains: Konva.Text[], pos:{x:number, y:number}, radius: number){
    let index = this.edges.indexOf(edge);
    let gain : Konva.Text = gains[index];
    if(this.edges[index][1] == this.edges[index][0]){
      arrows[index].points(this.points.concat([pos.x, pos.y, pos.x-20, pos.y-50, pos.x+20, pos.y-50, pos.x, pos.y]));
    }else{
      arrows[index].points(this.getConnectorPoints(fromPos, pos, radius));
    }
    gain.setAttr('x', arrows[index].points()[2] - 10);
    gain.setAttr('y', arrows[index].points()[3] - 25);
  }


  getConnectorPoints(from: {x:number, y:number}, to: {x:number, y:number}, radius: number) {
    const dx = to.x - from.x;
    const dy = to.y - from.y;
    let angle = Math.atan2(-dy, dx);

    return [
      from.x + -radius * Math.cos(angle + Math.PI),
      from.y + radius * Math.sin(angle + Math.PI),
      (to.x-from.x+cot(((90)/2))* (to.y-from.y))/2 + from.x, 
      (to.y-from.y+cot(((90)/2))* (from.x-to.x))/2 + from.y,
      to.x + -radius * Math.cos(angle),
      to.y + radius * Math.sin(angle),
    ];
  }

  drawBranch() {
    this.branchFlag = !this.branchFlag;
    this.deleteFlag = false;
    if(!this.branchFlag){
      this.currBranch = [];
      this.points = [];
    }
  }

  submitGain(gain: number) {
    this.value = gain;
    document.getElementById('modal')!.style.display = 'none';
    this.isSubmitted = true;
  }

  waitUntil = (condition: () => any, checkInterval=100) => {
    return new Promise<void>(resolve => {
      let interval = setInterval(() => {
        if (!condition()) return;
        clearInterval(interval);
        resolve();
      }, checkInterval)
    })
  }

  delete(){
    this.deleteFlag = !this.deleteFlag;
    this.branchFlag = false;
    this.currBranch = [];
    this.points = [];
  }

  geteEgeList() {
    let edgeList : number[][] = [];
    for(let i = 0; i < this.edges.length; i++) {
      edgeList.push([]);
      edgeList[i].push(this.edges[i][0] - 5);
      edgeList[i].push(this.edges[i][1] - 5);
      edgeList[i].push(this.edges[i][2]);
    }
    return edgeList;
  }

}


