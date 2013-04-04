(ns gotbs.history.run-records
  (require [datomic.api :as d]
           [clojure.tools.logging :as log]))

(comment (def uri "datomic:free://localhost:4334/gotbs"))
(comment (def conn (d/connect uri)))

(comment (def mdb (d/db conn)))

(defn run-with-points [run-entity]
  {:vehicle-id (-> run-entity :run/vehicle :vehicle/cta_id)
   :route (-> run-entity :run/route :route/cta_id)
   :destination (-> run-entity :run/destination :destination/name)
   :points  (map (fn [point]
                   (let [[time dist lat lon]
                         ((juxt :runpoint/time
                                :runpoint/travelled-distance
                                :runpoint/latitude
                                :runpoint/longitude)
                          point)]
                     {:time time
                      :distance dist
                      :latitude lat
                      :longitude lon}))
                 (:runpoint/_run run-entity))})

(defn route-runs [db cta-route-id start-time end-time]
  (try
    (->>
     (d/q '[:find ?run
            :in $ ?cta-route-id ?start-time ?end-time
            :where
            [(> ?time ?start-time)]
            [(< ?time ?end-time)]
            [?route :route/cta_id ?cta-route-id]
            [?run :run/route ?route]
            [?point :runpoint/run ?run]
            [?point :runpoint/time ?time]]
          db cta-route-id start-time end-time)
     (map first)
     (map (partial d/entity db))
     (map run-with-points))
    (catch Exception e
      (log/error e "Failed at getting route runs")
      [])))

;; TODO - this should come from the route and destination, rather than
;; the runset
(defn pattern-distance [runset]
  (apply max (cons 0 (map :distance (mapcat :points runset)))))

;; TODO - need a way to get the total distance for a route.
;; we should also take the route direction
;; next up - look at the patterns an

(comment
  (defn rruns [db]
    (let [db (d/db conn)
          cta-route-id "56"
          start-time #inst "2012-03-02T06:00:00.000-00:00"
          end-time #inst "2012-03-08T11:00:00.000-00:00"]
      (route-runs db cta-route-id start-time end-time))))
