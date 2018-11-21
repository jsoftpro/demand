import axios from 'axios'

export default class AuthHttp {
  constructor () {
    this.authClient = axios.create({
      responseType: 'text'
    })
    // attribute 'auth' is a truk for a axios' 0.18.0 bug
    // must be replaced with an <instance>.defaults.headers.common['Authorization']
    this.authClient.defaults.auth = ''
    this.authClient.defaults.headers.common = { 'X-Requested-With': 'XMLHttpRequest' }
    this.apiClient = axios.create({
    })
    this.apiClient.defaults.auth = ''
  }
  get (url, config) {
    if (!config) config = {}
    config.method = 'get'
    config.url = url
    return this.authRequest(config)
  }
  delete (url, config) {
    if (!config) config = {}
    config.method = 'delete'
    config.url = url
    return this.authRequest(config)
  }
  post (url, data, config) {
    if (!config) config = {}
    config.method = 'post'
    config.url = url
    config.data = data
    return this.authRequest(config)
  }
  put (url, data, config) {
    if (!config) config = {}
    config.method = 'put'
    config.url = url
    config.data = data
    return this.authRequest(config)
  }
  patch (url, data, config) {
    if (!config) config = {}
    config.method = 'patch'
    config.url = url
    config.data = data
    return this.authRequest(config)
  }
  getAuthToken () {
    let that = this
    return new Promise((resolve, reject) => {
      that.authClient.post()
        .then(response => {
          let token = response.data
          if (token && token.access_token) {
            token = token.access_token
          }
          console.log('Got new token ' + token)
          resolve(token)
        })
        .catch(error => {
          if (error.response &&
            error.response.status === 401 &&
            that.authClient.defaults.login) {
            delete that.authClient.defaults.auth
            console.log('Retrieve login form')
            that.authClient.defaults.login(error)
              .then(auth => {
                // that.authClient.defaults.headers.common['Authorization'] = 'Basic ' + loginpass
                that.authClient.defaults.auth = auth
                let upperResolve = resolve
                let upperReject = reject
                return new Promise((resolve, reject) => {
                  console.log('Retry request token')
                  that.getAuthToken()
                    .then(response => {
                      upperResolve(response)
                    })
                    .catch(error => upperReject(error))
                })
              })
              // .catch(error => reject(error))
          } else {
            reject(error)
          }
        })
    })
  }
  authRequest (config) {
    let that = this
    return new Promise((resolve, reject) => {
      if (that.apiClient.defaults.token) {
        if (!config.headers) {
          config.headers = {}
        }
        config.headers['Authorization'] = 'Bearer ' + that.apiClient.defaults.token
      }
      that.apiClient.request(config)
        .then(response => resolve(response))
        .catch(error => {
          // console.log(`Http Error: ${JSON.stringify(error)}`)
          if (error.response && error.response.status === 401 && that.authClient) {
            delete that.apiClient.defaults.headers.common['Authorization']
            delete that.apiClient.defaults.auth
            delete that.apiClient.defaults.token
            console.log(`Unauthorized; Refreshing Token ${error.response.config.headers['WWW-Authenticate']}`)
            that.getAuthToken()
              .then(token => {
                console.log('Retry request with new token')
                // that.apiClient.defaults.headers.common['Authorization'] = 'Bearer ' + token
                that.apiClient.defaults.token = token
                error.response.config.headers['Authorization'] = 'Bearer ' + that.apiClient.defaults.token
                that.apiClient.request(error.response.config)
                  .then(response => resolve(response))
                  .catch(error => reject(error))
              })
              .catch(error => reject(error))
          } else {
            reject(error)
          }
        })
    })
  }
}
