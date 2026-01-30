import { Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { ChartConfiguration, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { CommonModule } from '@angular/common';
import 'chartjs-adapter-date-fns';
import {MarketDataApiService} from '../../../core/services/market-data-api.service';
import {AssetType} from '../../../core/models/asset.model'; // Important pour les dates !

@Component({
  selector: 'app-asset-chart',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './asset-chart.component.html',
  styleUrls: ['./asset-chart.component.scss']
})
export class AssetChartComponent implements OnChanges {
  @Input() symbol: string = '';
  @Input() assetType: string = 'STOCK';
  @Input() exchange: string = '';
  @Input() label: string = 'Évolution du cours';
  @Input() color: string = '#4F46E5'; // Une belle couleur Indigo par défaut

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

  constructor(private marketService: MarketDataApiService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['symbol'] && this.symbol) {
      this.loadData();
    }
  }

  private loadData(): void {
    this.marketService.getHistory(this.symbol, this.assetType, this.exchange).subscribe({
      next: (data) => {
        // Préparation des données pour Chart.js
        this.lineChartData = {
          datasets: [{
            data: data.map(p => p.price),
            label: this.symbol,
            borderColor: this.color,
            backgroundColor: this.createGradient(this.color),
            fill: true, // Effet "Area Chart" rempli
            borderWidth: 2
          }],
          labels: data.map(p => p.date)
        };

        // Force le rafraîchissement
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
