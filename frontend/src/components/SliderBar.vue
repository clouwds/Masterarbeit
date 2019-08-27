<template>
  <div class="slidecontainer">
    <span>{{categoryName}}</span>
    <input type="range" min="1" :max="maxValue" v-model.number=localCategoryValue step="1" class="slider">
    <span>{{localCategoryValue}}%</span>
  </div>
</template>

<script>
  export default {
    name: 'SliderBar',
    data () {
      return {
        categoriesCount: 0
      }
    },
    props: [
      'categoryValue',
      'categoryName',
      'maxValue',
      'totalValue',
      'jsonData',
      'index'
    ],
    model: {
      prop: 'categoryValue',
      event: 'update'
    },
    methods: {
      calcRelativeValue (value, total) {
        return Math.round(((value / total) * 100) * 100) / 100
      },
      scaleLinear (newVal, oldVal) {
        let diff = oldVal - newVal
        let categories = this.localJsonData.children
        let amount = diff / categories.length

        categories.forEach(function (category) {
          category.value += amount
        })
      }
    },
    computed: {
      localCategoryValue: {
        get: function () {
          return this.categoryValue
        },
        set: function (value) {
          this.$emit('update', value)
        }
      },
      localJsonData: {
        get: function () {
          return this.jsonData
        }
      }
    },
    watch: {
      localCategoryValue: {
        handler (newValue, oldValue) {
          this.scaleLinear(newValue, oldValue)
        }
      }
    }
  }
</script>

<style scoped>
  .slidecontainer {
    width: 30%;
    padding: 10px;
    margin: auto;
    display: inline-block;
    vertical-align: top;
  }

  .slider {
    -webkit-appearance: none;
    width: 100%;
    height: 10px;
    background: #d3d3d3;
    outline: none;
    opacity: 0.7;
    -webkit-transition: .2s;
    transition: opacity .2s;
  }

  .slider:hover {
    opacity: 1;
  }

  .slider::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 20px;
    height: 20px;
    background: #4CAF50;
    cursor: pointer;
  }

  .slider::-moz-range-thumb {
    width: 20px;
    height: 20px;
    background: #4CAF50;
    cursor: pointer;
  }
</style>
