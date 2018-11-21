<template>
  <q-layout view="hHh lpr fff">
    <q-layout-header>
      <div class="q-title q-pa-xs bg-light text-white">{{ $t('label.title') }}</div>
    </q-layout-header>
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { openURL } from 'quasar'
export default {
  name: 'Layout',
  data () {
    return {
      // leftDrawerOpen: this.$q.platform.is.desktop
    }
  },
  methods: {
    openURL
  }
}
</script>

<style>
</style>
