import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import ToastService from 'primevue/toastservice'

// Composants PrimeVue — préfixe Pv pour éviter les conflits HTML natifs
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Password from 'primevue/password'
import Select from 'primevue/select'
import Message from 'primevue/message'
import Toast from 'primevue/toast'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Dialog from 'primevue/dialog'
import Skeleton from 'primevue/skeleton'
import Divider from 'primevue/divider'
import Avatar from 'primevue/avatar'
import Badge from 'primevue/badge'
import ProgressBar from 'primevue/progressbar'
import Textarea from 'primevue/textarea'
import BadgeDecision from '@/components/BadgeDecision.vue'


import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: false
    }
  }
})
app.use(ToastService)

// Enregistrement global avec préfixe Pv
app.component('PvButton', Button)
app.component('PvInputText', InputText)
app.component('PvInputNumber', InputNumber)
app.component('PvPassword', Password)
app.component('PvSelect', Select)
app.component('PvMessage', Message)
app.component('PvToast', Toast)
app.component('PvCard', Card)
app.component('PvTag', Tag)
app.component('PvDataTable', DataTable)
app.component('PvColumn', Column)
app.component('PvDialog', Dialog)
app.component('PvSkeleton', Skeleton)
app.component('PvDivider', Divider)
app.component('PvAvatar', Avatar)
app.component('PvBadge', Badge)
app.component('PvProgressBar', ProgressBar)
app.component('PvTextarea', Textarea)
app.component('BadgeDecision', BadgeDecision)

app.mount('#app')
