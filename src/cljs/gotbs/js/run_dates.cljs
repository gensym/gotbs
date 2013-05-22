(ns gotbs.js.timeline)

(defn run-dates [label-selection date-picker date-picker-button]
  {:start  #inst "2012-03-07T15:00:00"
   :end  #inst "2012-03-07T18:00:00"
   :date-picker (.getElementById js/document date-picker)
   :button (.getElementById js/document date-picker-button)
   :label-select (.getElementById label-selection)})

(defn update-label [run-dates]
  (let [date-string
        (.format (js/moment (:start run-dates))
                 "dddd, MMMM Do YYYY")]
    
    ))
