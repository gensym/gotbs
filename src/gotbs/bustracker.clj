(ns gotbs.bustracker
  (:use
   gotbs.cta.bustracker-api
   [clojure.tools.logging :as log]
   clojure.java.io (input-stream))
  (:require
   [gotbs.cta.bustracker-data-parser :as parser]
   [gotbs.util.http-utils :as http]))

(def http-api (make-bustracker (System/getenv "CTA_BUSTRACKER_API_KEY")))

;; BEGIN HARDCODING
(def west-bound "West Bound")

(def north-bound "North Bound")

(def milwaukee-and-north-avenue-damen-56 "15847")

(def milwaukee-and-talman-56 "5552")

(def madison-and-clinton-56 "455")

(def destination "Jefferson Park Blue Line")
;; END HARDCODING

(defn fetch-stop-data [route direction]
  (parser/as-stop-data (stops http-api route direction)))

(defn stop-id [route direction stop-name]
  (first (map :stpid (filter #(= (:stpnm %1) stop-name) (fetch-stop-data route direction)))))

(defn stop-name [route direction stop-id]
     (first (map :stpnm (filter #(= (:stpid %) stop-id) (fetch-stop-data route direction)))))

(defn fetch-vehicles-on-route-data [route]
  (parser/as-vehicle-data (vehicles-on-route http-api route)))

(defn fetch-vehicles-data [& vehicle_ids]
  (let [vids  (reduce #(str %1 "," %2) vehicle_ids)]
    (parser/as-vehicle-data (vehicle-by-id http-api vids))))

(defn fetch-pattern-data-by-id [pattern-id]
  "Fetch data about the pattern by the pattern ID. Excludes waypoints"
  (parser/as-pattern-data  (pattern-by-id http-api pattern-id)))

(defn fetch-pattern-data-for-route [route dir]
  "Fetch the pattern data (including waypoints) by the route and direction"
  (parser/as-waypoint-data  (patterns-for-route http-api route) dir))

(defn fetch-routes []
  (parser/as-routes (all-routes http-api)))

(defn fetch-route-direction [route]
  (parser/as-route-direction (directions http-api route)))

(defn destination [route dir]
  (:stpnm (last (fetch-pattern-data-for-route route dir))))

(defn fetch-prediction-data [route stop-id]
  (parser/as-prediction (predictions http-api route stop-id)))

(defstruct prediction :route :direction :eta)

(defn make-prediction [bustracker-prediction]
  (struct-map
      prediction
      :route (:rt bustracker-prediction)
      :direction (:rtdir bustracker-prediction)
      :eta (last (re-seq #"\S+" (:prdtm bustracker-prediction)))))

(defn make-predictions [route stop-id]
  (map
   make-prediction
   (fetch-prediction-data route stop-id)))
