<template>
  <div>
    <div class="treemap">
      <!-- The SVG structure is explicitly defined in the template with attributes derived from component data -->
      <svg :height="height" style="margin-left: 0px;" :width="width">
        <g style="shape-rendering: crispEdges;" transform="translate(0,20)">
          <!-- We can use Vue transitions too! -->
          <transition-group name="list" tag="g" class="depth">
            <!-- Generate each of the visible squares at a given zoom level (the current selected node) -->
            <g
              class="children"
              v-for="(children, index) in selectedNode._children"
              :key="'c_' + children.id"
              v-if="selectedNode"
              >

              <!-- Generate the children squares (only visible on hover of a square) -->
              <rect
                v-for="child in children._children"
                class="child"
                :id="child.id"
                :key="child.id"
                :height="y(child.y1) - y(child.y0)"
                :width="x(child.x1) - x(child.x0)"
                :x="x(child.x0)"
                :y="y(child.y0)"
                >
              </rect>

              <!--
                The visible square rect element.
                You can attribute directly an event, that fires a method that changes the current node,
                restructuring the data tree, that reactivly gets reflected in the template.
              -->
              <rect
                class="parent"
                v-on:click="selectNode"
                :id="children.id"
                :key="children.id"
                :x="x(children.x0)"
                :y="y(children.y0)"
                :width="x(children.x1 - children.x0 + children.parent.x0)"
                :height="y(children.y1 - children.y0 + children.parent.y0)"
                :style="{ fill: mapColor(children.data.name, index) }"
                >

                <!-- The title attribute -->
                <title>{{ capitalize(children.data.name)}} | {{children.data.value}}% </title>
              </rect>

              <!-- The visible square text element with the title and value of the child node -->
              <text
                dy="1em"
                :key="'t_' + children.parentId + '_' +children.id"
                :x="x(children.x0) + 6"
                :y="y(children.y0) + 6"
                style="fill-opacity: 1;"
                >
                {{ capitalize(children.data.name)}}
              </text>

              <text
                dy="2.25em"
                :key="'t_' + children.id"
                :x="x(children.x0) + 6"
                :y="y(children.y0) + 6"
                style="fill-opacity: 1;"
                >

                {{ roundValue(children.data.value)}}%
              </text>

            </g>
          </transition-group>

          <!-- The top most element, representing the previous node -->
          <g class="grandparent">

            <rect
              :height="margin.top"
              :width="width"
              :y="(margin.top * -1)"
              v-on:click="selectNode"
              :id="parentId">
            </rect>

            <!-- The visible square text element with the id (basically a breadcumb, if you will) -->
            <text
              dy=".65em"
              x="6"
              y="-14">

              {{ selectedNode.id }}
            </text>
          </g>
        </g>
      </svg>
    </div>

    <slider-bar
      v-for="(category, index) in jsonData.children"
      :key="category.name"
      :categoryName="capitalize(category.name)"
      :maxValue="category.maxValue"
      :totalValue="jsonData.value"
      :index="index"
      :jsonData="jsonData"
      v-model="category.value">
    </slider-bar>
  </div>
</template>

<script>
import {scaleLinear, scaleOrdinal, schemeCategory20} from 'd3-scale'
import {json} from 'd3-request'
import {hierarchy, treemap} from 'd3-hierarchy'
import SliderBar from './SliderBar'

// To be explicit about which methods are from D3 let's wrap them around an object
// Is there a better way to do this?
let d3 = {
  scaleLinear: scaleLinear,
  scaleOrdinal: scaleOrdinal,
  schemeCategory20: schemeCategory20,
  json: json,
  hierarchy: hierarchy,
  treemap: treemap
}

export default {
  name: 'Treemap',
  components: {
    SliderBar
  },
  // the component's data
  data () {
    return {
      rootNode: {},
      margin: {
        top: 20,
        right: 0,
        bottom: 0,
        left: 0
      },
      width: 960,
      height: 530,
      selected: null,
      currentNode: {},
      color: {},
      catArr: []

    }
  },
  props: [
    'jsonData'
  ],
  // You can do whatever when the selected node changes
  watch: {
    selectedNode (newData, oldData) {

    },
    jsonData (newVal, oldVal) {
      console.log('yes Im watching')
      this.jsonData = newVal
      var that = this

      // An array with colors (can probably be replaced by a vuejs method)
      that.color = d3.scaleOrdinal(d3.schemeCategory20)

      that.initialize()
      that.accumulate(that.rootNode, that)
      that.treemap(that.rootNode)
      that.catArr = that.jsonData.children
    }
  },
  // In the beginning...
  mounted () {
    var that = this

    // An array with colors (can probably be replaced by a vuejs method)
    that.color = d3.scaleOrdinal(d3.schemeCategory20)

    that.initialize()
    that.accumulate(that.rootNode, that)
    that.treemap(that.rootNode)
    that.catArr = that.jsonData.children
  },
  // The reactive computed variables that fire rerenders
  computed: {
    // The grandparent id
    parentId () {
      if (this.selectedNode.parent === undefined || this.selectedNode.parent === null) {
        return this.selectedNode.id
      } else {
        return this.selectedNode.parent.id
      }
    },
    // Returns the x position within the current domain
    // Maybe it can be replaced by a vuejs method
    x () {
      return d3.scaleLinear()
        .domain([0, this.width])
        .range([0, this.width])
    },
    // Returns the y position within the current domain
    // Maybe it can be replaced by a vuejs method
    y () {
      return d3.scaleLinear()
        .domain([0, this.height - this.margin.top - this.margin.bottom])
        .range([0, this.height - this.margin.top - this.margin.bottom])
    },
    // The D3 function that recursively subdivides an area into rectangles
    treemap () {
      return d3.treemap()
      .size([this.width, this.height])
      .round(false)
      .paddingInner(0)
    },
    // The current selected node
    selectedNode () {
      let node = null

      if (this.selected) {
        let nd = this.getNodeById(this.rootNode, this.selected, this)

        if (nd._children) {
          node = nd
        } else {
          node = nd.parent
        }
      } else {
        node = this.rootNode
      }

      // Recalculates the y and x domains
      this.x.domain([node.x0, node.x0 + (node.x1 - node.x0)])
      this.y.domain([node.y0, node.y0 + (node.y1 - node.y0)])

      return node
    }
  },
  methods: {
    // Called once, to create the hierarchical data representation
    initialize () {
      let that = this

      if (that.jsonData) {
        that.rootNode = d3.hierarchy(that.jsonData)
        .eachBefore(function (d) { d.id = (d.parent ? d.parent.id + '.' : '') + d.data.name }) // set id e.g. Newsviz.auto
        .sum((d) => d.value)
        .sort(function (a, b) {
          return b.height - a.height || b.value - a.value
        })
        that.rootNode.x = that.rootNode.y = 0
        that.rootNode.x1 = that.width
        that.rootNode.y1 = that.height
        that.rootNode.depth = 0

        console.log(that.rootNode)
      }
    },
    // Calculates the accumulated value (sum of children values) of a node - its weight,
    // represented afterwards in the area of its square
    accumulate (d, context) {
      d._children = d.children

      if (d._children) {
        d.value = d._children.reduce(function (p, v) { return p + context.accumulate(v, context) }, 0)
        return d.value
      } else {
        return d.value
      }
    },
    // Helper method - gets a node by its id attribute
    getNodeById (node, id, context) {
      if (node.id === id) {
        return node
      } else if (node._children) {
        for (var i = 0; i < node._children.length; i++) {
          var nd = context.getNodeById(node._children[i], id, context)
          if (nd) {
            return nd
          }
        }
      }
    },
    // When a user clicks a square, changes the selected data attribute
    // which fires the computed selectedNode, which in turn finds the Node by the id of the square clicked
    // and the template reflects the changes
    selectNode (event) {
      this.selected = event.target.id
    },
    roundValue (value) {
      if (value < 0.5) {
        return 1
      }
      return Math.round(value)
    },
    capitalize (str) {
      return str.charAt(0).toUpperCase() + str.slice(1)
    },
    mapColor (name, index) {
      const mapCat = this.catArr.map(x => x.name)
      const colScaleCat = d3.scaleOrdinal(d3.schemeCategory20).domain(mapCat)

      if (!(mapCat.includes(name))) {
        // @TODO
        // const mapSrc = this.catArr[index].children.map(x => x.name)
        // const colScaleSrc = d3.scaleOrdinal(d3.schemeCategory20).domain(mapSrc)
        // return colScaleSrc(name)
      }
      return colScaleCat(name)
    }
  }
}
</script>

<style scoped>

.treemap {
  margin: 30px;
}

text {
  pointer-events: none;
}

.grandparent text {
  font-weight: bold;
}

rect {
  fill: none;
  stroke: #fff;
}

rect.parent,
.grandparent rect {
  stroke-width: 2px;
}

.grandparent rect {
  fill: orange;
}

.grandparent:hover rect {
  fill: #ee9700;
}

.children rect.parent,
.grandparent rect {
  cursor: pointer;
}

.children rect.parent {
  fill: #bbb;
  fill-opacity: .5;
}

.children:hover rect.child {
  fill: #bbb;
}

.children text{
  font-size: 0.8em;
}

.grandparent text{
  font-size: 0.9em;
}

.list-enter-active, .list-leave-active {
  transition: all 1s;
}
.list-enter, .list-leave-to /* .list-leave-active for <2.1.8 */ {
  opacity: 0;
}
    
</style>
