import { QModal, QInput, QBtn } from 'quasar'

export default {
  name: 'QLogin',

  props: {
    value: Boolean,
    prompt: Object,
    ok: {
      type: [String, Object, Boolean],
      default: true
    },
    cancel: [String, Object, Boolean],
    stackButtons: Boolean,
    preventClose: Boolean,
    noBackdropDismiss: Boolean,
    noEscDismiss: Boolean,
    noRefocus: Boolean,
    position: String,
    color: {
      type: String,
      default: 'primary'
    }
  },
  render (h) {
    const child = []

    child.push(
      h('div',
        { staticClass: 'modal-header' },
        [ 'Login form' ])
    )

    child.push(
      h('div',
        { staticClass: 'modal-body modal-scroll' },
        this.__getPrompt(h))
    )

    if (this.$scopedSlots.buttons) {
      child.push(
        h('div', {
          staticClass: 'modal-buttons',
          'class': this.buttonClass
        }, [
          this.$scopedSlots.buttons({
            ok: this.__onOk,
            cancel: this.__onCancel
          })
        ])
      )
    } else if (this.ok || this.cancel) {
      child.push(this.__getButtons(h))
    }

    return h(QModal, {
      ref: 'modal',
      props: {
        value: this.value,
        minimized: true,
        noBackdropDismiss: this.noBackdropDismiss || this.preventClose,
        noEscDismiss: this.noEscDismiss || this.preventClose,
        noRefocus: this.noRefocus,
        position: this.position
      },
      on: {
        input: val => {
          this.$emit('input', val)
        },
        show: () => {
          if (this.$q.platform.is.desktop) {
            let node = this.$refs.modal.$el.getElementsByTagName('INPUT')
            if (node.length) {
              node[0].focus()
            }
          }
          this.$emit('show')
        },
        hide: () => {
          this.$emit('hide')
        },
        dismiss: () => {
          this.$emit('cancel')
        },
        'escape-key': () => {
          this.$emit('escape-key')
        }
      }
    }, child)
  },

  computed: {
    okLabel () {
      return this.ok === true
        ? this.$q.i18n.label.ok
        : this.ok
    },
    cancelLabel () {
      return this.cancel === true
        ? this.$q.i18n.label.cancel
        : this.cancel
    },
    buttonClass () {
      return this.stackButtons
        ? 'column'
        : 'row'
    },
    okProps () {
      return Object(this.ok) === this.ok
        ? Object.assign({
          color: this.color,
          label: this.$q.i18n.label.ok,
          noRipple: true
        }, this.ok)
        : { color: this.color, flat: true, label: this.okLabel, noRipple: true }
    },
    cancelProps () {
      return Object(this.cancel) === this.cancel
        ? Object.assign({
          color: this.color,
          label: this.$q.i18n.label.cancel,
          noRipple: true
        }, this.cancel)
        : { color: this.color, flat: true, label: this.cancelLabel, noRipple: true }
    }
  },

  methods: {
    show () {
      return this.$refs.modal.show()
    },
    hide () {
      return this.$refs.modal ? this.$refs.modal.hide().then(() => this.prompt.model) : Promise.resolve()
    },
    __getPrompt (h) {
      return [
        h(QInput, {
          style: 'margin-bottom: 10px',
          props: {
            value: this.prompt.model.username,
            type: 'text',
            placeholder: 'Username',
            autofocus: true,
            color: this.color,
            noPassToggle: true
          },
          on: {
            input: v => { this.prompt.model.username = v }
          }
        }),
        h(QInput, {
          style: 'margin-bottom: 10px',
          props: {
            value: this.prompt.model.password,
            type: 'password',
            placeholder: 'Password',
            color: this.color,
            noPassToggle: true
          },
          on: {
            input: v => { this.prompt.model.password = v },
            keyup: evt => {
              // if ENTER key
              if ((evt.which || evt.keyCode) === 13) {
                this.__onOk()
              }
            }
          }
        })
      ]
    },
    __getButtons (h) {
      const child = []

      if (this.cancel) {
        child.push(h(QBtn, {
          props: this.cancelProps,
          on: { click: this.__onCancel }
        }))
      }
      if (this.ok) {
        child.push(h(QBtn, {
          props: this.okProps,
          on: { click: this.__onOk }
        }))
      }

      return h('div', {
        staticClass: 'modal-buttons',
        'class': this.buttonClass
      }, child)
    },
    __onOk () {
      return this.hide().then(data => {
        this.$emit('ok', data)
      })
    },
    __onCancel () {
      return this.hide().then(() => {
        this.$emit('cancel')
      })
    }
  }
}
