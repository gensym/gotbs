
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

var normalized_data = [];

for (var i = 0; i < data.length; i++) {
  var datum = {};
  datum['lon'] = (data[i]['lon'] - min_lon) / (max_lon - min_lon) * width;
  datum['lat'] = (data[i]['lat'] - min_lat) / (max_lat - min_lat) * height;
  normalized_data.push(datum);
}                    

if (canvas.getContext && normalized_data.length > 0) {
  var ctx = canvas.getContext('2d');
  ctx.beginPath();
  ctx.moveTo(normalized_data[0]['lat'], normalized_data[0]['lon']);
  for (var i = 1; i < normalized_data.length; i++) {
    ctx.lineTo(normalized_data[i]['lat'], normalized_data[i]['lon']);
  }
  ctx.stroke();
}    
