<template>
  <div class="index">

    <h1>NewsViz</h1>

    <template v-if="treemapJsonLoaded">
      <Treemap :jsonData="this.treemapJson"/>
    </template>

    <SliderBar :value="value"/>

    <CardComponent
      v-for="article in articles"
      :key="article.url"
      :articleTitle="article.title"
      :articleImg="article.urlToImage"
      :articleText="article.description"
      :articleCategory="article.category"
      :articleSource="article.source"
      :articleContent="article.content"
      :articleDate="article.publishedAt"
      :articleUrl="article.url">
    </CardComponent>

  </div>
</template>

<script>
// @ is an alias to /src
import Treemap from '../components/Treemap.vue'
import CardComponent from '../components/CardComponent'
import SliderBar from '../components/SliderBar'
import {json} from 'd3-request'
import api from '@/backend-api'

let d3 = {
  json: json
}

export default {
  name: 'index',
  components: {
    SliderBar,
    CardComponent,
    Treemap
  },
  data () {
    return {
      treemapJsonLoaded: false,
      treemapJson: null,
      newsfeedJson: null,
      articles: null,
      response: [],
      errors: [],
      value: 65
    }
  },
  mounted () {
    var that = this
    // loads the data and calls the initialization methods
    this.fetchData()

    d3.json('/api/news',
      function (error, data) {
        if (error) console.log(error)
        that.articles = data
      }
    )
  },
  methods: {
    // showValue () {
    //   this.$emit('showValue', this.currentValue)
    // },
    fetchData () {
      api.data().then(response => {
        this.treemapJson = response.data
        this.treemapJsonLoaded = true
      })
    },
    calcRelativeValue (value, total) {
      return Math.round((value / total) * 100)
    },
    capitalize (str) {
      return str.charAt(0).toUpperCase() + str.slice(1)
    }

  }
}
</script>

<style>
</style>
