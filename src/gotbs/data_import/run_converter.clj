(ns gotbs.data-import.run-converter
  (:use [datomic.api :only [db q] :as d]))

(def uri "datomic:dev://localhost:4334/2012-JUN")

(def conn  (d/connect uri))
(def my-db (db conn))

(defn- to-entity [db]
  (fn [query-result]
    (d/entity db (first query-result))))

(defn all-snapshots [db]
  (map (to-entity db)
       (q '[:find ?e :where [?e :snapshot/vehicle-id]] db)))

(defn vehicle-snapshots [db vid]
  (map (to-entity db)
       (q '[:find ?e :in $ ?vid :where [?e :snapshot/vehicle-id ?vid]] db vid)))

(defn to-hash [entity]
  (reduce (fn[m v] (assoc m v (v entity))) {} (keys entity)))

(defn with-newrun-flag [sorted-vehicle-snapshots]
  (map
   (fn [[a b]]
     (assoc (to-hash a) :newrun (not (= (:snapshot/destination a) (:snapshot/destination b)))))
   (partition 2 
              (interleave
               sorted-vehicle-snapshots
               (cons {} sorted-vehicle-snapshots)))))
