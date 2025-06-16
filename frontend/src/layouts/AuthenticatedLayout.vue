<template>
  <div>
    <tool-bar></tool-bar>

    <v-main style="min-height: 100%;">
      <router-view style="min-height: 100%;" />
    </v-main>

    <notification-drawer></notification-drawer>
    <request-help-dialog :isXSmall="isXSmall" :isSmall="isSmall" :isMedium="isMedium" :isLarge="isLarge"
      :isXLarge="isXLarge">
    </request-help-dialog>
  </div>
</template>

<script>
import RequestHelpDialog from "./../components/RequestHelpDialog.vue"
import { useDisplayStore, useAuthStore, useMedicationStore, useNotificationStore } from "./../store/index"
import { mapState, mapActions } from "pinia"
import ToolBar from "./../components/ToolBar.vue"
import NotificationDrawer from "./../components/NotificationDrawer.vue"

export default {
  components: {
    RequestHelpDialog,
    ToolBar,
    NotificationDrawer
  },
  data() {
    return {
      notificationInterval: null,
      notifiedDoses: new Set(), // Para não repetir notificações para a mesma dose
    }
  },
  computed: {
    ...mapState(useDisplayStore, ["isXSmall", "isSmall", "isMedium", "isLarge", "isXLarge"]),
    ...mapState(useMedicationStore, ['userConfiguredMedicamentos']),
  },
  methods: {
    ...mapActions(useAuthStore, ["getAuthenticated"]),
    ...mapActions(useMedicationStore, ['fetchUserConfiguredMedicamentos']),
    ...mapActions(useNotificationStore, ['fetchNotifications', 'addNotification']),

    checkMedicationStatus() {
      if (!this.userConfiguredMedicamentos.length) return;
      const agora = new Date();

      this.userConfiguredMedicamentos.forEach(med => {
        // Ignora tratamentos concluídos
        if (new Date(med.frequencia.dataTermino + 'T23:59:59') < agora) return;
        if (!med.doses || med.doses.length === 0) return;

        const ultimaDose = new Date(med.doses.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))[0].timestamp + 'Z');
        const intervaloHoras = 24 / med.frequencia.vezesPorDia;
        const proximaDoseEsperada = new Date(ultimaDose.getTime() + intervaloHoras * 3600 * 1000);

        const doseKey = `${med.id}-${proximaDoseEsperada.toISOString()}`;

        const diffMillis = proximaDoseEsperada.getTime() - agora.getTime();
        const diffMinutes = Math.round(diffMillis / 60000);

        // >>>>> LÓGICA DE LEMBRETE (NOVA) <<<<<
        // Se a dose for nos próximos 15 minutos E ainda não foi notificada
        if (diffMinutes > 0 && diffMinutes <= 15) {
          if (!this.notifiedDoses.has(doseKey)) {
            this.addNotification({
              mensagem: `Lembrete: Sua dose de ${med.nome} é em aproximadamente ${diffMinutes} min.`,
              tipo: 'LEMBRETE_DOSE' // Novo tipo de notificação
            });
            this.notifiedDoses.add(doseKey);
          }
        }

        // >>>>> LÓGICA DE DOSE ATRASADA (EXISTENTE) <<<<<
        // Se a dose estiver atrasada há mais de 1 hora E ainda não foi notificada
        const umaHoraDepois = new Date(proximaDoseEsperada.getTime() + 3600 * 1000);
        if (agora > umaHoraDepois) {
          if (!this.notifiedDoses.has(doseKey)) {
            this.addNotification({
              mensagem: `Atenção: A dose de ${med.nome} está atrasada!`,
              tipo: 'DOSE_ATRASADA'
            });
            this.notifiedDoses.add(doseKey);
          }
        }
      });
    }
  },
  async mounted() {
    this.getAuthenticated();
    await this.fetchNotifications();
    await this.fetchUserConfiguredMedicamentos();

    this.notificationInterval = setInterval(this.checkMedicationStatus, 60000); // Roda a cada 60 segundos
  },
  beforeDestroy() {
    clearInterval(this.notificationInterval);
  }
}
</script>