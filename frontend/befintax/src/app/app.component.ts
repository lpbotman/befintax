import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit{
  title = 'befintax';

  constructor(private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
        this.router.navigate(['wallet']);
    }
}
