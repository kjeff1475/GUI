/*
 * Homework 5.
 * JavaScript code.
 *
 * author Kyle Jeffries
 * version 3
 */

var bgcColor = "ffffff";
var fgcColor = "ffffff";
function Model() {
  columns = null;
  data = null;
  selection = null;
}

function setTitle(f) {
  window.document.title = 'HW5: gracanin' + (f == undefined || f == null || f.length == 0 ? '' : ': ' + f);
}

var file = '';
var model = new Model();

function open() {
  var chooser = document.querySelector('#fileDialog');
  chooser.addEventListener("change", function(e) {
    var f = this.files[0];
    var reader = new FileReader();
    reader.addEventListener("loadend", function(e) {
      var lines = reader.result.split("\n");
      model.columns = lines[0].split(",");
      model.data = new Array(lines.length -1);
      var i;
      for (i = 1; i < lines.length; i++) {
        var line = lines[i].split(",");
        model.data[i-1] = new Array(line.length);
        var j;
        for (j = 0; j < line.length; j++) {
          model.data[i-1][j] = line[j];
        }
      }
      addTable(model, 'tableholder', 'table');
      file = f;
      setTitle(file.name);
    }, false);
    reader.readAsText(f);
  }, false);
  chooser.click();  
}

function addTable(m, thid, tid) {
  var i;
  var j;
  var tmp = 0;
  var tableholder = document.getElementById(thid);
  tmp = tableholder.offsetTop;
  var table = document.createElement('table');
  table.setAttribute('id', tid);
  window.addEventListener('model', modelChanged, false);
  tableholder.appendChild(table);
  var tr = document.createElement('tr');
  table.appendChild(tr);
  var th;
  var td;
  for (i = 0; i < m.columns.length; i++) {
    th = document.createElement('th');
    tr.appendChild(th);
    th.appendChild(document.createTextNode(m.columns[i]));
  }
  for (i = 0; i < m.data.length; i++) {
    tr = document.createElement('tr');
    table.appendChild(tr);
    for (j = 0; j < m.data[i].length; j++) {
      td = document.createElement('td');
      tr.appendChild(td);
      td.appendChild(document.createTextNode(m.data[i][j]));
      td.id = 't' + i + 't' + j;
      td.addEventListener("click", editCell, false);  
    }
  }
}

function editCell() {
  var row = this.id.substr(1,this.id.length -1).split('t')[0];
  var col = this.id.substr(1,this.id.length -1).split('t')[1];
  var modelEvent = new CustomEvent('model', {'detail': {'row':-1,'col':-1}});
  modelEvent.row = row;
  modelEvent.col = col;
  //find a way to edit the cell at (row, col)
  
  alert('Row: ' + modelEvent.row + '\nColumn: ' + modelEvent.col);
  window.dispatchEvent(modelEvent);
}

function modelChanged(e) {
  console.log('Model changed: row ' + e.row + ', column ' + e.col);
}

function close() {
  delete model.columns;
  delete model.data;
  delete model.selection;
  removeTable('tableholder', 'table');
  file = '';
  setTitle(file);
}

function removeTable(thid, tid) {
  var tmp = 0;
  var tableholder = document.getElementById(thid);
  tmp = tableholder.offsetTop;
  var table = document.getElementById(tid);
  tableholder.removeChild(table);
}

function textToHex(s) {
  var h = s;
  switch (s) {
    case "red":
       h = "FF0000";
       break;
    case "green":
       h = "00FF00";
       break;
    case "aqua": 
      h = "00FFFF";
      break;
    case "black": 
      h = "000000";
      break;
    case "blue": 
      h = "0000FF";
      break;
    case "gray": 
      h = "808080";
      break;
    case "maroon": 
      h = "800000";
      break;
    case "white": 
      h = "FFFFFF";
       break;
  }
  return h;
}

function hexToR(h) {
  return parseInt(cutHex(textToHex(h)).substring(0,2),16);
}
function hexToG(h) {
  return parseInt(cutHex(textToHex(h)).substring(2,4),16);
}
function hexToB(h) {
  return parseInt(cutHex(textToHex(h)).substring(4,6),16);
}
function cutHex(h) {
  return (h.charAt(0)=="#") ? h.substring(1,7):h
}
function save() {
  alert('File->Save menu item');
  
}

function quit() {
  document.write('');
}

function copy() {
  alert('File->Copy menu item');
}

function paste() {
  alert('File->Paste menu item');
}

function about() {
  alert('Homework 5 version 1.');
}

var canvas;
var gl;
var squareVerticesBuffer;
var shaderProgram;
var vertexPositionAttribute;

function bgcChanged(e) {
  bgcColor = document.getElementById("bgcolor").value;
  display();
}

function fgcChanged(e) {
  fgcColor = document.getElementById("fgcolor").value;
  display();
}

function init() {
  setTitle('');
  bgc = document.getElementById("bgcolor");
  bgc.addEventListener('change', bgcChanged, false);
  fgc = document.getElementById("fgcolor");
  fgc.addEventListener('change', fgcChanged, false);
  canvas = document.getElementById("canvas");
  canvas.addEventListener('mousemove', mouseMove, false);
  gl = null;
  try {
    gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
  }
  catch (e) {
  }
  if (gl) {
    initShaders();
    initBuffers();
    display();
  }
}

function mouseMove(e) {
  console.log('CLIENT --- X: ' + e.clientX + ', Y: ' + e.clientY + ', Button: ' + e.button);
  console.log('LAYER --- X: ' + e.layerX + ', Y: ' + e.layerY + ', Button: ' + e.button);
  console.log('SCREEN --- X: ' + e.screenX + ', Y: ' + e.screenY + ', Button: ' + e.button);
  console.log('Canvas width: ' + canvas.width + ', Canvas height: ' + canvas.height);
}

function initBuffers() {
  squareVerticesBuffer = gl.createBuffer();
  gl.bindBuffer(gl.ARRAY_BUFFER, squareVerticesBuffer);
  var vertices = new Float32Array([
     0.5,  0.5,
    -0.5,  0.5,
     0.5, -0.5,
    -0.5, -0.5,
  ]);
  gl.bufferData(gl.ARRAY_BUFFER, vertices, gl.STATIC_DRAW);
  gl.vertexAttribPointer(vertexPositionAttribute, 2, gl.FLOAT, false, 0, 0);
}

function display() {
   gl.clearColor(hexToR(bgcColor) / 255, hexToG(bgcColor) / 255, hexToB(bgcColor) /255, 1.0);
   gl.clearColor(hexToR(fgcColor) / 255, hexToG(fgcColor) / 255, hexToB(fgcColor) /255, 1.0);
   gl.clear(gl.COLOR_BUFFER_BIT);
   gl.drawArrays(gl.POINTS, 0, 4);
}

function initShaders() {
  var fragmentShader = getShader(gl, "fragment-shader");
  var vertexShader = getShader(gl, "vertex-shader");
  shaderProgram = gl.createProgram();
  gl.attachShader(shaderProgram, vertexShader);
  gl.attachShader(shaderProgram, fragmentShader);
  gl.linkProgram(shaderProgram);
  if (!gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)) {
    alert("Unable to initialize the shader program.");
  }
  gl.useProgram(shaderProgram);
  vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "vertexPosition");
  gl.enableVertexAttribArray(vertexPositionAttribute);
}

function getShader(gl, id) {
  var shaderScript = document.getElementById(id);
  if (!shaderScript) {
    return null;
  }
  var theSource = "";
  var currentChild = shaderScript.firstChild;
  while(currentChild) {
    if (currentChild.nodeType == 3) {
      theSource += currentChild.textContent;
    }   
    currentChild = currentChild.nextSibling;
  }
  var shader;
  if (shaderScript.type == "x-shader/x-fragment") {
    shader = gl.createShader(gl.FRAGMENT_SHADER);
  } else if (shaderScript.type == "x-shader/x-vertex") {
    shader = gl.createShader(gl.VERTEX_SHADER);
  } else {
    return null;
  }
  gl.shaderSource(shader, theSource);
  gl.compileShader(shader);
  if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
    alert("An error occurred compiling the shaders: " + gl.getShaderInfoLog(shader));
    return null;
  } 
  return shader;
}

window.addEventListener("load", init, false);
