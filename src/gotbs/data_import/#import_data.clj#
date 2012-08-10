(ns gotbs.data-import.import-data
  (:use [datomic.api :only [db q] :as d])
  (:require [gotbs.cta.bustracker-data-parser :as parser])
  (:import java.io.File java.text.SimpleDateFormat))

(defn to-vehicle-entity [snapshot-id veh]
  {:db/id (d/tempid :db.part/user),
   :snapshot/response snapshot-id,
   :snapshot/vehicle-id (:vid veh),
   :snapshot/update-time (.parse  (SimpleDateFormat. "yyyyMMdd HH:mm") (:tmstmp veh)),
   :snapshot/latitude (Double/parseDouble (:lat veh)),
   :snapshot/longitude (Double/parseDouble (:lon veh)),
   :snapshot/heading (Float/parseFloat (:hdg veh)),
   :snapshot/pattern-id (:pid veh),
   :snapshot/travelled-distance  (Float/parseFloat (:pdist veh)),
   :snapshot/route-id (:rt veh),
   :snapshot/destination (:des veh),
   })

(defn- snapshot-files [dirname]
  (filter
   #(.startsWith (.getName %) "vehicles")
   (.listFiles
    (File. dirname))))

(defn- fname-timestamp [fname]
  (.parse  (SimpleDateFormat. "yyMMddHHmmSS") (.substring fname 9 21)))

(defn- parse-snapshot-file [snapshot-file snapshot-id]
  (map
   (partial to-vehicle-entity snapshot-id)
   (parser/as-vehicle-data (slurp snapshot-file))))

(defn- to-transactions [snapshot-file]
  (let [retrieval-time (fname-timestamp  (.getName snapshot-file))
        snapshot-id (d/tempid :db.part/user)]
    (cons
     {:db/id snapshot-id :document/retrieval-time retrieval-time}
     (filter (complement empty?)
             (parse-snapshot-file snapshot-file snapshot-id)))))

(defn import-data [db-uri data-path]
  (let [conn (d/connect db-uri)
        errors (atom [])]
    (doseq [f (snapshot-files data-path)]
      (try
        @(d/transact conn (to-transactions f))
        (println "Imported " f)
        (catch Exception ex
          (swap! errors #(conj % {:name (.getName f) :exception ex})))))
    @errors))

(defn -main [db-uri]
  (let [errors
        (import-data db-uri "/Users/daltenburg/dev/busdata/data")]
    (doseq [err errors]
      (println err))
    (System/exit 0)))