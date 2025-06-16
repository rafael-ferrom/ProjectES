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

          <v-alert
            :type="med.stock.alertType"
            dense text border="left" class="mx-4"
          >
            <strong>Estoque:</strong> {{ med.stock.message }}
            <div v-if="med.stock.isExpired" class="mt-1 text-caption">
                Validade da caixa atual: {{ formatDate(med.stock.expiryDate) }}
            </div>
          </v-alert>

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
                <v-btn
                  v-else
                  color="success"
                  large
                  block
                  @click="handleRecordDose(med.id)"
                  :loading="med.isRecording"
                  :disabled="!med.canTakeDose"
                >
                  <v-icon left>{{ med.isStarted ? 'mdi-plus-circle' : 'mdi-play-circle' }}</v-icon>
                  {{ med.isStarted ? 'Tomar Próxima Dose' : 'Tomar 1ª Dose' }}
                </v-btn>
              </div>

              <v-expansion-panels flat class="mt-0">
                <v-expansion-panel>
                  <v-expansion-panel-header>Info & Instruções</v-expansion-panel-header>
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
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="5000">
        {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<script>
import { useMedicationStore } from "@/store/index.js";
import { mapState, mapActions, mapGetters } from 'pinia';

export default {
  name: 'ActiveMedicationsView',
  data() {
    return {
      loading: true,
      now: new Date(),
      timer: null,
      snackbar: { show: false, text: '', color: '' },
    }
  },
  computed: {
    ...mapState(useMedicationStore, ['userConfiguredMedicamentos']),
    ...mapGetters(useMedicationStore, ['getStockForBula']),

    processedMedications() {
      const today = new Date().toISOString().slice(0, 10);

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
        
        const stockInfo = this.getStockForBula(med.bula.id);
        const hasStock = stockInfo.totalPills > 0;
        const currentBox = stockInfo.boxes.sort((a,b) => new Date(a.dataValidade) - new Date(b.dataValidade))[0];
        const isCurrentBoxExpired = currentBox ? currentBox.dataValidade < today : false;
        
        let stockMessage = 'Sem estoque. Compre mais.';
        let alertType = 'error';
        if(hasStock) {
            const totalBoxes = stockInfo.boxes.length;
            stockMessage = `${stockInfo.totalPills} comprimidos em ${totalBoxes} caixa(s).`;
            alertType = 'success';
            if (stockInfo.totalPills < med.frequencia.vezesPorDia * 3) {
                alertType = 'warning';
                stockMessage += ' (Estoque baixo!)';
            }
        }
        if (isCurrentBoxExpired) {
            stockMessage = 'Caixa atual está vencida!';
            alertType = 'error';
        }

        return {
          ...med,
          isRecording: false,
          isStarted,
          lastDose,
          nextDoseTime,
          isDoseDue: nextDoseTime ? this.now >= nextDoseTime : true,
          isCompleted: this.now > endDate || (totalDosesInTreatment > 0 && dosesTaken >= totalDosesInTreatment),
          dosesTaken,
          totalDosesInTreatment,
          doseProgress: Math.min(100, doseProgress),
          stock: {
              hasStock,
              isExpired: isCurrentBoxExpired,
              expiryDate: currentBox?.dataValidade,
              message: stockMessage,
              alertType
          },
          canTakeDose: hasStock && !isCurrentBoxExpired && (nextDoseTime ? this.now >= nextDoseTime : true),
        };
      });
    }
  },
  methods: {
    ...mapActions(useMedicationStore, ['fetchUserConfiguredMedicamentos', 'recordDose', 'fetchUserStock']),
    
    async handleRecordDose(medicationId) {
        const med = this.processedMedications.find(m => m.id === medicationId);
        if (!med) return;
        
        med.isRecording = true;
        
        try {
            await this.recordDose(medicationId);
            this.snackbar = { show: true, text: 'Dose registrada com sucesso!', color: 'success' };
        } catch (error) {
            console.error("Failed to record dose:", error);
            this.snackbar = { show: true, text: error.message || "Erro ao registrar dose.", color: 'error' };
        } finally {
            med.isRecording = false;
        }
    },

    formatDateTime(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString + 'Z');
      
      return new Intl.DateTimeFormat('pt-BR', {
        timeZone: 'America/Sao_Paulo',
        year: '2-digit', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit'
      }).format(date);
    },
    
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString + 'T00:00:00Z');
      
      return new Intl.DateTimeFormat('pt-BR', {
        timeZone: 'America/Sao_Paulo',
        year: 'numeric', month: 'long', day: 'numeric'
      }).format(date);
    }
  },
  async mounted() {
    this.loading = true;
    await Promise.all([
        this.fetchUserConfiguredMedicamentos(),
        this.fetchUserStock()
    ]);
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