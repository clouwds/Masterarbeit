<template>
  <div class="slidecontainer">
    <span>Politik</span>
    <input type="range" min="1" v-bind:max="max" v-model="currentValue" step="1" @change="showValue" class="slider">
    <span>{{currentValue}} Artikel ({{calcRelativeValue(currentValue, 273)}}%)</span>
  </div>
</template>

<script>
  export default {
    name: 'SliderBar',
    data () {
      return {
        currentValue: 65,
        max: 65
      }
    },
    methods: {
      showValue () {
        this.$emit('showValue', this.currentValue)
      },
      calcRelativeValue (value, total) {
        return Math.round((value / total) * 100)
      }
    }
  }
</script>

<style scoped>
  .slidecontainer {
    width: 50%;
    padding: 10px;
    margin: auto;
  }

  .slider {
    -webkit-appearance: none;
    width: 100%;
    height: 25px;
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
    width: 25px;
    height: 25px;
    background: #4CAF50;
    cursor: pointer;
  }

  .slider::-moz-range-thumb {
    width: 25px;
    height: 25px;
    background: #4CAF50;
    cursor: pointer;
  }
</style>
