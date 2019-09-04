<template>
  <div class="index">

    <h1>NewsViz</h1>

    <Treemap
      @reload="forceRerender"
      v-if="treemapJsonLoaded"
      :key="treemapKey"
      :lastNode="lastNode"
      :jsonData="treemapJson">
    </Treemap>

<!--    <button @click="forceRerender">Update</button>-->

    <div></div>
    <div>{{treemapJson}}</div>

    <card-component v-if="newsJsonLoaded"
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
    </card-component>

  </div>
</template>

<script>
// @ is an alias to /src
import Treemap from '../components/Treemap.vue'
import CardComponent from '../components/CardComponent'
import SliderBar from '../components/SliderBar'
import api from '@/backend-api'

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
      newsJsonLoaded: false,
      treemapJson: null,
      articles: null,
      categories: null,
      response: [],
      errors: [],
      treemapKey: 0,
      lastNode: null
    }
  },
  mounted () {
    // loads the data and calls the initialization methods
    this.fetchTreemapData()
    this.fetchNewsData()
  },
  methods: {
    // showValue () {
    //   this.$emit('showValue', this.currentValue)
    // },
    fetchTreemapData () {
      api.data().then(response => {
        this.treemapJson = response.data
        this.categories = response.data.children
        this.treemapJsonLoaded = true
        this.sortByCategoryValue()
      })
    },
    fetchNewsData () {
      api.news().then(response => {
        this.articles = response.data
        this.newsJsonLoaded = true
      })
    },
    sortByCategoryValue () {
      this.categories = this._.orderBy(this.categories, 'value', 'desc')
      this.treemapJson.children = this.categories
    },
    calcRelativeValue (value, total) {
      return Math.round((value / total) * 100)
    },
    capitalize (str) {
      return str.charAt(0).toUpperCase() + str.slice(1)
    },
    forceRerender () {
      this.treemapKey += 1

      api.input(this.treemapJson).then(response => {
        this.articles = response.data
        this.newsJsonLoaded = true
      })
    }
  }
}
</script>

<style>
</style>
