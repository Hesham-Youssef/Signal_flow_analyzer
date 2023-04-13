import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-routh',
  templateUrl: './routh.component.html',
  styleUrls: ['./routh.component.css']
})
export class RouthComponent implements OnInit {
  coefficients : any[] = [];

  constructor() { }

  ngOnInit(): void {
  }

  addCoef() {
    this.coefficients.push({});
  }

  submitCoef() {
    console.log(this.coefficients);
  }
}
