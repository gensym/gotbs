function plot_waypoints(canvas_id, waypoints) { 
  var canvas = $(canvas_id)[0];
  var points = waypoints.map(function(x) { return [x['lon'],x['lat']]});
  var scaler = partial(scale_coordinate, canvas.width, canvas.height);
  var translater = compose(scaler, make_normalizer(points));

  var to_plot = points.map(translater);
  if (canvas.getContext && to_plot.length > 0) {
    var ctx = canvas.getContext('2d');
    ctx.beginPath();
    ctx.moveTo(to_plot[0][0], to_plot[0][1]);
    for (var i = 1; i <  to_plot.length; i++) {
      ctx.lineTo(to_plot[i][0], to_plot[i][1]);
    }
    ctx.stroke();
  }
}
