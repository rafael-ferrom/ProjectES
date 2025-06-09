<template>
  <v-container class="pt-8">
    <v-row class="d-flex pb-8">
      <v-col class="d-flex justify-center text-h3 font-weight-light text-center primary--text">
        {{ $t('activeMedicationsView.myActiveMedicines') }}
      </v-col>
    </v-row>

    <v-row v-if="loading" justify="center">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
        <p class="mt-4">Loading your medications...</p>
      </v-col>
    </v-row>

    <v-row v-else-if="!userConfiguredMedicamentos.length" justify="center">
      <v-col cols="12" md="8">
        <v-alert type="info" prominent border="left" colored-border color="primary">
          {{ $t('activeMedicationsView.noConfiguredMedicine') }}
          <router-link :to="{ name: 'home' }">{{ $t('activeMedicationsView.configureNow') }}</router-link>
        </v-alert>
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col
        v-for="med in processedMedications"
        :key="med.id"
        cols="12"
        md="6"
        lg="4"
      >
        <v-card class="mx-auto d-flex flex-column" outlined elevation="2">
          <v-card-title class="text-h6 font-weight-medium primary--text pb-1">
            {{ med.nome }}
          </v-card-title>
          
          <v-card-subtitle class="pt-0 pb-2">
            {{ med.dosagem }} &ndash; {{ med.tipo }}
          </v-card-subtitle>

          <v-divider></v-divider>

          <v-card-text class="flex-grow-1">
            <div class="mb-3">
              <strong>{{ $t('activeMedicationsView.treatmentProgress', { duration: med.durationDays }) }}</strong>
              <span class="float-right">
                {{ $t('activeMedicationsView.daysProgress', { daysPassed: med.daysPassed, duration: med.durationDays }) }}
              </span>
              <v-progress-linear :value="med.progressPercentage" color="light-blue" height="15" striped rounded class="mt-2">
                <template v-slot:default="{ value }">
                  <strong>{{ Math.ceil(value) }}%</strong>
                </template>
              </v-progress-linear>
              <div v-if="med.isCompleted" class="mt-2 success--text font-weight-bold text-center">
                <v-icon color="success">mdi-check-circle</v-icon> {{ $t('activeMedicationsView.treatmentCompleted') }}
              </div>
            </div>

            <v-list dense two-line class="pa-0">
              <v-list-item>
                <v-list-item-icon><v-icon>mdi-calendar-range</v-icon></v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>Treatment Period</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDate(med.frequencia.dataInicio) }} to {{ formatDate(med.frequencia.dataTermino) }}</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
              <v-list-item>
                <v-list-item-icon><v-icon>mdi-pill</v-icon></v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>Frequency</v-list-item-title>
                  <v-list-item-subtitle>{{ med.frequencia.vezesPorDia }} times per day</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
            </v-list>
            
            <v-expansion-panels flat class="mt-3">
              <v-expansion-panel>
                <v-expansion-panel-header>Medication Info & Instructions {{med.instrucoes}}</v-expansion-panel-header>
                <v-expansion-panel-content>
                  <strong>{{ med.bula.nomeComercial }} ({{ med.bula.principioAtivo }})</strong>
                  <p class="text-caption grey--text">{{ med.bula.fabricante }} - {{ med.bula.apresentacao }}</p>
                  <v-divider class="my-2"></v-divider>
                  <ul v-if="med.instrucoes.length" class="pl-5">
                    <li v-for="(inst, index) in med.instrucoes" :key="index">{{ inst.descricao }}</li>
                  </ul>
                  <p v-else class="text-caption">No specific instructions provided.</p>
                </v-expansion-panel-content>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { useMedicationStore } from "@/store/index.js";
import { mapState, mapActions } from 'pinia';

export default {
  name: 'ActiveMedicationsView',
  data() {
    return {
      loading: true,
      now: new Date(), // Used for progress calculation
    }
  },
  computed: {
    ...mapState(useMedicationStore, ['userConfiguredMedicamentos']),

    processedMedications() {
      // Process the raw data from the store to add calculated fields for the UI
      return this.userConfiguredMedicamentos.map(med => {
        const startDate = new Date(med.frequencia.dataInicio + 'T00:00:00');
        const endDate = new Date(med.frequencia.dataTermino + 'T23:59:59');
        const totalDurationMs = endDate - startDate;
        const passedMs = this.now - startDate;

        const durationDays = Math.round(totalDurationMs / (1000 * 60 * 60 * 24));
        const daysPassed = Math.max(0, Math.min(durationDays, Math.floor(passedMs / (1000 * 60 * 60 * 24))));
        
        const progressPercentage = durationDays > 0 ? (passedMs / totalDurationMs) * 100 : 0;
        
        return {
          ...med,
          durationDays: durationDays,
          daysPassed: daysPassed,
          progressPercentage: Math.min(100, Math.max(0, progressPercentage)),
          isCompleted: this.now > endDate,
        };
      }).sort((a, b) => new Date(a.frequencia.dataInicio) - new Date(b.frequencia.dataInicio));
    }
  },
  methods: {
    ...mapActions(useMedicationStore, ['fetchUserConfiguredMedicamentos']),

    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const options = { year: 'numeric', month: 'short', day: 'numeric' };
      return new Date(dateString + 'T00:00:00').toLocaleDateString('en-US', options);
    }
  },
  async mounted() {
    this.loading = true;
    await this.fetchUserConfiguredMedicamentos();
    this.loading = false;
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