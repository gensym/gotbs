(ns gotbs.busdata
  (:use gotbs.bustracker))

;; TODO - test this. It probably does not work
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
     (= (:des veh) (destination route dir)))
   (fetch-vehicles-on-route-data route)))


(defn vehicle-direction [vehicle]
  (fetch-pattern-data-for-route
   (:pid vehicle)))


;; TODO - fetch-vehicles-on-route-data needs to take a direction so we
;; don't get vehicles moving in the wrong direction

(let
    [compare-stop
     (fn [comparer route dir stop-id]
       (filter
	#(comparer
	  (Float/parseFloat (:pdist %))
	  (stop-pdist route dir stop-id))
	(fetch-vehicles-on-route-data route)))]
  
  (defn fetch-vehicles-past-stop [route dir stop-id]
    (compare-stop > route dir stop-id))

  (defn fetch-vehicles-before-stop [route dir stop-id]
    (compare-stop < route dir stop-id)))

(defn fetch-vehicles-between-stops [route dir begin-stop-id end-stop-id]
  (let [start-dist (stop-pdist route dir begin-stop-id)
        end-dist (stop-pdist route dir end-stop-id)]
    (filter (fn [x]
              (let [dist (Float/parseFloat (:pdist x))]
                (and
                 (< dist end-dist)
                 (> dist start-dist))))
            (fetch-vehicles-on-route-data route))))
  
(defn extract-vehicles [predictions]
  (map :vid predictions))

(defn get-candidate-buses
     "Returns a seq of vehicle data structs for buses passengers on a given route may be on"
     [route direction begin-stop-id end-stop-id]
     (concat
      (extract-vehicles (fetch-prediction-data route begin-stop-id))
      (fetch-vehicles-between-stops route direction begin-stop-id end-stop-id)))
