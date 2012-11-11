(ns gotbs.data-import.file-importer
  (:use [datomic.api :only [db q tempid] :as d]
        [clojure.set :only [join]]
        [gotbs.util.relational :only [extend-rel
                                      project-rel
                                      add-seq
                                      rename-rel
                                      extract-rel
                                      extend-with-id-of-normalized]])
    (:require [gotbs.cta.bustracker-data-parser :as parser])
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

(defn find-by-attribute [db att v]
  (->>
   (q [:find '?e :in '$ '?val
        :where ['?e att '?val]] db (att v))
   (map (fn [[e]] (d/entity db e)))
   (first)))

(defn find-run [db runpoint]
  
  "TODO: this method is nonfunctional
runpoint should have :run/route, :run/destination, and :run/vehicle populated"
  (q '[:find ?e
       :in $ ?rt ?dest ?veh
       :where
       [?e :run/route ?rt]
       [?e :run/destination ?dest]
       [?e :run/vehicle ?veh]]
     db,
     (:run/route runpoint),
     (:run/destination runpoint),
     (:run/vehicle runpoint)))

(defn produce-by-attribute [db att v]
  (if-let [existing (find-by-attribute db att v)]
    existing
    (assoc (select-keys v [att]) :db/id (d/tempid :db.part/user))))

(defn find-route [db rt]
  (produce-by-attribute db :route/cta_id rt))

(defn find-vehicle [db v]
  (produce-by-attribute db :vehicle/cta_id v))

(defn find-destination [db dest]
  (produce-by-attribute db :destination/name dest))


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
         (extend-rel :runpoint/travelled-distance :snapshot/travelled-distance)
         (extend-rel :runpoint/time :snapshot/update-time))))

(defn transactions [file db]
  (let [points (location-points file)
        routes (map (partial find-route db) (routes points))
        destinations (map (partial find-destination db) (destinations points))
        vehicles (map (partial find-vehicle db) (vehicles points))
        runpoints (-> points
                      (extend-with-id-of-normalized :run/route routes :db/id)
                      (extend-with-id-of-normalized :run/destination destinations :db/id)
                      (extend-with-id-of-normalized :run/vehicle vehicles :db/id))]
    (concat routes
            destinations
            runpoints)))

(defn snapshot-files [dirname]
  (filter
   #(.startsWith (.getName %) "vehicles")
   (.listFiles
    (File. dirname))))

(defn do-it [db-uri dirname]
  (let [conn (d/connect db-uri)]
    (doseq [file (snapshot-files dirname)]
      (try
        @(d/transact conn (transactions file (db conn)))))))


(def filename "/Users/daltenburg/dev/busdata/dataslice/vehicles-120308024301.xml")

(def uri "datomic:free://localhost:4334/gotbs")

(defn reset-db! [uri]
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)
        import-schema (read-string  (slurp "resources/schema/location-schema.dtm"))
        run-schema (read-string (slurp "resources/schema/run-schema.dtm"))]
    @(d/transact conn import-schema)
    @(d/transact conn run-schema)
    (db conn)))
;; WHERE TO NEXT?

;; Another function will take an xrel (the origial one) and a
;; <NORMALIZED RELATION> and extend that xrel to include references to
;; that NORMALIZED RELATION

;; (def mdb (reset-db! uri))

;; (pprint (take 100  (transactions file mdb)))


(def file (File. filename))
(def ld (location-points file))

