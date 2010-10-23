(ns gotbs.busdata
  (:use gotbs.bustracker))

(def get-candidate-buses
     "Returns a seq of vehicle data structs for buses passengers on a given route may be on"
     [route direction begin-stop-id end-stop-id]
     (let [prediction-vehicles ()])
     )