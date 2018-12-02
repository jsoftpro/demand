import axios from 'axios'
import 'whatwg-fetch'

export default class AuthHttp {
  constructor () {
    this.authClient = {
      defaults: {
        baseURL: '',
        responseType: 'text',
        auth: '',
        headers: {
          common: { 'X-Requested-With': 'XMLHttpRequest' }
        }
      },
      auth: function () {
        let url = this.defaults.baseURL
        let query = Object.keys(this.defaults.params)
          .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(this.defaults.params[key]))
          .reduce((query, pair) => query + (query ? '&' : '') + pair)
        if (query) url += (url.indexOf('?') === -1 ? '?' : '&') + query
        let params = {
          method: 'POST',
          credentials: 'include',
          headers: {
          }
        }
        console.log(`authClient.post ${url}`)
        Object.assign(params.headers, this.defaults.headers.common)
        if (this.defaults.auth) {
          params.headers['Authorization'] = this.defaults.auth
        }
        return new Promise((resolve, reject) => {
          fetch(url, params)
            .then(response => {
              if (response.status === 200) {
                resolve(response.json())
              } else if (response.status === 401) {
                delete this.defaults.auth
                if (this.defaults.login) {
                  console.log('Retrieve login form')
                  this.defaults.login(response)
                    .then(auth => {
                      this.defaults.auth = 'Basic ' + btoa(auth.username + ':' + auth.password)
                      let upperResolve = resolve
                      let upperReject = reject
                      return new Promise((resolve, reject) => {
                        console.log('Retry request token')
                        this.auth()
                          .then(response => {
                            upperResolve(response)
                          })
                          .catch(error => upperReject(error))
                      })
                    })
                    .catch(error => reject(error))
                } else {
                  reject(new Error('Authentication form is not alowed'))
                }
              } else {
                reject(new Error(response.status + ':' + response.statusText))
              }
            })
            .catch(error => {
              reject(error)
            })
        })
      }
    }

    this.apiClient = axios.create({})
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
      that.authClient.auth()
        .then(token => {
          if (token && token.access_token) {
            token = token.access_token
          }
          console.log('Got new token ' + token)
          resolve(token)
        })
        .catch(error => {
          console.log(`Auth error: ${error}`)
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
