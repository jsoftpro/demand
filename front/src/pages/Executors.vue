<template>
  <q-page :padding="$q.platform.is.desktop" class="justify-center">
  </q-page>
</template>

<script>

export default {
  name: 'PageIndex',
  data () {
    return {
      disabled: true,
      profile: '',
      config: {
        user: ''
      }
    }
  },
  methods: {

  }
}

</script>
