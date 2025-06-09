<template>
  <v-container class="pt-8">
    <v-row class="d-flex pb-8">
      <v-col class="d-flex justify-center text-h3 font-weight-light text-center primary--text">
        {{ $t('activeMedicationsView.myActiveMedicines') }}
      </v-col>
    </v-row>

    <v-row v-if="!activeMedicationsStore.hasActiveMedicamentos" justify="center">
      <v-col cols="12" md="8">
        <v-alert type="info" prominent border="left" colored-border color="primary">
          {{ $t('activeMedicationsView.noConfiguredMedicine') }}
          <router-link :to="{ name: 'home' }">{{ $t('activeMedicationsView.configureNow') }}</router-link>
        </v-alert>
      </v-col>
    </v-row>

    <v-row>
      <v-col
        v-for="activeMed in processedActiveMedications"
        :key="activeMed.activeId"
        cols="12"
        md="6"
        lg="4"
      >
        <v-card class="mx-auto" outlined elevation="2">
          <v-img
            v-if="activeMed.details && activeMed.details.fotoLink"
            :src="activeMed.details.fotoLink"
            height="150px"
            contain
            class="grey lighten-3"
          >
            <template v-slot:placeholder>
              <v-row class="fill-height ma-0" align="center" justify="center">
                <v-progress-circular indeterminate color="grey lighten-1"></v-progress-circular>
              </v-row>
            </template>
          </v-img>
          <v-avatar v-else tile size="100%" height="150px" color="grey lighten-3" class="d-flex align-center justify-center">
             <v-icon size="50" color="grey">mdi-pill</v-icon>
          </v-avatar>

          <v-card-title class="text-h6 font-weight-medium primary--text pb-1">
            {{ (activeMed.details && activeMed.details.nome) || $t('activeMedicationsView.unknownMedication') }}
          </v-card-title>

          <v-card-subtitle class="pt-0 pb-2">
            {{ $t('activeMedicationsView.startDate') }}: {{ formatDate(activeMed.configurationTimestamp) }}
          </v-card-subtitle>

          <v-divider></v-divider>

          <v-card-text class="pb-1">
            <div class="mb-2">
              <strong>{{ $t('activeMedicationsView.treatmentProgress', { duration: activeMed.dosageInfo.duracaoDias }) }}</strong>
              <span class="float-right">
                {{ $t('activeMedicationsView.daysProgress', { daysPassed: activeMed.daysPassed, duration: activeMed.dosageInfo.duracaoDias }) }}
              </span>
            </div>
            <v-progress-linear
              :value="activeMed.progressPercentage"
              color="light-blue"
              height="15"
              striped
              rounded
            >
              <template v-slot:default="{ value }">
                <strong>{{ Math.ceil(value) }}%</strong>
              </template>
            </v-progress-linear>
            <div v-if="activeMed.isCompleted" class="mt-2 success--text font-weight-bold text-center">
              <v-icon color="success">mdi-check-circle</v-icon> {{ $t('activeMedicationsView.treatmentCompleted') }}
            </div>
          </v-card-text>

          <v-list dense v-if="!activeMed.isCompleted">
            <v-list-item>
              <v-list-item-icon>
                <v-icon>mdi-clock-time-eight-outline</v-icon>
              </v-list-item-icon>
              <v-list-item-content>
                <v-list-item-title>{{ $t('activeMedicationsView.nextDose') }}</v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold primary--text">
                  {{ activeMed.nextDoseTime ? formatDateTime(activeMed.nextDoseTime) : $t('activeMedicationsView.calculating') }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </v-list>

          <v-divider></v-divider>

          <v-card-text class="text-caption">
            <p class="mb-1">
              <strong>{{ $t('activeMedicationsView.interval') }}</strong>
              {{ $t('activeMedicationsView.everyXHours', { interval: activeMed.dosageInfo.intervaloHoras }) }}
            </p>
            <p class="mb-1" v-if="activeMed.userInfo">
              {{ $t('activeMedicationsView.userInfo', { 
                  age: activeMed.userInfo.idade, 
                  weight: activeMed.userInfo.peso, 
                  allergies: activeMed.userInfo.alergias || 'N/A'
              }) }}
            </p>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn small color="error" outlined @click="confirmRemove(activeMed.activeId)">
              <v-icon left small>mdi-delete-outline</v-icon>
              Remover
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="dialogRemove" max-width="400">
      <v-card>
        <v-card-title class="headline">
          {{ $t('activeMedicationsView.removeDialogTitle') }}
        </v-card-title>
        <v-card-text>
          {{ $t('activeMedicationsView.removeDialogMessage') }}
        </v-card-text>
        <v-card-actions>
          <v-btn color="grey" text @click="dialogRemove = false">
            {{ $t('activeMedicationsView.cancel') }}
          </v-btn>
          <v-btn color="red" text @click="executeRemove">
            {{ $t('activeMedicationsView.confirm') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<script>
import { useMedicationStore, useActiveMedicationsStore } from "@/store/index.js";
import { mapStores } from 'pinia';

export default {
  name: 'ActiveMedicationsView',
  data() {
    return {
      now: Date.now(),
      timer: null,
      dialogRemove: false,
      itemToRemove: null,
    }
  },
  computed: {
    ...mapStores(useActiveMedicationsStore, useMedicationStore),
    processedActiveMedications() {
      // Acessa o estado diretamente, pois mapStores o torna disponível
      if (!this.activeMedicationsStore || !this.medicationStore) return [];

      return this.activeMedicationsStore.getAllActiveMedicamentos.map(activeMed => {
        const medicationDetails = this.medicationStore.getMedicamentoById(activeMed.medicationId);
        const startDate = activeMed.configurationTimestamp;
        const durationDays = parseInt(activeMed.dosageInfo.duracaoDias, 10);
        const intervalHours = parseInt(activeMed.dosageInfo.intervaloHoras, 10);

        const msInDay = 24 * 60 * 60 * 1000;
        const daysPassed = Math.floor((this.now - startDate) / msInDay);
        const clampedDaysPassed = Math.max(0, Math.min(daysPassed, durationDays));

        const progressPercentage = durationDays > 0 ? (clampedDaysPassed / durationDays) * 100 : 0;
        const isCompleted = clampedDaysPassed >= durationDays;

        let nextDoseTime = null;
        if (!isCompleted) {
          const intervalMs = intervalHours * 60 * 60 * 1000;
          const treatmentEndMs = startDate + (durationDays * msInDay);
          let currentDoseTime = startDate;

          // A primeira dose é considerada no momento da configuração.
          // Se o horário da primeira dose já passou, avançamos para a próxima.
          if (currentDoseTime < this.now) {
            while (currentDoseTime < this.now) {
              currentDoseTime += intervalMs;
            }
          }
          // Se currentDoseTime ainda for menor que this.now (caso o intervalo seja muito pequeno ou startDate muito no passado)
          // significa que a próxima dose deveria ser agora ou no futuro.
          // Se currentDoseTime >= this.now, esta é a próxima dose futura.

          if (currentDoseTime <= treatmentEndMs) {
            nextDoseTime = currentDoseTime;
          } else {
            // Todas as doses dentro do período de tratamento já passaram
            // Isso pode acontecer se o último intervalo calculado ultrapassar treatmentEndMs
            // mas ainda estamos dentro do período geral de "dias passados" < duração.
            // Neste caso, a próxima dose efetivamente não existe mais dentro do tratamento.
            // O tratamento será marcado como concluído pela lógica isCompleted.
          }
        }

        return {
          ...activeMed,
          details: medicationDetails,
          daysPassed: clampedDaysPassed,
          progressPercentage: Math.min(100, progressPercentage), // Garante que não passe de 100%
          nextDoseTime,
          isCompleted
        };
      }).sort((a, b) => a.configurationTimestamp - b.configurationTimestamp); // Ordena pelos mais antigos primeiro
    }
  },
  methods: {
    formatDate(timestamp) {
      if (!timestamp) return 'N/A';
      return new Date(timestamp).toLocaleDateString('pt-BR');
    },
    formatDateTime(timestamp) {
      if (!timestamp) return 'N/A';
      return new Date(timestamp).toLocaleString('pt-BR', {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
      });
    },
    confirmRemove(activeId) {
      this.itemToRemove = activeId;
      this.dialogRemove = true;
    },
    executeRemove() {
      if (this.itemToRemove) {
        this.activeMedicationsStore.removeActiveMedication(this.itemToRemove);
      }
      this.dialogRemove = false;
      this.itemToRemove = null;
    }
  },
  mounted() {
    // Carrega do localStorage ao montar o componente, se o store ainda não o fez.
    // O ideal é que o store faça isso na sua própria inicialização/criação.
    if (!this.activeMedicationsStore.activeMedicamentos.length) {
         this.activeMedicationsStore.loadActiveMedicamentosFromLocalStorage();
    }
    this.medicationStore.ensureMedicamentosLoaded(); // Garante que os detalhes dos medicamentos base estejam carregados

    // Atualiza 'now' a cada minuto para recalcular os tempos
    this.timer = setInterval(() => {
      this.now = Date.now();
    }, 60000); // A cada 60 segundos
  },
  beforeDestroy() {
    clearInterval(this.timer);
  }
};
</script>

<style scoped>
.v-card__title {
  word-break: break-word; /* Para nomes longos de medicamentos */
}
.v-progress-linear strong {
  color: white;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
}
</style>