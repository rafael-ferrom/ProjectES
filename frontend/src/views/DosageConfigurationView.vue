<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="10" md="8" lg="7" xl="6" class="pt-16">
        <div v-if="loading" class="text-center pa-12">
          <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
          <p class="mt-4">Loading Medication Details...</p>
        </div>

        <v-alert v-else-if="error" type="error" border="left" colored-border prominent>
          <div class="text-h6">Error Loading Medication</div>
          Could not load details for the requested medication. Please try again.
        </v-alert>

        <template v-else-if="medicationDetails">
          <v-card>
            <v-card-title class="headline font-weight-bold primary--text text-center d-block pt-4 pb-2">
              Configure Treatment: {{ medicationDetails.nomeComercial }}
            </v-card-title>
            
            <v-card-text class="text-center mb-4 text-body-1">
              Active Ingredient: {{ medicationDetails.principioAtivo }} ({{ medicationDetails.concentracao }})
            </v-card-text>

            <v-divider></v-divider>

            <v-form ref="configForm" v-model="isFormValid">
              <v-card-text>
                <h3 class="text-h6 font-weight-medium mb-4 primary--text">Treatment Details</h3>
                <v-row>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model="config.nome"
                      label="Medication Name (for your list)"
                      outlined dense
                      :rules="[rules.required]"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model="config.dosagem"
                      label="Dosage"
                      outlined dense
                      :rules="[rules.required]"
                    ></v-text-field>
                  </v-col>
                </v-row>

                <h3 class="text-h6 font-weight-medium mb-4 mt-2 primary--text">Frequency & Duration</h3>
                <v-row>
                   <v-col cols="12" sm="6">
                     <v-menu v-model="dateMenus.start" :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto">
                       <template v-slot:activator="{ on, attrs }">
                         <v-text-field v-model="config.dataInicio" label="Start Date" prepend-icon="mdi-calendar" readonly v-bind="attrs" v-on="on" outlined dense :rules="[rules.required]"></v-text-field>
                       </template>
                       <v-date-picker v-model="config.dataInicio" @input="dateMenus.start = false"></v-date-picker>
                     </v-menu>
                   </v-col>
                   <v-col cols="12" sm="6">
                      <v-text-field v-model="config.duracaoDias" label="Treatment duration (days)" type="number" outlined dense :rules="[rules.required, rules.positiveNumber]" min="1"></v-text-field>
                   </v-col>
                </v-row>
                <v-select v-model="config.vezesPorDia" :items="frequencyOptions" item-text="text" item-value="value" label="Doses per day" outlined dense class="mb-3" :rules="[rules.required]"></v-select>

                <h3 class="text-h6 font-weight-medium mb-4 mt-2 primary--text">Instructions</h3>
                <v-textarea v-model="config.instrucoes" label="Personal Instructions (optional)" outlined dense rows="3" placeholder="e.g., Take with food, avoid sunlight..."></v-textarea>
              </v-card-text>

              <v-card-actions class="pa-4">
                <v-spacer></v-spacer>
                <v-btn color="success" large @click="submitConfiguration" :loading="submitting" :disabled="!isFormValid">
                  <v-icon left>mdi-content-save</v-icon>
                  Save Treatment
                </v-btn>
              </v-card-actions>
            </v-form>
          </v-card>
        </template>
      </v-col>
    </v-row>
     <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="4000">
        {{ snackbar.text }}
     </v-snackbar>
  </v-container>
</template>

<script>
import { useMedicationStore } from "@/store/index.js";
import { mapActions } from 'pinia';

export default {
  name: 'ConfigureMedicationView',
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      loading: true,
      submitting: false,
      error: false,
      isFormValid: false,
      medicationDetails: null,
      config: {
        nome: '',
        dosagem: '',
        dataInicio: new Date().toISOString().substr(0, 10),
        duracaoDias: 7,
        vezesPorDia: null,
        // The instructions field is now pre-populated
        instrucoes: '', 
      },
      dateMenus: {
        start: false,
      },
      frequencyOptions: [
        { text: '1 time per day', value: 1 },
        { text: '2 times per day', value: 2 },
        { text: '3 times per day', value: 3 },
        { text: '4 times per day', value: 4 },
        { text: '6 times per day', value: 6 },
      ],
      rules: {
        required: value => !!value || 'This field is required.',
        positiveNumber: value => (value === null || value === '' || parseFloat(value) > 0) || 'Value must be greater than zero.',
      },
      snackbar: {
        show: false,
        text: '',
        color: 'success'
      }
    };
  },
  async created() {
    this.medicationDetails = await this.fetchMedicationById(this.id);
    if (this.medicationDetails) {
      this.config.nome = this.medicationDetails.nomeComercial;
      this.config.dosagem = this.medicationDetails.concentracao;

      // NEW: Pre-populate the instructions textarea
      // We check if instructions exist and join the array into a single string with newlines
      if (this.medicationDetails.instrucoes && this.medicationDetails.instrucoes.length > 0) {
        this.config.instrucoes = this.medicationDetails.instrucoes.join('\n');
      }

    } else {
      this.error = true;
    }
    this.loading = false;
  },
  methods: {
    ...mapActions(useMedicationStore, ['fetchMedicationById', 'addMedicationConfiguration']),
    
    async submitConfiguration() {
      this.$refs.configForm.validate();
      if (!this.isFormValid) return;

      this.submitting = true;

      // Calcula a data de término
      const startDate = new Date(this.config.dataInicio);
      // Clone startDate to avoid modifying it directly
      const endDate = new Date(startDate.getTime());
      endDate.setDate(endDate.getDate() + parseInt(this.config.duracaoDias, 10));

      // >>>>> PAYLOAD CORRIGIDO <<<<<
      const payload = {
        // Campos do formulário
        nome: this.config.nome,
        dosagem: this.config.dosagem,
        tipo: this.medicationDetails.formaFarmaceutica.toLowerCase(),
        dataInicio: this.config.dataInicio,
        dataTermino: endDate.toISOString().substr(0, 10),
        vezesPorDia: this.config.vezesPorDia,
        instrucoes: this.config.instrucoes.split('\n').filter(line => line.trim() !== ''),
        
        // ID da bula que está sendo configurada (da prop `id` do componente)
        bulaId: parseInt(this.id, 10)
      };

      try {
        // A action addMedicationConfiguration já inclui o userId, então não precisamos passá-lo aqui.
        await this.addMedicationConfiguration(payload);
        this.snackbar.text = 'Tratamento salvo com sucesso!';
        this.snackbar.color = 'success';
        this.snackbar.show = true;
        setTimeout(() => this.$router.push({ name: 'home' }), 1500);

      } catch (error) {
        console.error("Erro ao salvar tratamento:", error);
        this.snackbar.text = 'Erro ao salvar tratamento. Tente novamente.';
        this.snackbar.color = 'error';
        this.snackbar.show = true;
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>