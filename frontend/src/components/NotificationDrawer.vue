<template>
  <div>
    <v-navigation-drawer v-model="isDrawerOpen" app temporary right width="400">
      <v-list-item>
        <v-list-item-content>
          <v-list-item-title class="text-h6">
            Central de Notificações
          </v-list-item-title>
        </v-list-item-content>
        <v-list-item-action>
          <v-btn icon @click="toggleDrawer">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-list-item-action>
      </v-list-item>

      <v-divider></v-divider>

      <v-list two-line v-if="notifications.length > 0">
        <v-list-item v-for="n in notifications" :key="n.id">
          <v-list-item-icon class="mr-4">
            <v-icon :color="n.tipo === 'DOSE_ATRASADA' ? 'warning' : 'primary'">
              {{ n.tipo === 'DOSE_ATRASADA' ? 'mdi-alert-circle' : 'mdi-information' }}
            </v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title :class="{ 'font-weight-bold': !n.lida }">{{ n.mensagem }}</v-list-item-title>
            <v-list-item-subtitle>{{ formatTimestamp(n.timestamp) }}</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>
      <div v-else class="text-center pa-4 grey--text">
        Nenhuma notificação ainda.
      </div>
    </v-navigation-drawer>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="5000" top right>
      {{ snackbar.text }}
      <template v-slot:action="{ attrs }">
        <v-btn text v-bind="attrs" @click="snackbar.show = false">Fechar</v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
import { useNotificationStore } from '@/store';
import { mapState, mapWritableState, mapActions } from 'pinia';

export default {
  name: 'NotificationDrawer',
  computed: {
    ...mapState(useNotificationStore, ['notifications', 'snackbar']),
    ...mapWritableState(useNotificationStore, ['isDrawerOpen']),
  },
  methods: {
    ...mapActions(useNotificationStore, ['toggleDrawer']),
    formatTimestamp(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp + 'Z');

      return new Intl.DateTimeFormat('pt-BR', {
        timeZone: 'America/Sao_Paulo',
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    }
  }
}
</script>