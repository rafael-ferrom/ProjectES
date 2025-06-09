<template>
  <v-container class="pt-8">
    <v-row class="d-flex pb-8">
      <v-col class="d-flex justify-center text-h3 font-weight-light text-center primary--text">
        Meus Tratamentos Ativos
      </v-col>
    </v-row>

    <v-row v-if="loading" justify="center">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
      </v-col>
    </v-row>

    <v-row v-else-if="!userConfiguredMedicamentos.length" justify="center">
      <v-col cols="12" md="8">
        <v-alert type="info" prominent border="left" colored-border color="primary">
          Você ainda não configurou nenhum tratamento.
          <router-link :to="{ name: 'home' }">Configure um agora!</router-link>
        </v-alert>
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col v-for="med in processedMedications" :key="med.id" cols="12" md="6" lg="4">
        <v-card class="mx-auto d-flex flex-column" outlined elevation="2">
          <v-card-title class="text-h6 font-weight-medium primary--text pb-1">
            {{ med.nome }}
          </v-card-title>
          <v-card-subtitle class="pt-0 pb-2">
            {{ med.dosagem }} &ndash; {{ med.tipo }}
          </v-card-subtitle>

          <v-divider></v-divider>

          <template v-if="med.frequencia.primeiraDoseTimestamp">
            <v-card-text class="flex-grow-1">
              <div class="mb-3">
                <strong>Progresso do Tratamento ({{ med.durationDays }} dias)</strong>
                <v-progress-linear :value="med.progressPercentage" color="light-blue" height="15" striped rounded class="mt-2">
                  <template v-slot:default="{ value }">
                    <strong>{{ Math.ceil(value) }}%</strong>
                  </template>
                </v-progress-linear>
              </div>

              <v-list dense two-line class="pa-0">
                <v-list-item>
                  <v-list-item-icon><v-icon>mdi-calendar-check</v-icon></v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>Primeira dose em:</v-list-item-title>
                    <v-list-item-subtitle>{{ formatDateTime(med.frequencia.primeiraDoseTimestamp) }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item v-if="!med.isCompleted">
                  <v-list-item-icon><v-icon>mdi-clock-time-eight-outline</v-icon></v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>Próxima dose em:</v-list-item-title>
                    <v-list-item-subtitle class="font-weight-bold primary--text">{{ formatDateTime(med.nextDoseTime) }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                 <v-list-item v-else>
                    <v-list-item-icon><v-icon color="success">mdi-check-all</v-icon></v-list-item-icon>
                    <v-list-item-content>
                        <v-list-item-title class="success--text font-weight-bold">Tratamento Concluído!</v-list-item-title>
                    </v-list-item-content>
                 </v-list-item>
              </v-list>
            </v-card-text>
          </template>

          <template v-else>
            <v-card-text class="text-center flex-grow-1 d-flex flex-column justify-center align-center pa-8">
              <p class="text-body-1">Tratamento pronto para começar.</p>
              <v-btn color="success" @click="handleStartTreatment(med.id)" :loading="med.starting">
                <v-icon left>mdi-play-circle-outline</v-icon>
                Tomar 1ª Dose
              </v-btn>
            </v-card-text>
          </template>
          
          <v-divider></v-divider>
           <v-card-actions>
             <v-expansion-panels flat>
              <v-expansion-panel>
                <v-expansion-panel-header>Detalhes e Instruções</v-expansion-panel-header>
                <v-expansion-panel-content>
                   <strong>{{ med.bula.nomeComercial }} ({{ med.bula.principioAtivo }})</strong>
                   <ul v-if="med.instrucoes.length" class="pl-5 mt-2">
                    <li v-for="(inst, index) in med.instrucoes" :key="index">{{ inst.descricao }}</li>
                  </ul>
                </v-expansion-panel-content>
              </v-expansion-panel>
            </v-expansion-panels>
           </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { useMedicationStore } from "../store/index.js";
import { mapState, mapActions } from 'pinia';

export default {
  name: 'ActiveMedicationsView',
  data() {
    return {
      loading: true,
      now: new Date(),
      timer: null,
    }
  },
  computed: {
    ...mapState(useMedicationStore, ['userConfiguredMedicamentos']),

    processedMedications() {
      return this.userConfiguredMedicamentos.map(med => {
        const { frequencia } = med;
        const endDate = new Date(frequencia.dataTermino + 'T23:59:59');
        let nextDoseTime = null;

        // Calculations only for started treatments
        if (frequencia.primeiraDoseTimestamp) {
          // const startDate = new Date(frequencia.dataInicio + 'T00:00:00');
          const firstDoseDate = new Date(frequencia.primeiraDoseTimestamp);
          const intervalHours = frequencia.vezesPorDia > 0 ? 24 / frequencia.vezesPorDia : 0;
          
          let currentDose = firstDoseDate.getTime();
          while (currentDose < this.now.getTime()) {
            currentDose += intervalHours * 60 * 60 * 1000;
          }
          nextDoseTime = new Date(currentDose);
          
          // If calculated next dose is past the treatment end, there is no next dose
          if (nextDoseTime > endDate) {
              nextDoseTime = null;
          }
        }
        
        return {
          ...med,
          starting: false, // UI flag for loading state
          nextDoseTime,
          isCompleted: this.now > endDate,
        };
      });
    }
  },
  methods: {
    ...mapActions(useMedicationStore, [
      'fetchUserConfiguredMedicamentos',
      'startTreatment',
    ]),
    
    handleStartTreatment(medicationId) {
        const med = this.userConfiguredMedicamentos.find(m => m.id === medicationId);
        if (med) this.$set(med, 'starting', true); // Show loading on the button
        
        try {
          this.startTreatment(medicationId);
          this.fetchUserConfiguredMedicamentos(); // Refresh the list from the backend
        } catch (error) {
          console.error("Failed to start treatment:", error);
        } finally {
          if (med) this.$set(med, 'starting', false);
        }
    },

    formatDateTime(dateString) {
      if (!dateString) return 'N/A';
      const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(dateString).toLocaleDateString('pt-BR', options);
    },
  },
  async mounted() {
    this.loading = true;
    await this.fetchUserConfiguredMedicamentos();
    this.loading = false;

    this.timer = setInterval(() => {
      this.now = new Date();
    }, 60000); // Update current time every minute
  },
  beforeDestroy() {
      clearInterval(this.timer);
  }
};
</script>

<style scoped>
.v-card__title {
  word-break: break-word;
}
.v-progress-linear strong {
  color: white;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
}
.flex-grow-1 {
  flex-grow: 1 !important;
}
</style>