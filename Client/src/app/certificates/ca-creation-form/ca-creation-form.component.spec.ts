import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CaCreationFormComponent } from './ca-creation-form.component';

describe('CaCreationFormComponent', () => {
  let component: CaCreationFormComponent;
  let fixture: ComponentFixture<CaCreationFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CaCreationFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CaCreationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
