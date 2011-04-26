$(document).ready(function() {

  $('#add-route-button').hide();
  $("input#route-text").autocomplete({
    source: 
    function(request, response) {
      $.getJSON('/routes/available.json', request, function(data) {
        response(data);
      })
    }
  });

  function get_route_data() {
    var route = $("input[name='route-name']").val();
    var direction = $("input[name='route-direction']:checked").val();
    if (!route) {
      alert("Please select a route.")
    } else if (!direction) {
      alert("Please select a direction.");
    } else {
      $.get('/routes/waypoints.json', {"route": route, "direction": direction}, 
            function(data) {
              plot_waypoints('#map', data);
            });
    }
  }

  function accept_direction(directions) {
    var direction_input = '<div class="available-direction"><input id="route-direction-${i}" name="route-direction" type="radio" class="field radio direction-radio" value="${direction}" tabindex="${i + 1}" /><label class="choice" for="route-direction-${i}" >${direction}</label></div>';
    $.each(directions,  function(i, direction){ 
      $.tmpl(direction_input, {"direction": direction, "i": i}).appendTo('#available-route-directions');
    });
    $('.direction-radio').first().focus();
    $('#add-route-button').show();
    $('#route-selection').submit(get_route_data);
  }

  function get_available_directions() {
    $(".available-direction").remove();
    $.get("/routes/directions.json", {term: this.value}, accept_direction);
  }

  $("input#route-text").change(get_available_directions);
});
