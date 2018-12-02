import QLogin from '../components/QLogin'
import modalFn from '../../node_modules/quasar-framework/src/utils/modal-fn'

export default ({ app, Vue }) => {
  Vue.use(QLogin)
  Vue.prototype.$login = modalFn(QLogin, Vue)
}
