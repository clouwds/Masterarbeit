<template>
  <div class="slidecontainer">
    <span>{{nodeName}}</span>
    <input type="range" :min="minValue" :max="maxValue" v-model.number=localNodeValue @mousedown="setDragged" @mouseup="setDragged" step="0.01" class="slider">
    <span>{{localNodeValue}}%</span>
  </div>
</template>

<script>
  export default {
    name: 'SliderBar',
    data () {
      return {
        categoriesCount: 0,
        dragged: false
      }
    },
    props: [
      'nodeValue',
      'nodeName',
      'minValue',
      'maxValue',
      'totalValue',
      'jsonData',
      'index',
      'children'
    ],
    model: {
      prop: 'nodeValue',
      event: 'update'
    },
    mounted () {

    },
    methods: {
      roundValue (value) {
        return Math.round(value)
      },
      scaleLinear (newVal, oldVal) {
        let diff = oldVal - newVal
        let currentCategory = this.children[this.index]
        let amountRest = 0
        let isMaxValue = false
        let adaptableSliders = []

        if (currentCategory.size === currentCategory.maxValue) {
          isMaxValue = true
        }

        for (let child of this.children) {
          if (child !== currentCategory) {
            // if current slider has maxValue, set all other sliders to minValue
            if (isMaxValue) {
              child.size = child.minValue
              continue
            }

            // get sliders to adapt
            let inRange = child.size > child.minValue && child.size < child.maxValue

            if (diff < 0 && (inRange || child.size === child.maxValue)) {
              adaptableSliders.push(child)
            } else if (diff > 0 && (inRange || child.size === child.minValue)) {
              adaptableSliders.push(child)
            }
          }
        }

        let amount = diff / adaptableSliders.length

        // if current was set to max break, because the others already have been adapted
        for (let child of adaptableSliders) {
          if (isMaxValue) {
            break
          }

          let newValue = child.size + amount
          let amountToAdd = amount

          // if newValue is higher than maxValue, calc how much to add
          if (newValue > child.maxValue) {
            // calc amount, so that the category value does not rise over maxValue
            amountToAdd = child.maxValue - child.size
            // if newValue is lower than minValue, calc how much to add
          } else if (newValue < child.minValue) {
            // calc amount, so that the category value does not fall under minValue
            amountToAdd = child.minValue - child.size
          }
          amountRest += amount - amountToAdd
          child.size += amountToAdd
        }

        // if there is a rest, that could not been added, adjust the originally manipulated slider
        if (amountRest > 0) {
          currentCategory.size += amountRest
        }
        this.showValues()
      },
      setToMin () {
        for (let child of this.children) {
          if (!(child.size === child.maxValue)) {
            child.size = child.minValue
          }
        }
      },
      showValues () {
        for (let child of this.children) {
          console.log(child.name + ': ' + child.size)
        }
      },
      setDragged () {
        this.dragged = !this.dragged
      }
    },
    computed: {
      localCategoryName: {
        get: function () {
          return this.categoryName
        }
      },
      localNodeValue: {
        get: function () {
          return this.nodeValue
        },
        set: function (value) {
          this.$emit('update', value)
        }
      },
      localJsonData: {
        get: function () {
          return this.jsonData
        },
        set: function (value) {
          this.$emit('update', value)
        }
      }
    },
    watch: {
      localNodeValue: {
        handler (newValue, oldValue) {
          if (this.dragged) {
            this.scaleLinear(newValue, oldValue)
          }
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
