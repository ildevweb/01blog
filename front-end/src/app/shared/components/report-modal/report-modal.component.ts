import { Component, EventEmitter, Output, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-report-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './report-modal.component.html'
})
export class ReportModalComponent {
  @Output() submitReport = new EventEmitter<string>();

  @ViewChild('reasonSpam') reasonSpam!: ElementRef<HTMLInputElement>;
  @ViewChild('reasonHate') reasonHate!: ElementRef<HTMLInputElement>;
  @ViewChild('reasonInappropriate') reasonInappropriate!: ElementRef<HTMLInputElement>;
  @ViewChild('additionalReason') additionalReason!: ElementRef<HTMLInputElement>;

  onSubmit(): void {
    const reasonSpam = this.reasonSpam?.nativeElement?.checked;
    const reasonHate = this.reasonHate?.nativeElement?.checked;
    const reasonInappropriate = this.reasonInappropriate?.nativeElement?.checked;
    const additional = this.additionalReason?.nativeElement?.value?.trim() ?? '';
    const selectedReason = reasonSpam ? 'Spam' : reasonHate ? 'Hate Speech' : reasonInappropriate ? 'Inappropriate Content' : additional;
    this.submitReport.emit(selectedReason);
  }
}
