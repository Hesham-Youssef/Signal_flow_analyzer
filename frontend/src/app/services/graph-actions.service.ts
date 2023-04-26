import { Shape, ShapeConfig, shapes } from 'konva/lib/Shape';
import { Injectable } from '@angular/core';
import { Layer } from 'konva/lib/Layer';
import { Stage } from 'konva/lib/Stage';
import { Arrow } from 'konva/lib/shapes/Arrow';
import { Line } from 'konva/lib/shapes/Line';
import { Rect } from 'konva/lib/shapes/Rect';
import Konva from 'konva';
import { Circle } from 'konva/lib/shapes/Circle';
import { cot, log } from 'mathjs';
import { AnimateTimings } from '@angular/animations';

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


  updateDottedLines(layer: Layer, startNode: Shape, endNode: Shape, anchor: Shape, gain: Konva.Text) {
    let quadLinePath = layer.findOne('#p' + anchor._id) as any;
    quadLinePath.points([
      startNode.x(),
      startNode.y(),
      anchor.x(),
      anchor.y(),
      endNode.x(),
      endNode.y(),
    ]);
    let pos = anchor.position();
    gain.setAttr('x', pos.x - 10);
    gain.setAttr('y', pos.y - 10);
  }
  

  buildAnchor(layer: Layer, startNode: Shape, endNode: Shape, positions: number[], gain: Konva.Text) {
    let anchor = new Konva.Circle({
      x: positions[2],
      y: positions[3],
      radius: 20,
      stroke: '#666',
      fill: '#ddd',
      id:'a' + startNode._id+endNode._id,
      strokeWidth: 2,
      draggable: true,
    });
    layer.add(anchor);

    // add hover styling
    anchor.on('mouseover', function () {
      document.body.style.cursor = 'pointer';
      this.strokeWidth(4);
    });
    anchor.on('mouseout', function () {
      document.body.style.cursor = 'default';
      this.strokeWidth(2);
    });

    anchor.on('dragmove', (event) => {
      this.updateDottedLines(layer, startNode, endNode, event.target as Shape, gain);
    });

    let quadLinePath = new Konva.Line({
      dash: [10, 10, 0, 10],
      strokeWidth: 3,
      stroke: 'black',
      lineCap: 'round',
      id: 'p'+anchor._id,
      opacity: 0.3,
      points: [0, 0],
    });
    quadLinePath.moveToBottom();
    layer.add(quadLinePath);
    this.updateDottedLines(layer, startNode, endNode, anchor, gain);
    return anchor;
  }
  

  mouseEventListeners(stage: Stage, layer: Layer, arrows: Shape[], gains: Konva.Text[], nodes: Shape[]) {

    stage.on('mousedown touchstart', (event) => {
      if (event.target.hasName('node')) {
        this.holdingNode = true;
        this.currNode = event.target as Shape;
      }

    })

    stage.on('mouseup touchend', (event) => {
      this.holdingNode = false;
    })

    stage.on('click', async (event) => {


      if(!this.deleteFlag && (event.target.hasName('branch') || event.target.hasName('text'))){

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
              gains[i].remove();  
              arrows[i].remove();
              toBeDeleted.push(i);

              if(edge[0] != edge[1]){
                let anchor = layer.findOne('#a' + edge[0]+edge[1]);
                layer.findOne('#p' + anchor._id)!.remove();
                anchor!.remove();
              }
              
            }
          })
          console.log(toBeDeleted);
          
          for (let index = toBeDeleted.length-1; index > -1; index--) {
            arrows.splice(toBeDeleted[index], 1);
            this.edges.splice(toBeDeleted[index], 1);
            gains.splice(toBeDeleted[index], 1);
          }

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
          gains[index].remove();
          if(this.edges[index][0] != this.edges[index][1]){
            let anchor = layer.findOne('#a' + this.edges[index][0]+this.edges[index][1]);
            layer.findOne('#p' + anchor._id)!.remove();
            anchor!.remove();
          }
          arrows.splice(index, 1);
          this.edges.splice(index, 1);
          gains.splice(index, 1);
        }

        console.log(this.edges, arrows);
        
        return;
      }

      if (!this.branchFlag) {
        return;
      }

      if (event.target.hasName('stage')) {
        this.currBranch = [];
        this.points = [];
        this.selected!.fill('rgba(0, 0, 0, 1)')
        this.selected = null;
        return;
      }


      if (event.target.hasName('node')) {
        if (this.currBranch.length == 0) {
          this.selected = event.target as Circle;
          (event.target as Circle).fill('rgba(0, 0, 0, 0.5)');
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
          document.getElementById('modal')!.style.display = 'block';
          await this.waitUntil(() => this.isSubmitted);
          this.isSubmitted = false;
          this.currBranch.push(event.target._id);
          this.currBranch.push(Number(this.value));
          this.edges.push(this.currBranch);
          let pos = event.target.getPosition();
          let arrow = null;
          let text = new Konva.Text({
            x: 0,
            y: 0,
            text: this.value.toString(),
            fontSize: 20,
            fontFamily: 'Calibri',
            fill: 'green',
            name: 'text'
          });
          this.points = this.getConnectorPoints({x:this.points[0], y:this.points[1]}, event.target.position(), (event.target as Circle).radius());
          if (this.currBranch[0] == this.currBranch[1]) {
            this.points = [pos.x, pos.y, pos.x-20, pos.y-50, pos.x+20, pos.y-50, pos.x, pos.y];
            arrow = new Arrow({
              points: this.points,
              name: 'branch',
              stroke: 'blue',
              tension: 0.5,
              strokeWidth: 5,
            });
            text.x(this.points[2] + 10);
            text.y(this.points[3] - 30);
          } else {
              let startNode = nodes.filter((node) => node._id == this.currBranch[0])[0];
              let anchor = this.buildAnchor(layer, startNode, event.target as Shape, this.points, text);
              arrow = new Konva.Shape({
                stroke: 'red',
                name: 'branch',
                strokeWidth: 4,
                sceneFunc: (ctx, shape) => {
                  ctx.beginPath();
                  ctx.moveTo(startNode.x(), startNode.y());
                  ctx.quadraticCurveTo(
                    anchor.x(),
                    anchor.y(),
                    event.target.x(),
                    event.target.y()
                  );
                  ctx.fillStrokeShape(shape);
                },
            });
          }
          
          layer.add(arrow);
          layer.add(text);
          arrows.push(arrow as Arrow);
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
          this.updateArrows(layer, arrows, edge, event.target as Shape, gains, nodes.filter((node:Shape) => node._id == edge[1])[0], (event.target as Circle).radius())
        });

        this.edges.filter((edge: number[]) => (edge[1] == this.currNode._id))
        .forEach((edge: number[]) => {
          this.updateArrows(layer, arrows, edge, nodes.filter((node:Shape) => node._id == edge[0])[0], gains, event.target as Shape, (event.target as Circle).radius())
        });

    });

  }


  updateArrows(layer: Layer, arrows: Shape[], edge: number[], from: Shape, gains: Konva.Text[], end: Shape, radius: number){
    let index = this.edges.indexOf(edge);
    let gain : Konva.Text = gains[index];
    let points: number[] = []
    if(this.edges[index][1] == this.edges[index][0]){
      let pos = from.position();
      points = [pos.x, pos.y, pos.x-20, pos.y-50, pos.x+20, pos.y-50, pos.x, pos.y];
      (arrows[index] as Arrow).points(points);
      gain.setAttr('x', points[2] + 10);
      gain.setAttr('y', points[3] - 30);
    }else{
      let anchor: Shape = layer.findOne('#a' + from._id + end._id)
      this.updateDottedLines(layer, from, end, anchor, gain)
      points = this.getConnectorPoints(from.position(), end.position(), (from as Circle).radius());
    }
    
  }


  getConnectorPoints(from: {x:number, y:number}, to: {x:number, y:number}, radius: number) {
    const dx = to.x - from.x;
    const dy = to.y - from.y;
    let angle = Math.atan2(-dy, dx);

    return [
      from.x + -radius * Math.cos(angle + Math.PI),
      from.y + radius * Math.sin(angle + Math.PI),
      (to.x-from.x-cot(((10)/2))* (to.y-from.y))/2 + from.x, 
      (to.y-from.y-cot(((10)/2))* (from.x-to.x))/2 + from.y,
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


