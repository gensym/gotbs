goog.provide('gotbs.js.timeline');
goog.require('cljs.core');
document.write("<p>WTF - get. it. on.</p>");
console.log("FFFuuu");
gotbs.js.timeline.draw_path = (function draw_path(context,path){
if(!(cljs.core.empty_QMARK_.call(null,path)))
{var vec__2884 = cljs.core.first.call(null,path);
var x = cljs.core.nth.call(null,vec__2884,0,null);
var y = cljs.core.nth.call(null,vec__2884,1,null);
context.beginPath();
context.moveTo(x,y);
var G__2885_2887 = cljs.core.seq.call(null,cljs.core.rest.call(null,path));
while(true){
if(G__2885_2887)
{var vec__2886_2888 = cljs.core.first.call(null,G__2885_2887);
var x_2889__$1 = cljs.core.nth.call(null,vec__2886_2888,0,null);
var y_2890__$1 = cljs.core.nth.call(null,vec__2886_2888,1,null);
context.lineTo(x_2889__$1,y_2890__$1);
{
var G__2891 = cljs.core.next.call(null,G__2885_2887);
G__2885_2887 = G__2891;
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
return cljs.core.map.call(null,(function (p__2894){
var vec__2895 = p__2894;
var x = cljs.core.nth.call(null,vec__2895,0,null);
var y = cljs.core.nth.call(null,vec__2895,1,null);
return cljs.core.PersistentVector.fromArray([((x - x0) / (xrange * width)),((y - y0) / (yrange * height))], true);
}),path);
});
gotbs.js.timeline.find_range = (function find_range(pointsets){
return cljs.core.reduce.call(null,(function (p__2900,p__2901){
var vec__2902 = p__2900;
var xn = cljs.core.nth.call(null,vec__2902,0,null);
var xx = cljs.core.nth.call(null,vec__2902,1,null);
var yn = cljs.core.nth.call(null,vec__2902,2,null);
var yx = cljs.core.nth.call(null,vec__2902,3,null);
var vec__2903 = p__2901;
var x = cljs.core.nth.call(null,vec__2903,0,null);
var y = cljs.core.nth.call(null,vec__2903,1,null);
return cljs.core.PersistentVector.fromArray([((xn < x) ? xn : x),((xx > x) ? xx : x),((yn < y) ? yn : y),((yx > y) ? yx : y)], true);
}),cljs.core.PersistentVector.fromArray([0,0,0,0], true),cljs.core.apply.call(null,cljs.core.concat,pointsets));
});
gotbs.js.timeline.draw_canvas = (function draw_canvas(canvas_id,paths){
var elem = document.getElementById(canvas_id);
var context = elem.getContext("2d");
var width = elem.width;
var height = elem.height;
var vec__2906 = gotbs.js.timeline.find_range.call(null,paths);
var x0 = cljs.core.nth.call(null,vec__2906,0,null);
var x1 = cljs.core.nth.call(null,vec__2906,1,null);
var y0 = cljs.core.nth.call(null,vec__2906,2,null);
var y1 = cljs.core.nth.call(null,vec__2906,3,null);
var points = cljs.core.map.call(null,cljs.core.partial.call(null,gotbs.js.timeline.scale_points,x0,x1,width,y0,y1,height),paths);
context.clearRect(0,0,width,height);
context.save();
context.lineWidth = 1.5;
var G__2907_2908 = cljs.core.seq.call(null,points);
while(true){
if(G__2907_2908)
{var path_2909 = cljs.core.first.call(null,G__2907_2908);
gotbs.js.timeline.draw_path.call(null,context,path_2909);
{
var G__2910 = cljs.core.next.call(null,G__2907_2908);
G__2907_2908 = G__2910;
continue;
}
} else
{}
break;
}
return context.restore();
});
goog.exportSymbol('gotbs.js.timeline.draw_canvas', gotbs.js.timeline.draw_canvas);
