import { Component, OnInit } from '@angular/core';
import {DbServService} from '../shared/db-serv.service';
import {Animal} from '../models/animal';
import {AngularFireList, AngularFireObject} from '@angular/fire/database';
import {AddAnimal} from '../models/add-animal';

@Component({
  selector: 'app-animals-list',
  templateUrl: './animals-list.component.html',
  styleUrls: ['./animals-list.component.css']
})
export class AnimalsListComponent implements OnInit {

  constructor(private firebService: DbServService) { }
  animals: Animal[] = [];
  animalsq: Animal[] = [];
  ans: AngularFireList<Animal[]>;
  ansdet: AngularFireObject<Animal>;
  singleAnimal;
  newAnimal: Animal = {
    $key: '',
    type: 's',
    name: 'ss',
    weight: '44'
  };
  newAnimala: AddAnimal = {
    type: 's',
    name: 'ss',
    weight: '44'
  };
  ngOnInit(): void {
    this.firebService.getAnimals().snapshotChanges().subscribe(res => {
      res.forEach(t => {
        const todo = t.payload.toJSON();
        todo['$key'] = t.key;
        this.animals.push(todo as Animal);
      });
      console.log(this.animals);
    });
  }
  // tslint:disable-next-line:typedef
  getById(data: any) {
    this.firebService.getAnimalById(data.$key).snapshotChanges().subscribe(res => {
      console.log(res.payload.toJSON());
      this.singleAnimal = res.payload.toJSON();
    });
  }
  // tslint:disable-next-line:typedef
  onSub(data: any) {
    this.newAnimala.type = data.type;
    this.newAnimala.name = data.name;
    this.newAnimala.weight = data.weight;
    console.log(this.newAnimala);
    this.firebService.addAnimal(this.newAnimala);
  }
  delAnimal(k: string) {
    this.firebService.delAnimal(k).then(res => {
      this.animals = this.animals.filter(t => t.$key !== k);
    });
  }
  onSubUpdate(data: any) {
    this.newAnimal.$key = data.$key;
    this.newAnimal.type = data.type;
    this.newAnimal.name = data.name;
    this.newAnimal.weight = data.weight;
    this.firebService.updateAnimal(this.newAnimal).then(res => {
      console.log('updated');
    });
  }
  queryAnimals() {
    this.firebService.getByQuery().snapshotChanges().subscribe(res => {
      res.forEach(t => {
        const todo = t.payload.toJSON();
        todo['$key'] = t.key;
        this.animalsq.push(todo as Animal);
      });
      console.log(this.animalsq);
    });
  }
  proc() {
    this.animals.forEach(el => {
      if (el.type === 'ssak') {
        this.newAnimal.$key = el.$key;
        this.newAnimal.type = el.type;
        this.newAnimal.name = el.name;
        this.newAnimal.weight = '5';
        this.firebService.updateAnimal(this.newAnimal).then(res => {
          console.log('processed');
        });
      }
    });
  }
}
