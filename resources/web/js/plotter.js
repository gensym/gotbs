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

function make_normalizer(coordinates) {
  var x_s = $.map(coordinates, function(x) { return x[0]});
  var y_s = $.map(coordinates, function(x) { return x[1]});

  var min_x = min(x_s);
  var min_y = min(y_s);

  var width = max(x_s) - min_x;
  var height = max(y_s) - min_y;

  var scale = max([width, height]);

  var x_shift = height > width ? 0.5 - width / height / 2 : 0;
  var y_shift = width > height ? 0.5 - height / width / 2 : 0;

  return function(point) { 
    var x = (point[0] - min_x) / scale + x_shift;
    var y = (point[1] - min_y) / scale + y_shift;
    return [x,y];
  };
}

function compose(g, f) { 
  return function(x) { 
    return g(f(x));
  }
}

function scale_coordinate(width, height, normalized_point) {
  return [normalized_point[0] * width, normalized_point[1] * height];
}

function plot_waypoints(canvas_id, waypoints) { 
  var canvas = $(canvas_id)[0];
  var points = $.map(waypoints, function(x) { return [x['lat'],x['lon']]});

}



