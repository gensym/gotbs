(ns gotbs.stopinfo
  (:use gotbs.bustracker))

(defn make-stop-arrival-info [route direction stop-id]
  (list
   (assoc (first (make-predictions route stop-id))
     :stop-name
     (stop-name route direction stop-id))))
