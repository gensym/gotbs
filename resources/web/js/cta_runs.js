function run_canvas(canvas_id, start_time, end_time) {
  var elem = $(canvas_id)[0];
  if (!elem || !elem.getContext) {
    return;
  }
  
  var toPoint = function(width, height, dist, time) {
    var timespan = end_time - start_time;
    var x = (time - start_time) / timespan * width;
    
    return [x, dist * height];
  }

  var runs = [];
  
  var canvas = {
    
    addRun : function(run) {
      runs.push(run);
    },

    redraw : function() {

      var ctx = elem.getContext('2d');
      var width = elem.width;
      var height = elem.height;

      ctx.clearRect(0, 0, width, height);
      ctx.save();
      ctx.lineWidth = 1.5;

      var mapper = partial(toPoint, elem.width, elem.height)

      _.each(runs, function(run) {
        ctx.beginPath();
        var points = run.map(function(point) {
          return mapper(point.dist, point.time);
        });
       
        
        ctx.moveTo(points[0][0], points[0][1]);
        for (var i = 1; i < points.length; i++) {
          ctx.lineTo(points[i][0], points[i][1]);
        }
        ctx.stroke();
      });
      ctx.restore();
    },

  };

  return canvas;
}

function get_run_data(time_range, data_func) {
  $.get('/runs/for_route.json', {from: time_range[0].toJSON(), to: time_range[1].toJSON() }, data_func);
}

function plot_runs(canvas_id, start_time, end_time, runs) {
  
  var start = Date.parse(start_time);
  var end = Date.parse(end_time);
  var canvas = run_canvas(canvas_id, start, end);
  if (!canvas) {
    return;
  }
  
  _.each(runs, 
         function(run) {
           var r = 
             _.map(run, function(runpoint) {
               var f = 
                { dist: runpoint.dist, 
                  time: Date.parse(runpoint.time) };
               return f;
               });
           canvas.addRun(r);
         });
  canvas.redraw();
}
