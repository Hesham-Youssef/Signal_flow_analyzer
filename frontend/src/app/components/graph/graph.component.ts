import { GraphActionsService } from './../../services/graph-actions.service';
import { Component, OnInit } from '@angular/core';
import Konva from 'konva'
import { Layer } from 'konva/lib/Layer';
import { shapes } from 'konva/lib/Shape';
import { Stage } from 'konva/lib/Stage';
import { Circle } from 'konva/lib/shapes/Circle';
import { Rect } from 'konva/lib/shapes/Rect';
import { clone } from 'mathjs';


@Component({
  selector: 'app-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.css']
})
export class GraphComponent implements OnInit {
  stage!: Stage;
  layer!: Layer;
  nodes: any[] = [];
  arrows: any[] = [];
  nodeColor: string = '#000000';
  node!: Circle;
  selectionRec!: Rect;

  constructor(private graphActionsService: GraphActionsService) { }

  ngOnInit(): void {
    this.stage = new Stage({
      container: 'container',
      width: window.innerWidth,
      height: window.innerHeight
    });

    this.selectionRec = new Konva.Rect({
      fill: 'rgba(0, 0, 255, 0.5)',
      visible: true
    });
    this.node = new Konva.Circle({
      x: 50,
      y: 50,
      radius: 20,
      name: 'node',
      fill: this.nodeColor,
      draggable: true
    });

    this.layer = new Layer();
    this.stage.add(this.layer);
    this.layer.add(this.selectionRec);
    
    

    this.graphActionsService.mouseEventListeners(this.stage, this.layer, this.selectionRec, this.arrows, this.nodes);
  }

  addNode(){
    let node = clone(this.node);

    this.nodes.push(node);
    this.layer.add(node);
    this.layer.draw();
  }

  addBranch(){
    this.graphActionsService.drawBranch();
  }

  delete(){
    this.graphActionsService.delete();
  }

}
