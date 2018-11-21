<template>
  <div>
    <div class="col row inline full-width">
      <q-datetime type="date" v-model="model.from" :float-label="labelFrom" :format="format" :max="model.to" default-view="day" format-model="auto" style="width: 50%" :clearable="!model.to" class="col-auto q-pr-lg" />
      <q-datetime type="date" v-model="model.to" :float-label="labelTo" :format="format" :min="model.from" default-view="day" format-model="auto" style="width: 50%" clearable @clear="clearAll" class="col-auto q-pl-lg" />
    </div>
  </div>
</template>

<script>

import { QDatetime } from 'quasar'

export default {
  name: 'QDaterange',
  props: {
    value: { required: true },
    label: String,
    format: String
  },
  data () {
    return {
      model: {
        from: this.value ? this.value.from : null,
        to: this.value ? this.value.to : null
      }
    }
  },
  computed: {
    labelFrom: {
      get () {
        return this.label + ' (' + this.$t('label.from', 'from') + ')'
      }
    },
    labelTo: {
      get () {
        return this.label + ' (' + this.$t('label.to', 'to') + ')'
      }
    }
  },
  watch: {
    value: {
      handler: function (v) {
        this.model.from = v ? v.from : null
        this.model.to = v ? v.to : null
      },
      immediate: true
    },
    model: {
      handler: function (v) {
        if (this.model.to && this.model.from) {
          this.$emit('input', this.model)
          console.log(`set range ${JSON.stringify(this.model)}`)
        }
      },
      deep: true
    }
  },
  methods: {
    clearAll () {
      this.model.from = null
      this.model.to = null
      this.$emit('input', this.model)
    }
  },
  components: {
    QDatetime
  }
}

</script>
