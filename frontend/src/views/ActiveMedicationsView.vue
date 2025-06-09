<template>
  <v-container class="pt-8">
    <v-row class="d-flex pb-8">
      <v-col class="d-flex justify-center text-h3 font-weight-light text-center primary--text">
        Meus Tratamentos Ativos
      </v-col>
    </v-row>

    <v-row v-if="loading" justify="center" class="pa-12">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
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
          <v-img :src="med.bula.fotoUrl" height="100px" contain class="ma-2"></v-img>
          <v-card-title class="text-h6 font-weight-medium primary--text pb-1">
            {{ med.nome }}
          </v-card-title>
          <v-card-subtitle class="pt-0 pb-2">
            {{ med.dosagem }} &ndash; {{ med.tipo }}
          </v-card-subtitle>

          <v-divider></v-divider>

          <v-card-text class="flex-grow-1">
            <div class="mb-3">
              <strong>Progresso de Doses</strong>
              <span class="float-right">Dose {{ med.dosesTaken }} de {{ med.totalDosesInTreatment }}</span>
              <v-progress-linear :value="med.doseProgress" color="teal" height="15" striped rounded class="mt-2">
                <template v-slot:default="{ value }">
                  <strong>{{ Math.ceil(value) }}%</strong>
                </template>
              </v-progress-linear>
            </div>

            <v-list dense two-line class="pa-0">
              <v-list-item>
                <v-list-item-icon><v-icon>mdi-calendar-range</v-icon></v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>Período do Tratamento</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDate(med.frequencia.dataInicio) }} a {{ formatDate(med.frequencia.dataTermino) }}</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
              <v-list-item>
                <v-list-item-icon><v-icon>mdi-pill</v-icon></v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>Frequência</v-list-item-title>
                  <v-list-item-subtitle>{{ med.frequencia.vezesPorDia }} vezes por dia</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>

              <v-list-item v-if="med.lastDose">
                  <v-list-item-icon><v-icon>mdi-history</v-icon></v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>Última dose em:</v-list-item-title>
                    <v-list-item-subtitle>{{ formatDateTime(med.lastDose.timestamp) }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item v-if="!med.isCompleted && med.nextDoseTime">
                  <v-list-item-icon><v-icon>mdi-clock-time-eight-outline</v-icon></v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>Próxima dose em:</v-list-item-title>
                    <v-list-item-subtitle class="font-weight-bold primary--text">{{ formatDateTime(med.nextDoseTime) }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
            </v-list>
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions class="d-flex flex-column align-stretch pa-0">
              <div class="pa-4 text-center">
                <div v-if="med.isCompleted" class="text-center success--text font-weight-bold">
                    <v-icon color="success">mdi-check-all</v-icon> Tratamento Concluído!
                </div>
                <v-btn v-else-if="!med.isStarted" color="success" large block @click="handleRecordDose(med.id)" :loading="med.isRecording">
                    <v-icon left>mdi-play-circle</v-icon> Tomar 1ª Dose
                </v-btn>
                <v-btn v-else color="success" large block @click="handleRecordDose(med.id)" :loading="med.isRecording" :disabled="!med.isDoseDue">
                    <v-icon left>mdi-plus-circle</v-icon> Tomar Próxima Dose
                </v-btn>
              </div>

              <v-expansion-panels flat class="mt-0">
                <v-expansion-panel>
                  <v-expansion-panel-header>Medication Info & Instructions</v-expansion-panel-header>
                  <v-expansion-panel-content>
                    <strong>{{ med.bula.nomeComercial }} ({{ med.bula.principioAtivo }})</strong>
                    <p class="text-caption grey--text">{{ med.bula.fabricante }} - {{ med.bula.apresentacao }}</p>
                    <v-divider class="my-2"></v-divider>
                    <p v-if="!med.instrucoes.length" class="text-caption">Nenhuma instrução específica salva.</p>
                    <ul v-else class="pl-5">
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
import { useMedicationStore } from "@/store/index.js";
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
        const sortedDoses = [...med.doses].sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
        const lastDose = sortedDoses.length > 0 ? sortedDoses[0] : null;
        const isStarted = !!lastDose;

        const { frequencia } = med;
        const intervalHours = frequencia.vezesPorDia > 0 ? 24 / frequencia.vezesPorDia : 0;
        const startDate = new Date(frequencia.dataInicio + 'T00:00:00');
        const endDate = new Date(frequencia.dataTermino + 'T23:59:59');
        
        const durationInMs = endDate - startDate;
        const durationDays = durationInMs > 0 ? Math.ceil(durationInMs / (1000 * 60 * 60 * 24)) : 1;
        const totalDosesInTreatment = durationDays * frequencia.vezesPorDia;

        let nextDoseTime = null;
        if (isStarted) {
            const lastDoseTime = new Date(lastDose.timestamp);
            nextDoseTime = new Date(lastDoseTime.getTime() + intervalHours * 60 * 60 * 1000);
        }

        const dosesTaken = med.doses.length;
        const doseProgress = totalDosesInTreatment > 0 ? (dosesTaken / totalDosesInTreatment) * 100 : 0;
        
        return {
          ...med,
          isRecording: false,
          isStarted,
          lastDose,
          nextDoseTime,
          isDoseDue: nextDoseTime ? this.now >= nextDoseTime : false,
          isCompleted: this.now > endDate || (totalDosesInTreatment > 0 && dosesTaken >= totalDosesInTreatment),
          dosesTaken,
          totalDosesInTreatment,
          doseProgress: Math.min(100, doseProgress),
        };
      });
    }
  },
  methods: {
    ...mapActions(useMedicationStore, ['fetchUserConfiguredMedicamentos', 'recordDose']),
    
    async handleRecordDose(medicationId) {
        const med = this.userConfiguredMedicamentos.find(m => m.id === medicationId);
        if (med) this.$set(med, 'isRecording', true);
        
        try {
            await this.recordDose(medicationId);
            await this.fetchUserConfiguredMedicamentos();
        } catch (error) {
            console.error("Failed to record dose:", error);
        } finally {
            if (med) this.$set(med, 'isRecording', false);
        }
    },

    formatDateTime(dateString) {
      if (!dateString) return 'N/A';
      const options = { year: '2-digit', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
      return new Date(dateString).toLocaleString('pt-BR', options);
    },
    
    // NOVO: Método para formatar apenas a data
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      // Adiciona T00:00:00 para evitar problemas com fuso horário
      return new Date(dateString + 'T00:00:00').toLocaleDateString('pt-BR', options);
    }
  },
  async mounted() {
    await this.fetchUserConfiguredMedicamentos();
    this.loading = false;
    this.timer = setInterval(() => { this.now = new Date(); }, 1000);
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