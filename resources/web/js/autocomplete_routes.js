
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

  $("input#route-text").change(function() {
    var direction_input = '<div class="available-direction"><input id="route-direction-${i}" name="route-direction" type="radio" class="field radio direction-radio" value="${direction}" tabindex="${i + 1}" /><label class="choice" for="route-direction-${i}" >${direction}</label></div>';
    $(".available-direction").remove();
    $.ajax({
      url:"/routes/directions.json",
      data: {term: this.value},
      success: function(directions) { 
        $.each(directions,  function(i, direction){ 
          $.tmpl(direction_input, {"direction": direction, "i": i}).appendTo('#available-route-directions');
        });
        $('.direction-radio').first().focus();
        $('#add-route-button').show();
        $('#route-selection').onSubmite = function() { return true;
                                              }

        // todo - add form validation - turn submit into an ajax
        // submit to add routes
      }});
  });
});
