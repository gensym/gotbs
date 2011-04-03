function load_script(name, callback) {
  var html_doc = document.getElementsByTagName('head').item(0);
  var tag = document.createElement('script');
  tag.type = "text/javascript";
  tag.src = name;

  tag.onreadystatechange = callback;
  tag.onload = callback;

  html_doc.appendChild(tag);
}

function load_css(name) {
  var tag = document.createElement('link');
  tag.rel = "stylesheet";
  tag.type = "text/css";
  tag.href = name;
  document.body.appendChild(tag);
}

function load_cdn_script(cdn_addr, local_addr, test_fn, callback) {
  load_script(cdn_addr);
  !test_fn() && load_script(local_addr);
  for (var i = 3; i < arguments.length; i++) {
    load_css(arguments[i]);
  }
}
