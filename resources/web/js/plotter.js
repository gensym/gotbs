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

function flatten(nested_arrays) {
  return nested_arrays.reduce(function(a, b) { 
    return a.concat(b); 
  });
}

function compose(g, f) { 
  return function(x) { 
    return g(f(x));
  }
}
