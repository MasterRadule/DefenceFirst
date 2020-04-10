import {Pipe, PipeTransform} from '@angular/core';
import {Mode} from '../model/mode.enum';

@Pipe({
  name: 'modePipe'
})
export class ModePipe implements PipeTransform {

  transform(value: Mode): any {
    switch (value) {
      case Mode.PENDING:
        return 'Create';
      case Mode.ACTIVE:
        return 'Revoke';
      default:
        return 'View';
    }
  }

}
