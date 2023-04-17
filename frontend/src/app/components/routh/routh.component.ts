import {Component, OnInit} from '@angular/core';
import {HttpService} from "../../services/http.service";

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

  constructor(private httpService: HttpService) { }

  ngOnInit(): void {
    document.body.style.background = '#161618';
  }

  addCoef() {
    this.coefficients.push({});
  }

  removeCoef() {
    this.coefficients.pop();
  }

  submitCoef() {
    this.coefList = this.coefficients.map(d => {return Number(d.value)});
    this.httpService.getRouth(this.coefList).subscribe(data => {
      console.log(data);
    });
    document.getElementById('ans')!.style.display = 'flex';
  }

  validateNo(e: KeyboardEvent) {
    return (!isNaN(Number(e.key)) || e.key == "Backspace" || e.key == "-");
  }
}
