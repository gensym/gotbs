(ns gotbs.data-import.run-converter
  (:use [datomic.api :only [db q] :as d]
        [gotbs.util.collections :only [flex-partition]]))

(defn all-vids [db]
  (->> db
       (q '[:find ?vid :where [_ :snapshot/vehicle-id ?vid]])
       (map (fn [[v]] v))))

(defn vehicle-snapshots [db vid]
  "Return a list of sorted vehicle snapshots"
  (->> (q '[:find ?e :in $ ?vid :where [?e :snapshot/vehicle-id ?vid]] db vid)
       (map (fn [[e]] (d/entity db e)))
       (sort-by :snapshot/update-time)))

(defn to-run-groups [sorted-vehicle-snapshots]
  (->> (interleave sorted-vehicle-snapshots (cons {} sorted-vehicle-snapshots))
       (partition 2)
       (map   (fn [[a b]]
                (assoc (into {} a) :newrun
                       (or
                        (not (= (:snapshot/destination a) (:snapshot/destination b)))
                        (not (= (:snapshot/vehicle-id a) (:snapshot/vehicle-id b)))
                        (not (= (:snapshot/route-id a) (:snapshot/route-id b)))))) )
       (flex-partition :newrun)))

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

(defn some-txns [db vids]
  (->> vids
       (map (partial  vehicle-snapshots db))
       (map to-run-groups)
       (map (fn [groups]
              (->> groups
                   (map to-run-transactions)
                   (reduce concat))))
       (reduce concat))
  
  )

(defn txns [db]
  (some-txns db (all-vids db)))

(defn -main [uri]
  "datomic:dev://localhost:4334/gotbs"
  (let [conn  (d/connect uri)
        my-db (db conn)
        ts (txns my-db)]
    (dorun
     (try 
       @(d/transact conn ts)
       (println "Convert to runs")
       (catch Exception ex
         (.printStackTrace ex))))))