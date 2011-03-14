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

function partial(fn) {
  var args = Array.prototype.slice.call(arguments);
  args.shift();
  return function () {
    var new_args = Array.prototype.slice.call(arguments);
    return fn.apply(window, args.concat(new_args));
  }
}


function normalize(min_width, min_height, max_width, max_height, width, height, waypoint) {
  var datum = {};
  datum['lon'] = (waypoint['lon'] - min_width) / (max_width - min_width) * width;
  datum['lat'] = height - (waypoint['lat'] - min_height) / (max_height - min_height) * height;
  return datum;
}

function plot_waypoints(canvas_id, waypoints) {
  var canvas = $(canvas_id)[0];
  var width = canvas.width;
  var height = canvas.height;
  
  var lats = $.map(waypoints, function (x) { return x['lat']; });                    
  var max_lat = max(lats);
  var min_lat = min(lats);

  var lons = $.map(waypoints, function (x) { return x['lon']; });                    
  var max_lon = max(lons);
  var min_lon = min(lons);

  var normalizer = partial(normalize, min_lon, min_lat, max_lon, max_lat, width, height);
  var normalized_waypoints = $.map(waypoints, normalizer);

  if (canvas.getContext && normalized_waypoints.length > 0) {
    var ctx = canvas.getContext('2d');
    ctx.beginPath();
    ctx.moveTo(normalized_waypoints[0]['lon'], normalized_waypoints[0]['lat']);
    for (var i = 1; i < normalized_waypoints.length; i++) {
      ctx.lineTo(normalized_waypoints[i]['lon'], normalized_waypoints[i]['lat']);
    }
    ctx.stroke();
  }    
}

