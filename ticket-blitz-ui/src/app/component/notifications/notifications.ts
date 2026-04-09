import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface Notification {
  id: string;
  type: 'info' | 'success' | 'warning' | 'error';
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
  actionUrl?: string;
  actionText?: string;
}

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrls: ['./notifications.scss'],
})
export class NotificationsComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  notifications = signal<Notification[]>([]);

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadMockNotifications();
  }

  loadMockNotifications() {
    const mockNotifications: Notification[] = [
      {
        id: '1',
        type: 'success',
        title: 'Booking Confirmed',
        message:
          'Your tickets for Summer Music Festival have been confirmed. Check your email for details.',
        timestamp: new Date(Date.now() - 1000 * 60 * 30).toISOString(), // 30 minutes ago
        read: false,
        actionUrl: '/dashboard',
        actionText: 'View Booking',
      },
      {
        id: '2',
        type: 'info',
        title: 'Event Reminder',
        message:
          "Summer Music Festival starts in 2 days. Don't forget to bring your tickets!",
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(), // 1 day ago
        read: true,
      },
      {
        id: '3',
        type: 'warning',
        title: 'Payment Processing',
        message:
          "Your payment for Tech Conference 2024 is being processed. You'll receive a confirmation once completed.",
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(), // 2 hours ago
        read: false,
        actionUrl: '/dashboard',
        actionText: 'Check Status',
      },
      {
        id: '4',
        type: 'error',
        title: 'Booking Failed',
        message:
          'Unfortunately, your booking for Rock Concert could not be completed due to insufficient tickets.',
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24 * 3).toISOString(), // 3 days ago
        read: true,
      },
    ];

    this.notifications.set(mockNotifications);
  }

  getIcon(type: string): string {
    switch (type) {
      case 'info':
        return 'ℹ️';
      case 'success':
        return '✅';
      case 'warning':
        return '⚠️';
      case 'error':
        return '❌';
      default:
        return '🔔';
    }
  }

  formatTime(timestamp: string): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;

    return date.toLocaleDateString();
  }

  unreadCount(): number {
    return this.notifications().filter((n) => !n.read).length;
  }

  markAsRead(notification: Notification) {
    if (!notification.read) {
      this.notifications.update((notifications) =>
        notifications.map((n) =>
          n.id === notification.id ? { ...n, read: true } : n,
        ),
      );
    }
  }

  markAllAsRead() {
    this.notifications.update((notifications) =>
      notifications.map((n) => ({ ...n, read: true })),
    );
  }

  deleteNotification(notification: Notification, event: Event) {
    event.stopPropagation();
    this.notifications.update((notifications) =>
      notifications.filter((n) => n.id !== notification.id),
    );
  }

  clearAll() {
    if (confirm('Are you sure you want to clear all notifications?')) {
      this.notifications.set([]);
    }
  }

  performAction(notification: Notification, event: Event) {
    event.stopPropagation();
    if (notification.actionUrl) {
      this.router.navigate([notification.actionUrl]);
    }
  }
}
