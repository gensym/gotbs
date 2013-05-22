function run_dates(label_selection, date_picker, date_picker_button) {

  var start = new Date(2012, 2, 7, 15);
  var end = new Date(2012, 2, 7, 18);

 // var dpe = $(date_picker);
//  var dpb = $(date_picker_button);
  

  function updateLabel(date) {
    var formatted = moment(date).format("dddd, MMMM Do YYYY");
    $(label_selection).text(formatted);
  }

  function onSelect(datestr, dp) {
    updateLabel(new Date(datestr));
    dpe.hide();
    dpb.show();
    dpb.show();
  }

  return {
    init: function() {
  //    dpe.datepicker();
  //  dpe.datepicker("option", "onSelect", onSelect);
     // dpe.hide();
//      dpb.click(function(event) {
//        dpe.show();
//        $(event.target).hide();
//        return false;
//      });

//      updateLabel(start);
    },

    range: [start, end],
  }
}
