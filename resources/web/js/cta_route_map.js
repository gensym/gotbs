function make_route(name, coordinates) {
  return {name: name, coordinates: coordinates, vehicles :{}};
}

function route_canvas(canvas_id) {
  var canvas = $(canvas_id)[0];
  if (!canvas || !canvas.getContext) { 
    return;
  }

  if (!canvas.routes) {
    canvas.routes = {};

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

    var drawPoint = function(point) {
      var ctx = canvas.getContext('2d');
      ctx.save();
      ctx.strokeStyle = "rgb(176,216,244)";
      ctx.beginPath();
      ctx.arc(point[0], point[1], 5.0, 0, Math.PI*2);
      ctx.closePath();
      ctx.stroke();
      ctx.restore();
    }

    canvas.addRoute = function(rtid, coordinates) {
      this.routes[rtid] = make_route(rtid, coordinates);
    };

    canvas.updateVehicle = function(rtid, vid, lat, lon) {
      this.routes[rtid].vehicles[vid] =  {'lat' : lat, 'lon': lon};
    }

    canvas.highlightRoute = function(rt) {
    }

    var scaler = partial(scale_coordinate, canvas.width, canvas.height);
    var to_point = function(x) { return [x['lon'],x['lat']]};
    var coordinates = function(route) { return route.coordinates; }

    canvas.redraw = function() {
      var ctx = this.getContext('2d');


      var pattern_points = _.map(flatten(_.map(_.values(this.routes), coordinates)), to_point);
      var translater = compose(scaler, compose(make_normalizer(pattern_points), to_point));

      ctx.clearRect(0, 0, this.width, this.height);
      $.each(this.routes, function(rtid, route) {
        drawPath(route.coordinates.map(translater));
        $.each(route.vehicles, function(vid, veh) {
          drawPoint(translater(veh));
        });
      })

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

function highlight_route(canvas_id, rt) {
  var canvas = route_canvas(canvas_id);
  if (!canvas) {
    return;
  }
  canvas.highlightRoute(rt);
  canvas.redraw();

  
}

function plot_vehicle(canvas_id, rt, vid, lat, lon) {
  var canvas = route_canvas(canvas_id);

  if (!canvas) {
    return;
  }

  canvas.updateVehicle(rt, vid, lat, lon);
  canvas.redraw();
}
