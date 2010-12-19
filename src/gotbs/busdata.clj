(ns gotbs.busdata
  (:use clojure.set)
  (:use gotbs.bustracker))

(defn stop-pdist [route dir stop-id]
  (Float/parseFloat
   (:pdist
    (first
     (filter
      #(= stop-id (:stpid %))
      (fetch-pattern-data-for-route route dir))))))

(defn in-flight-vehicles [route dir]
  (filter
   (fn [veh]
     (=
      dir
      (:rtdir (fetch-pattern-data-by-id (:pid veh)))))
   (fetch-vehicles-on-route-data route)))

(defn vehicle-direction [vehicle]
  (:rtdir (fetch-pattern-data-by-id
           (:pid vehicle))))

(let
    [compare-stop
     (fn [comparer route dir stop-id]
       (filter
	#(comparer
	  (Float/parseFloat (:pdist %))
	  (stop-pdist route dir stop-id))
	(in-flight-vehicles route dir)))]
  
  (defn fetch-vehicles-past-stop [route dir stop-id]
    (compare-stop > route dir stop-id))

  (defn fetch-vehicles-before-stop [route dir stop-id]
    (compare-stop < route dir stop-id)))

 
(defn fetch-vehicles-between-stops [route dir begin-stop-id end-stop-id]
  (seq
   (intersection
    (set (fetch-vehicles-past-stop route dir begin-stop-id))
    (set (fetch-vehicles-before-stop route dir end-stop-id)))))


(defn extract-vehicles [predictions]
  (map :vid predictions))

(defn get-candidate-buses
     "Returns a seq of vehicle data structs for buses passengers on a given route may be on"
     [route direction begin-stop-id end-stop-id]
     (concat
      (extract-vehicles (fetch-prediction-data route begin-stop-id))
      (fetch-vehicles-between-stops route direction begin-stop-id end-stop-id)))
