$(document).ready(function() {

  $('#route-direction-selection').hide();
  
  $('#add-route-button').hide();
  $('#route-selection').submit(get_route_data);
  $("input#route-text").autocomplete({
    source: 
    function(request, response) {
      $.getJSON('/routes/available.json', request, function(data) {
        response(data);
      })
    }
  });

  function add_route(name, data) {
    plot_waypoints('#map', name, data);
    $.tmpl("<li>${name}</li>", {"name": name}).appendTo("#displayed-routes");
  }

  function get_route_data() {
    var route = $("input[name='route-name']").val();
    var direction = $("input[name='route-direction']:checked").val();
    if (!route) {
      alert("Please select a route.")
    } else if (!direction) {
      // waiting for direction. An alert here is annoying
    } else {
      $.get('/routes/waypoints.json', {"route": route, "direction": direction}, 
            function(data) {
              add_route(route, data);
              $('#route-selection')[0].reset();
              $('#add-route-button').hide();
              $('#route-direction-selection .available-direction').remove();
              $('#route-direction-selection').hide();
              $("input#route-text").focus();
            });
    }
  }

  function accept_direction(directions) {
    var direction_input = '<div class="available-direction"><input id="route-direction-${i}" name="route-direction" type="radio" class="field radio direction-radio" value="${direction}" tabindex="${i + 1}" /><label class="choice" for="route-direction-${i}" >${direction}</label></div>';
    $.each(directions,  function(i, direction){ 
      $.tmpl(direction_input, {"direction": direction, "i": i}).appendTo('#available-route-directions');
    });
    $('#route-direction-selection').show();
    $('.direction-radio').first().focus();
    $('.direction-radio').first().each(function(i,r) { r.checked = "checked" });
    $('#add-route-button').show();
  }

  function get_available_directions() {
    $(".available-direction").remove();
    $.get("/routes/directions.json", {term: this.value}, accept_direction);
  }

  $("input#route-text").change(get_available_directions);
  $("input#route-text").focus();
  
});
