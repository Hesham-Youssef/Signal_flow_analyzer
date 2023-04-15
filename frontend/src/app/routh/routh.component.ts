import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-routh',
  templateUrl: './routh.component.html',
  styleUrls: ['./routh.component.css']
})
export class RouthComponent implements OnInit {
  coefficients : any[] = [{}, {}, {}];
  coefList : number[] = [0, 0, 0];
  ansTable: number[][] = [[1, 2, 3], [4, 5, 6], [7, 8, 9]];
  rootsInRHS: number = 0;
  stabilityStatus: string = "Critically Stable";

  constructor() { }

  ngOnInit(): void { }

  addCoef() {
    this.coefficients.push({});
  }

  removeCoef() {
    this.coefficients.pop();
  }

  submitCoef() {
    this.coefList = this.coefficients.map(d => {return Number(d.value)});
    console.log(this.coefList);
    //here goes the http request to get the calculations from the back
    //once we put the values into their respective variables, it will change on the UI
    document.getElementById('ans')!.style.opacity = '1';
  }

  validateNo(e: KeyboardEvent) {
    return (!isNaN(Number(e.key)) || e.key == "Backspace");
  }
}
