function route_canvas(canvas_id) {
  var canvas = $(canvas_id)[0];
  if (!canvas || !canvas.getContext) { 
    return;
  }

  if (!canvas.routes) {
    canvas.routes = [];

    var drawPath = function(coordinates) {
      var ctx = canvas.getContext('2d');
      if (coordinates.length > 0) {
        ctx.beginPath();
        ctx.moveTo(coordinates[0][0], coordinates[0][1]);
        for (var i = 1; i < coordinates.length; i++) {
          ctx.lineTo(coordinates[i][0], coordinates[i][1]);
        }
        ctx.stroke();
      }
    };

    canvas.addRoute = function(route) {
      this.routes = this.routes.concat([route]);
    };

    canvas.redraw = function() {
      var ctx = this.getContext('2d');
      ctx.clearRect(0, 0, this.width, this.height);
      var routes = this.routes;
      this.with_transform(function() {
        for (var i = 0; i < routes.length; i++) {
          drawPath(routes[i]);
        }
      });
    };

    canvas.build_matrix = function() {
      var coordinates = flatten(this.routes);
      var x_s = coordinates.map(function(x) { return x[0]});
      var y_s = coordinates.map(function(x) { return x[1]});

      var min_x = min(x_s);
      var min_y = min(y_s);
      
      var width = max(x_s) - min_x;
      var height = max(y_s) - min_y;
      
      var scale = max([width, height]);
      
      var x_shift = height > width ? 0.5 - width / height / 2 : 0;
      var y_shift = width > height ? 0.5 - height / width / 2 : 0;
      
      var a = 1 / scale * this.width; // x scale
      var c = 0; // don't rotate
      var e = x_shift * this.width - min_x / scale * this.width; // x offset
      
      var b = 0; // don't rotate
      var d = 1 / scale * this.height; // y scale
      var f = y_shift * this.height - min_y / scale * this.height; // y offset
      return [[a,c,e],[b,d,f],[0,0,1]]
      //return [[a,c,e],[b,d,f][0,0,1]];
    }

    canvas.with_transform = function(fn) {
      var ctx = this.getContext('2d');
      ctx.save();
      var matrix = this.build_matrix();
      ctx.setTransform(matrix[0][0], matrix[1][0], matrix[0][1], matrix[1][1], matrix[0][2], matrix[1][2]);
      fn();
      ctx.restore();
    }
  }
  return canvas;
}

function plot_waypoints(canvas_id, waypoints) { 
  var canvas = route_canvas(canvas_id);

  if (!canvas) {
    return;
  }
  var points = waypoints.map(function(x) { return [x['lon'],x['lat']]});
  
  //var scaler = partial(scale_coordinate, canvas.width, canvas.height);
  //var translater = compose(scaler, make_normalizer(points));
  //var to_plot = points.map(translater);

  canvas.addRoute(points);
  canvas.redraw();
}
