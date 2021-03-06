(ns gotbs.data-import.file-importer
  (:use [datomic.api :only [db q tempid] :as d]
        [clojure.set :only [join]]
        [gotbs.util.relational :only [extend-rel
                                      project-rel
                                      add-seq
                                      rename-rel
                                      extract-rel
                                      extend-with-id-of-normalized]])
  (:require [gotbs.cta.bustracker-data-parser :as parser]
            [gotbs.data-import.query :as query])
  (:import java.io.File java.text.SimpleDateFormat))

(defn fname-timestamp [fname]
  (let [tstring
        (-> fname
            java.io.File.
            .getName
            (.substring 9 21))]
    (.parse (SimpleDateFormat. "yyMMddHHmmSS") tstring)))

(defn parse-timestamp [snapshot]
  (.parse  (SimpleDateFormat. "yyyyMMdd HH:mm") (:tmstmp snapshot)))

(defn get-snapshot-reponses [xrel]
  (->>
   (into #{}
         (project-rel [:filename :snapshot/response] xrel))
   (rename-rel :snapshot/response :db/id)))

(defn routes [xrel]
  (into #{}
        (project-rel [:route/cta_id] xrel)))

(defn vehicles [xrel]
  (into #{}
        (project-rel [:vehicle/cta_id] xrel)))

(defn destinations [xrel]
  (into #{}
        (project-rel [:destination/name] xrel)))

(defn runpoints [xrel]
  (into #{}
        (map #(assoc % :meta/entity-type "runpoint")
                    (project-rel [:runpoint/run
                                  :runpoint/longitude
                                  :runpoint/latitude
                                  :runpoint/travelled-distance
                                  :runpoint/time]
                                 xrel))))

(defn find-run [db {destination-name :destination/name
                    destination-id :run/destination
                    route  :route/cta_id
                    route-id :run/route
                    vehicle :vehicle/cta_id
                    vehicle-id :run/vehicle
                    time :runpoint/time}]
  (if-let [existing (query/find-run db
                                    vehicle
                                    route
                                    destination-name
                                    time)]
    (let [id (:db/id existing)]
      (assoc
          (into {}
                (map (fn [[k v]] [k (:db/id v)]) existing))
        :db/id id
        :meta/entity-type "run"))
    {:run/route route-id
     :run/destination destination-id
     :run/vehicle vehicle-id
     :meta/entity-type "run"
     :db/id (d/tempid :db.part/user)
     }))

(defn find-by-attribute [db att v]
  (->>
   (q [:find '?e :in '$ '?val
        :where ['?e att '?val]] db (att v))
   (map (fn [[e]] (d/entity db e)))
   (first)))

(defn find-runpoint [db runpoint]
  (assoc runpoint :db/id (d/tempid :db.part/user)))

(defn produce-by-attribute [db att v]
  (if-let [existing (find-by-attribute db att v)]
    (into {:db/id (:db/id existing)} existing)
    (assoc (select-keys v [att]) :db/id (d/tempid :db.part/user))))

(defn find-route [db rt]
  (assoc
      (produce-by-attribute db :route/cta_id rt)
    :meta/entity-type "route"))

(defn find-vehicle [db v]
  (assoc
      (produce-by-attribute db :vehicle/cta_id v)
    :meta/entity-type "vehicle"))

(defn find-destination [db dest]
  (assoc
      (produce-by-attribute db :destination/name dest)
    :meta/entity-type "destination"))

(defn location-points [file]
  (let [filename (.getName file)]
    (->> file
         (slurp)
         (parser/as-vehicle-data)
         (join
          [{:file-timestamp (fname-timestamp filename)}])
         (join [{:filename filename}])
         (join [{:snapshot/response (tempid :db.part/user)}])
         (extend-rel :snapshot/update-time parse-timestamp)
         (extend-rel :snapshot/latitude #(Double/parseDouble (:lat %)))
         (extend-rel :snapshot/longitude #(Double/parseDouble (:lon %)))
         (extend-rel :snapshot/heading #(Float/parseFloat (:hdg %)))
         (extend-rel :snapshot/pattern-id :pid)
         (extend-rel :snapshot/travelled-distance #(Float/parseFloat (:pdist %)))
         (extend-rel :snapshot/route-id :rt)
         (extend-rel :snapshot/destination :des)
         (extend-rel :snapshot/vehicle-id :vid)
         (extend-rel :route/cta_id :snapshot/route-id)
         (extend-rel :destination/name :snapshot/destination)
         (extend-rel :vehicle/cta_id :snapshot/vehicle-id)
         (extend-rel :runpoint/longitude :snapshot/longitude)
        (extend-rel :runpoint/latitude :snapshot/latitude)
         (extend-rel :runpoint/travelled-distance (comp double :snapshot/travelled-distance))
         (extend-rel :runpoint/time :snapshot/update-time))))


(defn transactions [file db]
  (let [points (location-points file)
        routes (map (partial find-route db) (routes points))
        destinations (map (partial find-destination db) (destinations points))
        vehicles (map (partial find-vehicle db) (vehicles points))
        annotated-points (-> points
                             (extend-with-id-of-normalized :run/route routes :db/id)
                             (extend-with-id-of-normalized :run/destination destinations :db/id)
                             (extend-with-id-of-normalized :run/vehicle vehicles :db/id))
        runs (map #(find-run db %) annotated-points)
        runpoints
        (map (partial find-runpoint db)
             (-> annotated-points
                 (extend-with-id-of-normalized :runpoint/run runs :db/id)
                 runpoints))]
    (mapcat seq
            (list routes
                  vehicles
                  destinations
                  runs
                  runpoints))))
