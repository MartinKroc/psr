import { Injectable } from '@angular/core';
import {AngularFireDatabase, AngularFireList, AngularFireObject} from '@angular/fire/database';
import {Observable} from 'rxjs';
import {Animal} from '../models/animal';
import {AddAnimal} from '../models/add-animal';

@Injectable({
  providedIn: 'root'
})
export class DbServService {
  ani: AngularFireList<Animal>;
  aniDetails: AngularFireObject<Animal>;
  aniQuery: AngularFireList<Animal>;
  aniToProc: any[];
  constructor(public db: AngularFireDatabase) { }

    getAnimals(): AngularFireList<Animal> {
    this.ani = this.db.list('/animals') as AngularFireList<Animal>;
    return this.ani;
  }
  // tslint:disable-next-line:typedef
  addAnimal(an: AddAnimal) {
    this.db.database.ref('/animals').push(an);
  }
  delAnimal(key: string): Promise<void> {
      return this.db.database.ref('/animals/' + key).remove();
  }
  updateAnimal(a: Animal): Promise<void> {
    const reference = this.db.database.ref('/animals/' + a.$key);
    delete a.$key;
    return reference.update({
      ...a
    });
  }
  getAnimalById(key: string): AngularFireObject<Animal> {
    this.aniDetails = this.db.object('/animals/' + key) as AngularFireObject<Animal>;
    return this.aniDetails;
  }
  getByQuery(): AngularFireList<Animal> {
      this.aniQuery = this.db.list('/animals', ref => ref.orderByChild('weight').equalTo('33')) as AngularFireList<Animal>;
      return this.aniQuery;
  }
}
