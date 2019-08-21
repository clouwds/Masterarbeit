import axios from 'axios'

const AXIOS = axios.create({
  baseURL: '/api',
  timeout: 1000
})

export default {
  data () {
    return AXIOS.get('/data')
  },
  news () {
    return AXIOS.get('/news')
  },
  fetch () {
    return AXIOS.get('/fetch')
  }
}
