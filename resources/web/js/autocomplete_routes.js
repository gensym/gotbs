$(document).ready(function() {

  $('.route-picker .loader').hide();
  $('#route-direction-selection').hide();
  $('#add-route-button').hide();
  $('#route-selection').submit(get_route_data);

  function accept_direction(rtid, directions) {
    var direction_input = '<div class="available-direction"><input id="route-direction-${i}" name="route-direction" type="radio" class="field radio direction-radio" value="${direction}" tabindex="${i + 1}" /><label class="choice" for="route-direction-${i}" >${direction}</label></div>';
    $.each(directions,  function(i, direction){ 
      $.tmpl(direction_input, {"direction": direction, "i": i}).appendTo('#available-route-directions');
    });
    $('.route-picker .loader').hide();
    $('#route-direction-selection').show();
    $('.direction-radio').first().focus();
    $('.direction-radio').first().each(function(i,r) { r.checked = "checked" });
    $('#route-selection').data('rt', rtid);
    $('#add-route-button').show();}

  function get_available_directions(rtid) {
    // TODO - show a spinner here
    $(".available-direction").remove();
    $('.route-picker .loader').show();
    $.get("/routes/directions.json", {term: rtid}, 
          function(directions) {
            accept_direction(rtid, directions);
          });
  }

  function get_route_id(route_name, rt_fn) {
    $.getJSON('/routes/route-descriptor.json',
              {term: route_name},
              function(route) {
                rt_fn(route["rt"]);
              });
  }

  $("input#route-text").autocomplete({
    source: 
    function(request, response) {
      $.getJSON('/routes/available.json', request, function(data) {
        response(data);
      })
    },
    change: function() {
      get_route_id(this.value, function(rtid) {
        get_available_directions(rtid);
      });
    }
  });

  var ws = $.websocket("ws://127.0.0.1:8888/topics", {
    events: {
             updated: function(e) { 
               $.each(e.message, function(idx, vehicle_snapshot) {
                 plot_vehicle('#map', vehicle_snapshot["rt"], vehicle_snapshot["vid"], vehicle_snapshot["lat"], vehicle_snapshot["lon"]);
               });
             }
    }
  });

  function highlight_route(route_item) {
    route_item.addClass('highlighted-route');
  }

  function unhighlight_route(route_item) {
    route_item.removeClass('highlighted-route');
  }

  var routes = {}
  function add_route(name, direction, data) {
    var key = [name, direction]
    if (!routes.hasOwnProperty(key)) {
      routes[key] = true;
      ws.send('subscribe', {topic: key});
      plot_waypoints('#map', name, data);
      var item = $.tmpl("<li id=\"${elem_id}\">${name} - ${direction}</li>", 
                        {"name": name, "direction": direction });

      item.route_name = name;
      item.route_direction = direction;
     
      item.mouseover(partial(highlight_route, item));
      item.mouseout(partial(unhighlight_route, item));
      item.appendTo("#displayed-routes");
    }
  }

  function get_route_data() {
    var route = $('#route-selection').data('rt');
    var direction = $("input[name='route-direction']:checked").val();
    if (!route) {
      alert("Please select a route.")
    } else if (!direction) {
      // waiting for direction. An alert here is annoying
    } else {
      $('.route-picker .loader').show();

      $.get('/routes/waypoints.json', {"route": route, "direction": direction}, 
            function(data) {
              add_route(route, direction, data);
              $('#route-selection')[0].reset();
              $('.route-picker .loader').hide();
              $('#add-route-button').hide();
              $('#route-direction-selection .available-direction').remove();
              $('#route-direction-selection').hide();
              $("input#route-text").focus();
            });
    }
  }

  $("input#route-text").focus();
});
