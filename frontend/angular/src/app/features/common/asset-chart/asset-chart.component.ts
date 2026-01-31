import {ChangeDetectorRef, Component, effect, input, Input, ViewChild} from '@angular/core';
import { ChartConfiguration, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { CommonModule } from '@angular/common';
import 'chartjs-adapter-date-fns';
import {MarketDataApiService} from '../../../core/services/market-data-api.service';
import {Asset} from '../../../core/models/asset.model';


@Component({
  selector: 'app-asset-chart',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './asset-chart.component.html',
  styleUrls: ['./asset-chart.component.scss']
})
export class AssetChartComponent {
  asset = input<Asset>()
  @Input() label: string = 'Évolution du cours';
  @Input() color: string = '#4F46E5';

  currentAsset?: Asset;
  isLoadingChart = true;

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  public lineChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  };

  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    elements: {
      point: { radius: 0, hitRadius: 10 }, // Cache les points pour un look "lisse"
      line: { tension: 0.4 } // Courbe lissée (Bézier)
    },
    scales: {
      x: {
        type: 'time',
        time: { unit: 'day' },
        grid: { display: false } // Pas de grille verticale (plus propre)
      },
      y: {
        grid: { color: '#e5e7eb' } // Grille légère
      }
    },
    plugins: {
      legend: { display: false }, // On cache la légende par défaut
      tooltip: { mode: 'index', intersect: false }
    }
  };

  public lineChartType: ChartType = 'line';

  constructor(private cdr: ChangeDetectorRef, private marketService: MarketDataApiService) {
    effect(() => {
      const asset = this.asset();
      if(asset && asset.symbol !== this.currentAsset?.symbol) {
        this.currentAsset = asset;
        this.loadData();
      }
    });
  }

  private loadData(): void {
    const asset = this.asset();
    if (!asset) return;

    this.isLoadingChart = true;

    this.marketService.getHistory(asset.symbol!, asset.type!, asset.exchange).subscribe({
      next: (data) => {
        this.lineChartData = {
          datasets: [{
            data: data.map(p => p.price),
            label: asset.symbol,
            borderColor: this.color,
            backgroundColor: this.createGradient(this.color),
            fill: true, // Effet "Area Chart" rempli
            borderWidth: 2
          }],
          labels: data.map(p => p.date)
        };
        this.isLoadingChart = false;
        this.cdr.detectChanges();
        this.chart?.update();
      },
      error: (err) => console.error('Erreur chargement graphique', err)
    });
  }

  // Petite astuce pour un dégradé joli sous la courbe
  private createGradient(color: string): any {
    const ctx = document.createElement('canvas').getContext('2d');
    if (!ctx) return color;
    const gradient = ctx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, color + '80'); // Couleur à 50% opacité
    gradient.addColorStop(1, '#ffffff00'); // Transparent en bas
    return gradient;
  }
}
