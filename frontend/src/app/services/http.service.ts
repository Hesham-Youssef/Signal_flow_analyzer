import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class HttpService {
  apiURL = "http://localhost:8080/solver";

  constructor(private http: HttpClient) { }

  getSystemSol(edgeList: number[][], nodes: number) {
    let params = new HttpParams();
    for (let i = 0; i < edgeList.length; i++) {
      params = params.append('edges', edgeList[i].join(','));
    }
    params = params.append('nodes', nodes);
    return this.http.get(this.apiURL + "/flowGraph", {params: params});
  }

  getRouth(coefficients: number[]) {
    let params = new HttpParams();
    params = params.append('edges', coefficients.toString());
    return this.http.get(this.apiURL + "/routhHurwitz" , {params: params});
  }

}
