(ns gotbs.run-data
  (:require [gotbs.history.run-records :as history]
            [clj-time.coerce :as coerce]
            [clojure.tools.logging :as log])
  (:import java.text.SimpleDateFormat))


(defn- to-run-graph [start-time end-time runset]
  (let [pdist (history/pattern-distance runset)]
    {:start-time start-time
     :end-time end-time
     :runs
     (map
      (fn [run]
        (map (fn [point]
               {
                :dist (double (/ (:distance point) pdist))
                :time (:time point)
                }) (:points run))
        ) runset)}))

(defn for-route [db start-time end-time]
  (let [cta-route-id "56"
        runs (history/route-runs db cta-route-id start-time end-time)]
    (do
      (log/info "Returning " (count runs) "runs")
      (to-run-graph start-time end-time runs))))

(comment (defn for-route-old [db start-time end-time]
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
              })))
