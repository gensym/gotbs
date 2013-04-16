goog.provide('gotbs.js.timeline');
goog.require('cljs.core');
goog.require('goog.net.XhrIo');
document.write("<p>WTF - get. it. on!</p>");
console.log("FFFuuu");
gotbs.js.timeline.draw_path = (function draw_path(context,path){
if(!(cljs.core.empty_QMARK_.call(null,path)))
{var vec__2889 = cljs.core.first.call(null,path);
var x = cljs.core.nth.call(null,vec__2889,0,null);
var y = cljs.core.nth.call(null,vec__2889,1,null);
context.beginPath();
context.moveTo(x,y);
var G__2890_2892 = cljs.core.seq.call(null,cljs.core.rest.call(null,path));
while(true){
if(G__2890_2892)
{var vec__2891_2893 = cljs.core.first.call(null,G__2890_2892);
var x_2894__$1 = cljs.core.nth.call(null,vec__2891_2893,0,null);
var y_2895__$1 = cljs.core.nth.call(null,vec__2891_2893,1,null);
context.lineTo(x_2894__$1,y_2895__$1);
{
var G__2896 = cljs.core.next.call(null,G__2890_2892);
G__2890_2892 = G__2896;
continue;
}
} else
{}
break;
}
return context.stroke();
} else
{return null;
}
});
gotbs.js.timeline.scale_points = (function scale_points(x0,x1,width,y0,y1,height,path){
var xrange = (x1 - x0);
var yrange = (y1 - y0);
return cljs.core.map.call(null,(function (p__2899){
var vec__2900 = p__2899;
var x = cljs.core.nth.call(null,vec__2900,0,null);
var y = cljs.core.nth.call(null,vec__2900,1,null);
return cljs.core.PersistentVector.fromArray([((x - x0) / (xrange * width)),((y - y0) / (yrange * height))], true);
}),path);
});
gotbs.js.timeline.find_range = (function find_range(pointsets){
var vec__2906 = cljs.core.ffirst.call(null,pointsets);
var x = cljs.core.nth.call(null,vec__2906,0,null);
var y = cljs.core.nth.call(null,vec__2906,1,null);
return cljs.core.reduce.call(null,(function (p__2907,p__2908){
var vec__2909 = p__2907;
var xn = cljs.core.nth.call(null,vec__2909,0,null);
var xx = cljs.core.nth.call(null,vec__2909,1,null);
var yn = cljs.core.nth.call(null,vec__2909,2,null);
var yx = cljs.core.nth.call(null,vec__2909,3,null);
var vec__2910 = p__2908;
var x__$1 = cljs.core.nth.call(null,vec__2910,0,null);
var y__$1 = cljs.core.nth.call(null,vec__2910,1,null);
return cljs.core.PersistentVector.fromArray([((xn < x__$1) ? xn : x__$1),((xx > x__$1) ? xx : x__$1),((yn < y__$1) ? yn : y__$1),((yx > y__$1) ? yx : y__$1)], true);
}),cljs.core.PersistentVector.fromArray([x,x,y,y], true),cljs.core.apply.call(null,cljs.core.concat,pointsets));
});
gotbs.js.timeline.draw_canvas = (function draw_canvas(canvas_id,paths){
var elem = document.getElementById(canvas_id);
var context = elem.getContext("2d");
var width = elem.width;
var height = elem.height;
var vec__2913 = gotbs.js.timeline.find_range.call(null,paths);
var x0 = cljs.core.nth.call(null,vec__2913,0,null);
var x1 = cljs.core.nth.call(null,vec__2913,1,null);
var y0 = cljs.core.nth.call(null,vec__2913,2,null);
var y1 = cljs.core.nth.call(null,vec__2913,3,null);
var points = cljs.core.map.call(null,cljs.core.partial.call(null,gotbs.js.timeline.scale_points,x0,x1,width,y0,y1,height),paths);
context.clearRect(0,0,width,height);
context.save();
context.lineWidth = 1.5;
var G__2914_2915 = cljs.core.seq.call(null,points);
while(true){
if(G__2914_2915)
{var path_2916 = cljs.core.first.call(null,G__2914_2915);
gotbs.js.timeline.draw_path.call(null,context,path_2916);
{
var G__2917 = cljs.core.next.call(null,G__2914_2915);
G__2914_2915 = G__2917;
continue;
}
} else
{}
break;
}
return context.restore();
});
goog.exportSymbol('gotbs.js.timeline.draw_canvas', gotbs.js.timeline.draw_canvas);
gotbs.js.timeline.get_uri = (function get_uri(start_date,end_date){
var uri = (new goog.Uri("/"));
var qr = uri.getQueryData();
qr.add("from",start_date.toJSON());
qr.add("to",end_date.toJSON());
return uri.setPath("/runs/for_route.edn");
});
gotbs.js.timeline.get_runs_json = (function get_runs_json(start_date,end_date){
return goog.net.XhrIo.send(gotbs.js.timeline.get_uri.call(null,start_date,end_date),(function (message){
var runset = message.target.getResponseText();
console.log(runset);
document.runs = runset;
return null;
}));
});
gotbs.js.timeline.get_data = (function get_data(start_date,end_date){
return gotbs.js.timeline.get_runs_json.call(null,start_date,end_date);
});
goog.exportSymbol('gotbs.js.timeline.get_data', gotbs.js.timeline.get_data);
gotbs.js.timeline.start_date = (new Date(1331154000000));
gotbs.js.timeline.end_date = (new Date(1331164800000));
