<template>
  <a :href="articleUrl" target="_blank">
    <md-card>
      <md-card-area>
        <md-card-media>
          <img :src="articleImg" alt="People">
        </md-card-media>

        <md-card-header>
          <div class="md-date-icon">{{articleDate | formatDate}} - {{articleCategory | capitalize}}</div>
          <div class="md-title">{{articleTitle}}</div>
          <div class="md-subhead">{{articleSource}}</div>
        </md-card-header>

        <md-card-content>
          {{articleContent}}
        </md-card-content>
      </md-card-area>
    </md-card>
  </a>
</template>

<script>
  import moment from 'moment'
  import Vue from 'vue'

  Vue.filter('formatDate', function (value) {
    if (value) {
      return moment(String(value)).format('DD.MM.YYYY')
    }
  })

  Vue.filter('capitalize', function (value) {
    if (value) {
      return value.charAt(0).toUpperCase() + value.slice(1)
    }
  })

  export default {
    name: 'CardComponent',
    props: [
      'articleTitle',
      'articleImg',
      'articleText',
      'articleSource',
      'articleCategory',
      'articleSource',
      'articleContent',
      'articleDate',
      'articleUrl'
    ]
  }
</script>

<style lang="scss" scoped>
 .md-card {
    width: 320px;
    height: 600px;
    margin: 6px;
    margin-bottom: 30px;
    display: inline-block;
    vertical-align: top;
  }

  .md-card-example {
    .md-subhead {
      .md-icon {
        $size: 16px;

        width: $size;
        min-width: $size;
        height: $size;
        font-size: $size !important;
      }

      span {
        vertical-align: middle;
      }
    }
  }
</style>
