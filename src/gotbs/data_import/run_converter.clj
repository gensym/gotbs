(ns gotbs.data-import.run-converter
  (:use [datomic.api :only [db q] :as d]
        [gotbs.util.collections :only [flex-partition]]))

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
     (assoc (to-hash a) :newrun
            (or
             (not (= (:snapshot/destination a) (:snapshot/destination b)))
             (not (= (:snapshot/vehicle-id a) (:snapshot/vehicle-id b)))
             (not (= (:snapshot/route-id a) (:snapshot/route-id b))))))
   (partition 2 
              (interleave
               sorted-vehicle-snapshots
               (cons {} sorted-vehicle-snapshots)))))


(defn grouped-by-run [sorted-vehicle-snapshots]
  (let [flagged (with-newrun-flag sorted-vehicle-snapshots)]
    (flex-partition :newrun flagged)))

(defn to-runpoint-txn [runid runpoint]
  {:db/id (d/tempid :db.part/user)
   :runpoint/run runid
   :runpoint/longitude (:snapshot/longitude runpoint)
   :runpoint/latitude (:snapshot/latitude runpoint)
   :runpoint/travelled-distance (:snapshot/travelled-distance runpoint)
   :runpoint/time (:snapshot/update-time runpoint)})

(defn to-run-transactions [run-group]
  (let [runid (d/tempid :db.part/user)
        routeid (d/tempid :db.part/user)
        vehicleid (d/tempid :db.part/user)]
    (concat
     [ {:db/id runid
        :run/route routeid
        :run/vehicle vehicleid}
       {:db/id routeid
        :route/name (:snapshot/route-id (first run-group))}
       {:db/id vehicleid
        :vehicle/cta_id (:snapshot/vehicle-id (first run-group))}
      ]
     (map (partial to-runpoint-txn runid) run-group))))
