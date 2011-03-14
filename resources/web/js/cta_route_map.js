
function max(list) {
  if (list.length == 0) {
    return null;
  }
  var ret = list[0];
  for (var i = 1; i < list.length; i++) {
    if (list[i] > ret) {
      ret = list[i];
    }                        
  }
  return ret;
}

function min(list) {
  if (list.length == 0) {
    return null;
  }
  var ret = list[0];
  for (var i = 1; i < list.length; i++) {
    if (list[i] < ret) {
      ret = list[i];
    }                        
  }
  return ret;
}



var canvas = $('#map')[0];
var width = canvas.width;
var height = canvas.height;

var lats = $.map(data, function (x) { return x['lat']; });                    
var max_lat = max(lats);
var min_lat = min(lats);

var lons = $.map(data, function (x) { return x['lon']; });                    
var max_lon = max(lons);
var min_lon = min(lons);

function partial(fn) {
  var args = Array.prototype.slice.call(arguments);
  args.shift();
  return function () {
    var new_args = Array.prototype.slice.call(arguments);
    return fn.apply(window, args.concat(new_args));
  }
}

// I think floating point errors are killing us here
function normalize(min_width, min_height, max_width, max_height, width, height, waypoint) {
  var datum = {};
  datum['lon'] = (waypoint['lon'] - min_width) / (max_width - min_width) * width;
  datum['lat'] = height - (waypoint['lat'] - min_height) / (max_height - min_height) * height;
  return datum;
}

var normalizer = partial(normalize, min_lon, min_lat, max_lon, max_lat, width, height);
var normalized_data = $.map(data, normalizer);

if (canvas.getContext && normalized_data.length > 0) {
  var ctx = canvas.getContext('2d');
  ctx.beginPath();
  ctx.moveTo(normalized_data[0]['lon'], normalized_data[0]['lat']);
  for (var i = 1; i < normalized_data.length; i++) {
    ctx.lineTo(normalized_data[i]['lon'], normalized_data[i]['lat']);
  }
  ctx.stroke();
}    
