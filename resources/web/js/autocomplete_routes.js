function suggest(something) {
  return [
    "50 - Damen",
    "56 - Milwaukee", 
    "72 - Armitage",
    "70 - North",
  ];
}


$(document).ready(function() {
  $("input#route-text").autocomplete({
    source: 
    function(request, response) {
      $.getJSON('/routes/available.json', request, function(data) {
        response(data);
      })
    }
  });
});