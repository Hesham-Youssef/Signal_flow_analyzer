import { GraphActionsService } from './../../services/graph-actions.service';
import {Component, OnInit} from '@angular/core';
import Konva from 'konva'
import { Layer } from 'konva/lib/Layer';
import { shapes } from 'konva/lib/Shape';
import { Stage } from 'konva/lib/Stage';
import { Circle } from 'konva/lib/shapes/Circle';
import { Rect } from 'konva/lib/shapes/Rect';
import { clone } from 'mathjs';
import {HttpService} from "../../services/http.service";


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
  gains: any[] = [];
  ids: any[] = [];
  nodeColor: string = '#324b77';
  addBtn: boolean = false;
  deleteBtn: boolean = false;
  node!: Circle;
  selectionRec!: Rect;
  gain: number = 0;
  forwardPaths: number[][] = [];
  loops: number[][] = [];
  loopGains: number[] = [];
  pathGains: number[] = [];
  deltas: number[] = [];
  systemDelta: number = 0;
  systemGain: number = 0;

  constructor(private graphActionsService: GraphActionsService, private httpService: HttpService) { }

  ngOnInit(): void {
    document.body.style.background = '#161618';
    window.onclick = function(event: any) {
      if (event.target == document.getElementById('modal')) {
        event.target.style.display = "none";
      }
    }
    this.stage = new Stage({
      container: 'container',
      name: 'stage',
      width: window.innerWidth * 0.96,
      height: window.innerHeight * 0.8
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

    this.graphActionsService.mouseEventListeners(this.stage, this.layer, this.arrows, this.gains, this.nodes, this.ids);
  }

  addNode(){
    let node = clone(this.node);
    let id = new Konva.Text({
      x: node.getAttr('x') - 5,
      y: node.getAttr('y') + 24,
      text: ((node._id - 5) /2).toString(),
      fontSize: 16,
      fill: "Black",
      strokeWidth: 2
    });
    this.nodes.push(node);
    this.layer.add(node);
    this.ids.push(id)
    this.layer.add(id);
    this.layer.draw();
  }

  addBranch(){
    this.addBtn = !this.addBtn;
    this.deleteBtn = false;
    this.updateBtns();
    this.graphActionsService.drawBranch();
  }

  closeModal() {
    document.getElementById('modal')!.style.display = 'none';
  }

  validateNo(e: KeyboardEvent) {
    return (!isNaN(Number(e.key)) || e.key == "Backspace" || e.key == "-");
  }

  submitGain() {
    this.graphActionsService.submitGain(this.gain);
    this.gain = 0;
  }

  delete(){
    this.deleteBtn = !this.deleteBtn;
    this.addBtn = false;
    this.updateBtns();
    this.graphActionsService.delete();
  }

  solveSystem() {
    this.httpService.getSystemSol(this.graphActionsService.getEdgeList(), this.nodes.length).subscribe((data: any) => {
      this.forwardPaths = data.Paths;
      this.pathGains = data.pathsGain;
      this.loops = data.Loops;
      this.loopGains = data.loopsGain;
      this.deltas = data.Deltas;
      this.systemDelta = data.SystemDelta;
      this.systemGain = data.SystemGain;
    });
    document.getElementById("ans-card")!.style.display = "flex";

  }

  closeSystemSol() {
    document.getElementById("ans-card")!.style.display = "none";
  }

  private updateBtns() {
    if (!this.addBtn) {
      document.getElementById('add-btn')!.style.background = '#262628';
    }
    else {
      document.getElementById('add-btn')!.style.background = '#3038d1';
    }

    if (!this.deleteBtn) {
      document.getElementById('delete-btn')!.style.background = '#262628';
    }
    else {
      document.getElementById('delete-btn')!.style.background = '#ff2800';
    }
  }
}
