(ns gotbs.stopinfo
  (:use gotbs.bustracker))

(defn make-stop-arrival-info [stop-id route]
  (list
   (assoc (first (make-predictions route stop-id))
     :stop-name
     (stop-name route stop-id))))
