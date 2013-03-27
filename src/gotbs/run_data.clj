(ns gotbs.run-data
  (:require [gotbs.history.run-records :as history]
            [clj-time.coerce :as coerce])
    (:import java.text.SimpleDateFormat))

(defn for-route [start-time end-time]
  (let [period-millis (- (.getTime end-time) (.getTime start-time))
        as-date (fn[time-ratio]
                  (.toDate
                   (.plusMillis (coerce/from-date start-time)
                                (* time-ratio period-millis))))]
    {:start-time start-time
     :end-time end-time
     :runs
     [[{ :dist 0.1, :time (as-date 0)},
       { :dist 0.5, :time (as-date 0.25) },
       { :dist 0.7, :time (as-date 0.50)}, 
       { :dist 1.0, :time (as-date 0.60)}]

      [{ :dist 0.1 :time (as-date 0.25)}
       { :dist 1.0 :time (as-date 1.0)}]]
     }))
