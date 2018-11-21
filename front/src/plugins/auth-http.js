import AuthHttp from './AuthHttp'

const http = new AuthHttp()
export default ({ Vue }) => {
  Vue.prototype.$http = http
}
export { http }
