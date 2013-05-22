(ns gotbs.js.run-dates
  (:require [domina :as dom]))

(defn init [label-selection date-picker date-picker-button]
  {:start  #inst "2012-03-07T15:00:00"
   :end  #inst "2012-03-07T18:00:00"
   :date-picker (dom/by-id date-picker)
   :button (dom/by-id  date-picker-button)
   :label-select (dom/by-id label-selection)})


(defn start-date [{start :start}]
  start)


(defn end-date [{end :end}]
  end)

(defn update-label [run-dates]
  (let [date-string
        (.format (js/moment (:start run-dates))
                 "dddd, MMMM Do YYYY")]
    (dom/set-text! (:label-select run-dates) date-string)))
