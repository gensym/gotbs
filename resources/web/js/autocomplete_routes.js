$(document).ready(function() {

  function disable_add_route_form() {
    $('#route-selection').submit(function() { return false });
    $('#add-route-button').hide();
  }

    var direction_input = '<div class="available-direction"><input id="route-direction-${i}" name="route-direction" type="radio" class="field radio direction-radio" value="${direction}" tabindex="${i + 1}" /><label class="choice" for="route-direction-${i}" >${direction}</label></div>';

  function add_available_directions(directions) {
    $.each(directions,  function(i, direction){ 
      $.tmpl(direction_input, {"direction": direction, "i": i}).appendTo('#available-route-directions');
    });
    $('.direction-radio').first().focus();  
  }

  function enable_add_route_form() {
    $('#add-route-button').show();
    $('#route-selection').submit(
      function() { 
        alert($('input#route-text').val());
        alert($('input[name="route-direction"]:checked').val());
        return false;
      });
  }


  $("input#route-text").autocomplete({
    source: 
    function(request, response) {
      $.getJSON('/routes/available.json', request, function(data) {
        response(data);
      })
    }
  });

  // Tabbing from the route-text input should focus any present radio
  // buttons
  $("input#route-text").keydown(function(event) {
    if (event.keyCode == '9') {
      event.preventDefault();
      $('.direction-radio').first().focus();
    }
  });

  $("input#route-text").change(function() {
    $(".available-direction").remove();
    disable_add_route_form();
    $.ajax({
      url:"/routes/directions.json",
      data: {term: this.value},
      success: function(directions) { 
        add_available_directions(directions);
        enable_add_route_form();
      }});
  });

  disable_add_route_form();
});
