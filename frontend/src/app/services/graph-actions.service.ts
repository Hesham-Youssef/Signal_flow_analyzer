import { Shape, shapes } from 'konva/lib/Shape';
import { Injectable } from '@angular/core';
import { Layer } from 'konva/lib/Layer';
import { Stage } from 'konva/lib/Stage';
import { Arrow } from 'konva/lib/shapes/Arrow';
import { Line } from 'konva/lib/shapes/Line';
import { Rect } from 'konva/lib/shapes/Rect';
import { atan, cos, index, log, sin, sqrt } from 'mathjs';
import Konva from 'konva';
import { Circle } from 'konva/lib/shapes/Circle';

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

  constructor() { }

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

      if (this.deleteFlag) {
        let toBeDeleted: any[] = []
        if (event.target.hasName('node')) {
          this.edges.forEach((edge, i) => {
            if (edge[0] == event.target._id - 5 || edge[1] == event.target._id - 5) {
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
      }


      if (event.target.hasName('node')) {
        if (this.currBranch.length == 0) {
          this.currBranch.push(event.target._id - 5);
          this.points.push(event.target.getPosition().x);
          this.points.push(event.target.getPosition().y);
        } else {
          let res = this.edges.filter((edge: number[]) => {
            return (edge[0] == this.currBranch[0] && edge[1] == event.target._id - 5)
          }).length;

          if (res != 0) {
            alert("an edge already exists between those nodes");
            this.currBranch = [];
            this.points = [];
            return;
          }
          console.log(this.edges);
          document.getElementById('modal')!.style.display = 'block';
          await this.waitUntil(() => this.isSubmitted);
          this.isSubmitted = false;
          this.currBranch.push(event.target._id - 5);
          this.currBranch.push(Number(this.value));
          console.log(this.value);
          this.edges.push(this.currBranch);
          let x = event.target.getPosition().x;
          let y = event.target.getPosition().y;

          if (this.currBranch[0] == this.currBranch[1]) {
            this.points = this.points.concat([x - 20, y - 50, x + 20, y - 50, x, y]);
          } else {
            this.points = this.points.concat([(this.points[0] + x + (y - this.points[1])) / 2, (this.points[1] + y - x + this.points[0]) / 2, x, y]);
          }
          console.log(this.points);

          let arrow = new Arrow({
            points: this.points,
            name: 'branch',
            stroke: 'blue',
            tension: 0.5,
            strokeWidth: 5
          });
          let text = new Konva.Text({
            x: (this.points[0] + x + y - this.points[1]) / 2 - 10,
            y: (this.points[1] + y - x + this.points[0]) / 2 - 25,
            text: this.value.toString(),
            fontSize: 20,
            fontFamily: 'Calibri',
            fill: 'green'
          });
          layer.add(arrow);
          layer.add(text);
          arrows.push(arrow);
          gains.push(text);
          arrow.moveToBottom();
          layer.draw();
          this.currBranch = [];
          this.points = [];
        }
        return;
      }

    });

    stage.on('dragmove touchmove', (event) => { //doesn't keep following still needs to be fixed
      event.evt.preventDefault();
      if (!this.holdingNode) {
        return;
      }

      const pos: any = this.currNode.getPosition();
      this.edges.filter((edge: number[]) => (edge[0] == this.currNode._id))
        .map((edge) => arrows[this.edges.indexOf(edge)])
        .forEach((edge: Konva.Arrow) => {
          let points = edge.points();
          let gain : Konva.Text = gains[arrows.indexOf(edge)];
          edge.points([pos.x, pos.y, (points[4] + pos.x - (pos.y - points[5])) / 2, (points[5] + pos.y + pos.x - points[4]) / 2].concat(edge.points().slice(4, 6)));
          gain.setAttr('x', (points[0] + points[4] + points[5] - points[1]) / 2 - 10);
          gain.setAttr('y', (points[1] + points[5] - points[4] + points[0]) / 2 - 25);
        });

      this.edges.filter((edge: number[]) => (edge[1] == this.currNode._id))
        .map((edge) => arrows[this.edges.indexOf(edge)])
        .forEach((edge: Konva.Arrow) => {
          let points = edge.points();
          if(this.currBranch[0] == this.currBranch[1]){
            edge.points(this.points.concat([pos.x, pos.y, pos.x-20, pos.y-50, pos.x+20, pos.y-50, pos.x, pos.y]));
          }else{
            edge.points(edge.points().slice(0, 2).concat([(points[0]+pos.x+(pos.y-points[1]))/2, (points[1]+pos.y-pos.x+points[0])/2, pos.x, pos.y]));
          }
        });

    });

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
}


