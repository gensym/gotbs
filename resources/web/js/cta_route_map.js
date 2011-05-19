function make_route(name, coordinates) {
  return {name: name, coordinates: coordinates};
}

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
        ctx.lineWidth = 1.5;
        ctx.beginPath();
        ctx.moveTo(coordinates[0][0], coordinates[0][1]);
        for (var i = 1; i < coordinates.length; i++) {
          ctx.lineTo(coordinates[i][0], coordinates[i][1]);
        }
        ctx.stroke();
      }
    };

    canvas.addRoute = function(name, coordinates) {
      this.routes = this.routes.concat(make_route(name, coordinates));
    };

    var scaler = partial(scale_coordinate, canvas.width, canvas.height);
    var to_point = function(x) { return [x['lon'],x['lat']]};

    canvas.redraw = function() {
      var ctx = this.getContext('2d');

      var points = flatten(this.routes.map(function(route) { return route.coordinates })).map(to_point);
      var translater = compose(scaler, compose(make_normalizer(points), to_point));

      ctx.clearRect(0, 0, this.width, this.height);
      for (var i = 0; i < this.routes.length; i++) {
        drawPath(this.routes[i].coordinates.map(translater));
      }
    };
  }
  return canvas;
}


function plot_waypoints(canvas_id, route_name, waypoints) { 
 var canvas = route_canvas(canvas_id);

  if (!canvas) {
    return;
  }

  canvas.addRoute(route_name, waypoints);
  canvas.redraw();
}
