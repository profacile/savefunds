import type { ToastServiceMethods } from 'primevue/toastservice'

let _toast: ToastServiceMethods | null = null

export function setToastInstance(toast: ToastServiceMethods) {
  _toast = toast
}

export function showToast(
  severity: 'success' | 'info' | 'warn' | 'error',
  summary: string,
  detail: string,
  life = 5000
) {
  _toast?.add({ severity, summary, detail, life })
}
