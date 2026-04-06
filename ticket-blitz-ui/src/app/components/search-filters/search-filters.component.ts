import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface SearchFilters {
  searchTerm: string;
  dateFrom: string;
  dateTo: string;
  priceMin: number;
  priceMax: number;
  status: string;
}

@Component({
  selector: 'app-search-filters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-filters.component.html',
  styleUrls: ['./search-filters.component.css'],
})
export class SearchFiltersComponent {
  @Output() filtersChange = new EventEmitter<SearchFilters>();

  filters: SearchFilters = {
    searchTerm: '',
    dateFrom: '',
    dateTo: '',
    priceMin: 0,
    priceMax: 0,
    status: '',
  };

  onFiltersChange() {
    this.filtersChange.emit(this.filters);
  }

  clearFilters() {
    this.filters = {
      searchTerm: '',
      dateFrom: '',
      dateTo: '',
      priceMin: 0,
      priceMax: 0,
      status: '',
    };
    this.onFiltersChange();
  }
}
