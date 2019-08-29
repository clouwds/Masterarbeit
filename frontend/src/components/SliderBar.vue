<template>
  <div class="slidecontainer">
    <span>{{categoryName}}</span>
    <input type="range" min="1" :max="maxValue" v-model.number=localCategoryValue @mousedown="setDragged" @mouseup="setDragged" step="1" class="slider">
    <span>{{roundValue(localCategoryValue)}}%</span>
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
      roundValue (value) {
        return Math.round(value)
      },
      scaleLinear (newVal, oldVal) {
        let diff = oldVal - newVal
        let categories = this.localJsonData.children
        let currentCategory = categories[this.index]
        let tmpCatCount = 0
        let amountRest = 0
        let isMaxValue = false

        // check if > 1.05 and < maxValue - 0,5 because of rounding issues
        for (let category of categories) {
          if (category.name === currentCategory.name.toLowerCase() && category.value === category.maxValue) {
            isMaxValue = true
          }

          // get Number of sliders to adapt
          let inRange = category.value >= category.minValue + 0.5 && category.value <= category.maxValue - 0.5
          if (!(category.name === currentCategory.name.toLowerCase()) && inRange) {
            tmpCatCount += 1
          }
        }

        // if no slider is in Range, adapt all
        if (tmpCatCount === 0) {
          tmpCatCount = categories.length - 1
        }

        let amount = diff / tmpCatCount

        // adapt sliders
        categories.forEach(function (category) {
          if (!(category.name === currentCategory.name.toLowerCase())) {
            // if the manipulated slider is set to maxValue, set all other sliders to minValue
            if (isMaxValue) {
              category.value = category.minValue
            } else {
              let inRange = category.value > category.minValue + 0.5 && category.value < category.maxValue - 0.5

              if (inRange || tmpCatCount === categories.length - 1) {
                let newValue = category.value + amount

                // if newValue is higher than maxValue, calc how much to add
                if (newValue > category.maxValue) {
                  // calc amount, so that the category value does not rise over maxValue
                  let amountToAdd = category.maxValue - category.value
                  amountRest += amount - amountToAdd
                  category.value += amountToAdd

                  // if newValue is lower than minValue, calc how much to add
                } else if (newValue < category.minValue) {
                  // calc amount, so that the category value does not fall under 1
                  let amountToAdd = 1 - category.value
                  amountRest += amount - amountToAdd
                  category.value += amountToAdd

                  // if newValue is between minValue and maxValue, add amount
                } else {
                  category.value += amount
                }
              }
            }
          }
        })

        // if there is a rest, that could not been added, adjust the originally manipulated slider
        if (amountRest !== 0) {
          for (let category of categories) {
            if (category.name === currentCategory.name.toLowerCase()) {
              category.value += amountRest
              break
            }
          }
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
        },
        set: function (value) {
          this.$emit('update', value)
        }
      }
    },
    watch: {
      localCategoryValue: {
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
