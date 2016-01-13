/*
 * Homework 5 assignment solution.
 * JavaScript code.
 *
 * author Kyle Jeffries
 * version 1
 */

var chooser;
var file;
var clipboard;
var currentRow;
var currentColumn;
var model;
var modelEvent;

var canvas;
var gl;
var oldX;
var oldY;
var rubberband;
var rubberbandWidth;
var rubberbandData;
var shaderProgram;
var vertexPositionAttribute;

var pointSize;
var backgroundColor;
var foregroundColor;
var selectionColor;

var pointSelect;
var backgroundInput;
var foregroundInput;
var selectionInput;

// BEGIN Model

function Model() {
  var columns = null;
  var data = null;
  var selection = null;
}

function getAllData() {
  var r = getRowCount();
  var data = new Float32Array(2 * r);
  for (i = 0; i < r; i++) {
    data[2 * i] = parseFloat(model.data[i][0]);
    data[2 * i + 1] = parseFloat(model.data[i][1]);
  }
  return data;
}

function getSelectedData() {
  s = (model.selection == null ? 0 : model.selection.length);
  var data = new Float32Array(2 * s);
  if (s > 0) {
    var j = 0;
    for (i = 0; i < s; i++) {
      data[j++] = parseFloat(model.data[model.selection[i]][0]);
      data[j++] = parseFloat(model.data[model.selection[i]][1]);
    }
  }
  return data;
}

function setSelectedRows(r) {
  if (r == null) {
    if (model.selection != null) {
      model.selection = null;
    }
  }
  else {
    if (model.selection != null) {
      if (equals(model.selection,r)) {
        return;
      }
    }
    model.selection = r == null ? null : copyOf(r);
  }
  modelEvent.selection = true;
  window.dispatchEvent(modelEvent);	
}

function copyOf(a) {
  var b = new Array(a.length);
  for (i = 0; i < a.length; i++) {
    b[i] = a[i];
  }
  return b;
}

function equals(a, b) {
  if (a.length != b.length) {
    return false;
  }
  for (i = 0; i < a.length; i++) {
    if (a[i] != b[i]) {
      return false;
    }
  }
  return true;
}

function addSelectedRows(xmin, xmax, ymin, ymax) {
  var r = getRowCount();
  var selected = new Array(r);
  for (i = 0; i < r; i++) {
    selected[i] = 0;
	x = parseFloat(model.data[i][0]);
	y = parseFloat(model.data[i][1]);
    if (x >= xmin && x <= xmax && y >= ymin && y <= ymax) {
      selected[i] = 1;
    }
  }
  if (model.selection != null) {
    for (i = 0; i < model.selection.length; i++) {
      selected[model.selection[i]] = 1;
    }
  }
  var count = 0;
  for (i = 0; i < r; i++) {
    if (selected[i] > 0) {
      count++;
    }
  }
  var newSelected = new Array(count);
  count = 0;
  for (i = 0; i < r; i++) {
    if (selected[i] > 0) {
      newSelected[count++] = i;
    }
  }
  setSelectedRows(newSelected);
}

function getScaleX() {
  return scale(0);
}

function getScaleY() {
  return scale(1);
}

function getTranslationX() {
  return translate(0);
}

function getTranslationY() {
  return translate(1);
}

function scale(c) {
  var s = 1;
  var min = Infinity;
  var max = -Infinity;
  for (i = 0; i < getRowCount(); i++) {
    var f = parseFloat(model.data[i][c]);
    if (f > max) {
      max = f;
    }
    if (f < min) {
      min = f;
    }
  }
  if (min < max) {
    s = 1.6 / (max - min);
  }
  return s;
}

function translate(c) {
  var t = 0;
  var min = Infinity;
  var max = -Infinity;
  for (i = 0; i < getRowCount(); i++) {
    var f = parseFloat(model.data[i][c]);
    if (f > max) {
      max = f;
    }
    if (f < min) {
      min = f;
    }
  }
  if (min < max) {
    t = 0.8 - max * 1.6 / (max - min);
  }
  return t;
}

function getRowCount() {
  if (model == undefined || model.data == undefined) {
    return 0;
  }
  return model.data.length;
}

function addSelectedRow(r) {
  if (model.selection == null || model.selection == undefined) {
    model.selection = new Array(1);
    model.selection[0] = r;
  }
  else {
    var p = -1;
    for (i = 0; i < model.selection.length; i++) {
      if (model.selection[i] == r) {
        return;
      }
    }
    var newSelection = new Array(model.selection.length + 1);
    for (i = 0; i < model.selection.length; i++) {
      newSelection[i] = model.selection[i];
    }
    newSelection[model.selection.length] = r;
    model.selection = newSelection;
  }
  modelEvent.selection = true;
  window.dispatchEvent(modelEvent);	

}

function modelChange(e) {
  if (modelEvent.selection) {
    for (i = 0; i < getRowCount(); i++) {
      for (j = 0; j < 2; j++) {
        var td = document.getElementById('t' + i  + 't' + j);
        td.style.backgroundColor = '#DDDDDD';
      }
    }
    if (model.selection == null) {
      display();
      return;
    }
    for (i = 0; i < model.selection.length; i++) {
      for (j = 0; j < 2; j++) {
        var td = document.getElementById('t' + model.selection[i]  + 't' + j);
        td.style.backgroundColor = 'blue';
      }    
    }
  }
  else {
    var id = 't' + e.row + 't' + e.col;
    model.data[e.row][e.col] = e.val;
    var td = document.getElementById(id);
    td.removeChild(td.firstChild);
    td.appendChild(document.createTextNode(model.data[e.row][e.col]));
  }
  display();
}

// END Model



// START Menu Bar

function open() {
  chooser.click();  
}

function close() {
  delete model.columns;
  delete model.data;
  delete model.selection;
  modelEvent.selection = false;
  window.dispatchEvent(modelEvent);
  removeTable('tableholder', 'table');
  file = '';
  setTitle(file);
}

function save() {
  var line = model.columns[0];
  for (i = 1; i < model.columns.length; i++) {
    line += ', ' + model.columns[i];
  }
  line += '\n';
  for (i = 0; i < model.data.length; i++) {
    line += model.data[i][0];
    for (j = 1; j < model.data[i].length; j++) {
      line += ', ' + model.data[i][j];
    }
    line += '\n';
  }
  console.log(line);
}

function quit() {
  document.write('');
}

function copy() {
  clipboard = model.data[currentRow][currentColumn];
}

function paste() {
  model.data[currentRow][currentColumn] = clipboard;
  modelEvent.row = currentRow;
  modelEvent.col = currentColumn;
  modelEvent.val = clipboard;
  modelEvent.selection = false;
  window.dispatchEvent(modelEvent);
}

function about() {
  alert('Homework 5 version 1.');
}

function addTable(m, thid, tid) {
  var i;
  var j;
  var tmp = 0;
  var tableholder = document.getElementById(thid);
  tmp = tableholder.offsetTop;
  var table = document.createElement('table');
  table.setAttribute('id', tid);
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
      td.addEventListener("dblclick", editCell, false);  
      td.addEventListener("click", selectRow, false);  
    }
  }
}

function removeTable(thid, tid) {
  var tmp = 0;
  var tableholder = document.getElementById(thid);
  tmp = tableholder.offsetTop;
  var table = document.getElementById(tid);
  tableholder.removeChild(table);
}

function editCell() {
  var row = this.id.substr(1,this.id.length -1).split('t')[0];
  var col = this.id.substr(1,this.id.length -1).split('t')[1];
  currentRow = row;
  currentColumn = col;
  var val = prompt('Row: ' + row + '\nColumn: ' + col);
  if (val != null) {
    model.data[row][col] = val;
    modelEvent.row = row;
    modelEvent.col = col;
    modelEvent.val = val;
    modelEvent.selection = false;
  	window.dispatchEvent(modelEvent);
  }
}

function selectRow(e) {
  var row = this.id.substr(1,this.id.length -1).split('t')[0];
  var col = this.id.substr(1,this.id.length -1).split('t')[1];
  currentRow = row;
  currentColumn = col;
  if (!e.metaKey) {
    setSelectedRows(null);
  }
  addSelectedRow(row);
  display();
}

function setTitle(f) {
  window.document.title = 'HW5: gracanin' + (f == undefined || f == null || f.length == 0 ? '' : ': ' + f);
}

// END Menu Bar



//START Chart

var moveCount = 0;
function sizeChange(e) {
  if (e.button == 0) {
    if (++moveCount > 5) {
      moveCount = 0;
      display();
    }
  }
}

function Color() {
  var  green = 1;
  var blue = 1;
  var alpha = 1;
}

function textToColor(s) {
  var h = (s.charAt(0)=='#') ? s.substring(1,s.length) : s;
  var names = new Array('aqua', 'black', "blue", 'fuchsia', 'gray', 'green', 'lime', 'maroon', 'navy', 'olive', 'purple', 'red', 'silver', 'teal', 'white', 'yellow');
  var values = new Array('00FFFF', '000000', '0000FF', 'FF00FF', '808080', '008000', '00FF00', '800000', '000080', '808000', '800080', 'FF0000', 'C0C0C0', '008080', 'FFFFFF', 'FFFF00');
  for (i = 0; i < names.length; i++) {
    if (h == names[i]) {
      h = values[i];
      break;
    }
  }
  if (h.length != 6){
    return null;
  }
  var c = new Color();
  c.red = parseInt(h.substring(0,2),16) / 255;
  if (isNaN(c.red)) {
    return null;
  }
  c.green = parseInt(h.substring(2,4),16) / 255;
  if (isNaN(c.green)) {
    return null;
  }
  c.blue = parseInt(h.substring(4,6),16) / 255;
  if (isNaN(c.blue)) {
    return null;
  }
  c.alpha = 1;
  return c;
}

function pointSizeChange(e) {
  pointSize = parseInt(pointSelect.value);
  display();
}

function backgroundColorChange(e) {
  var color = textToColor(backgroundInput.value);
  if (color != null) {
    backgroundColor = color;
    backgroundInput.style.backgroundColor = backgroundInput.value;
    display();
  }
}

function foregroundColorChange(e) {
  var color = textToColor(foregroundInput.value);
  if (color != null) {
    foregroundColor = color;
    foregroundInput.style.backgroundColor = foregroundInput.value;
    display();
  }
}

function selectionColorChange(e) {
  var color = textToColor(selectionInput.value);
  if (color != null) {
    selectionColor = color;
    selectionInput.style.backgroundColor = selectionInput.value;
    display();
  }
}

function mouseMove(e) {
  if (rubberband) {
    setRubberBandData(oldX, oldY -20, e.layerX, e.layerY -20, false);
    display();
  }
}

function mouseDown(e) {
  switch (e.button) {
  case 0:
    rubberband = true;
    oldX = e.layerX;
    oldY = e.layerY;
    break;
  case 2:
    setSelectedRows(null);
    break;
  }
}

function mouseUp(e) {
  if (rubberband) {
    setRubberBandData(oldX, oldY - 20, e.layerX, e.layerY - 20, true);
    rubberband = false;
  }
  display();
}

function setRubberBandData(x1, y1, x2, y2, update) {
  var xmin, xmax, ymin, ymax;
  xmin = x1 < x2 ? x1 : x2;
  xmax = x1 < x2 ? x2 : x1;
  ymin = y1 > y2 ? y1 : y2;
  ymax = y1 > y2 ? y2 : y1;

  xmin = -1.0 + 2.0 * xmin / canvas.width;
  xmax = -1.0 + 2.0 * xmax / canvas.width;
  ymin = 1.0 - 2.0 * ymin / canvas.height;
  ymax = 1.0 - 2.0 * ymax / canvas.height;

  xmin = (xmin - getTranslationX()) / getScaleX();
  xmax = (xmax - getTranslationX()) / getScaleX();
  ymin = (ymin - getTranslationY()) / getScaleY();
  ymax = (ymax - getTranslationY()) / getScaleY();

  rubberbandData[0] = xmin;
  rubberbandData[1] = ymin;
  rubberbandData[2] = xmax;
  rubberbandData[3] = ymin;
  rubberbandData[4] = xmax;
  rubberbandData[5] = ymax;
  rubberbandData[6] = xmin;
  rubberbandData[7] = ymax;
  if (update) {
    addSelectedRows(xmin, xmax, ymin, ymax);
  }
}

function display() {
  var location;
  var data;
  canvas.width = canvas.clientWidth;
  canvas.height = canvas.clientHeight;
  gl.viewport(0, 0, canvas.width, canvas.height);
  gl.clearColor(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.alpha);
  gl.clear(gl.COLOR_BUFFER_BIT);

  if ((data = getAllData()) != null) {

    var allBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, allBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW);
    vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "vPosition");
    gl.enableVertexAttribArray(vertexPositionAttribute);
    gl.vertexAttribPointer(vertexPositionAttribute, 2, gl.FLOAT, false, 0, 0);
    
    // Use the current value of pointSize variable as pointSize vertex shader variable.
    location = gl.getUniformLocation(shaderProgram, "pointSize");
    gl.uniform1i(location, pointSize);

    // Use the current value stored in the models, getScaleX(), as scaleX vertex shader variable value.
    location = gl.getUniformLocation(shaderProgram, "scaleX");
    gl.uniform1f(location, getScaleX());

    // Use the current value stored in the model, getScaleY(), as scaleY vertex shader variable value.
    location = gl.getUniformLocation(shaderProgram, "scaleY");
    gl.uniform1f(location, getScaleY());

    // Use the current value stored in the model, getTranslationX(), as translationX vertex shader variable value.
    location = gl.getUniformLocation(shaderProgram, "translationX");
    gl.uniform1f(location, getTranslationX());

    // Use the current value stored in the model, getTranslationY(), as translationY vertex shader variable value.
    location = gl.getUniformLocation(shaderProgram, "translationY");
    gl.uniform1f(location, getTranslationY());

    // Use the current values of the foreground color components as color vertex shader variables values.
    location = gl.getUniformLocation(shaderProgram, "color");
    gl.uniform4f(location, foregroundColor.red, foregroundColor.green, foregroundColor.blue, foregroundColor.alpha);

    // Draw points (all data).
    gl.drawArrays(gl.POINTS, 0, data.length / 2);
  }
  
  if ((data = getSelectedData()) != null) {

    var selectedBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, selectedBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW);
    vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "vPosition");
    gl.enableVertexAttribArray(vertexPositionAttribute);
    gl.vertexAttribPointer(vertexPositionAttribute, 2, gl.FLOAT, false, 0, 0);

    // Use the current values of the selection color components as color vertex shader variables values.
    location = gl.getUniformLocation(shaderProgram, "color");
    gl.uniform4f(location, selectionColor.red, selectionColor.green, selectionColor.blue, selectionColor.alpha);

    // Draw points (selected data).
    gl.drawArrays(gl.POINTS, 0, data.length / 2);
  }

  // Skip if no rubberband data available
  if (rubberband) {
    var rubberbandBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, rubberbandBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, rubberbandData, gl.STATIC_DRAW);
    vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "vPosition");
    gl.enableVertexAttribArray(vertexPositionAttribute);
    gl.vertexAttribPointer(vertexPositionAttribute, 2, gl.FLOAT, false, 0, 0);

    // Use the current values of the selection color components as color vertex shader variables values.
    location = gl.getUniformLocation(shaderProgram, "color");
    gl.uniform4f(location, selectionColor.red, selectionColor.green, selectionColor.blue, selectionColor.alpha);
			
    // Set the width of the rubberband rectangle as pointSize vertex shader variable.
    location = gl.getUniformLocation(shaderProgram, "pointSize");
    gl.uniform1i(location, rubberbandWidth);

    // Draw a line loop (rubberband rectangle data).
    gl.drawArrays(gl.LINE_LOOP, 0, rubberbandData.length / 2);
  }

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

// END Chart



// Starts JavaScript processgin when the page loads.

function init() {
  file = '';
  setTitle(file);
  chooser = document.querySelector('#fileDialog');
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
      modelEvent.selection = true;
  	  window.dispatchEvent(modelEvent);
      file = f;
      setTitle(file.name);
    }, false);
    reader.readAsText(f);
  }, false);

  model = new Model();
  modelEvent = new CustomEvent('model', {'detail': {'row':-1,'col':-1,'val':0, 'selection':false}});
  
  window.addEventListener('model', modelChange, false);
  
  var tableholder = document.getElementById('tableholder');
  tableholder.addEventListener('mousemove', sizeChange, false);
  
  pointSelect = document.getElementById('pointsize');  
  pointSelect.addEventListener('change', pointSizeChange, false);  
  pointSelect.value = '5'; 
  pointSize = parseInt(pointSelect.value); 
  
  backgroundInput = document.getElementById('backgroundcolor');
  backgroundInput.addEventListener('change', backgroundColorChange, false);
  backgroundInput.value = 'white';
  backgroundInput.style.backgroundColor = backgroundInput.value;
  backgroundColor = textToColor(backgroundInput.value);
  
  foregroundInput = document.getElementById('foregroundcolor');
  foregroundInput.addEventListener('change', foregroundColorChange, false);
  foregroundInput.value = 'blue';
  foregroundInput.style.backgroundColor = foregroundInput.value;
  foregroundColor = textToColor(foregroundInput.value);

  selectionInput = document.getElementById('selectioncolor');
  selectionInput.addEventListener('change', selectionColorChange, false);
  selectionInput.value = 'red';
  selectionInput.style.backgroundColor = selectionInput.value;
  selectionColor = textToColor(selectionInput.value);

  rubberband = false;
  rubberbandWidth = 3;
  rubberbandData = new Float32Array(8);

  canvas = document.getElementById('canvas');
  canvas.addEventListener('mousemove', mouseMove, false);
  canvas.addEventListener('mousedown', mouseDown, false);
  canvas.addEventListener('mouseup', mouseUp, false);
  gl = null;
  try {
    gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
  }
  catch (e) {
  }
  if (gl) {
    initShaders();
    display();
  }
}

window.addEventListener("load", init, false);
