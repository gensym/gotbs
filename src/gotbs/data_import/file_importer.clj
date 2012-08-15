(ns gotbs.data-import.file-importer
  (:use [datomic.api :only [db q tempid] :as d]
        [clojure.set :only [join]]
        [gotbs.util.relational :only [extend-rel
                                      project-rel
                                      add-seq
                                      rename-rel]])
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
        (project-rel [:snapshot/route-id] xrel)))

(defn vehicles [xrel db]
  (into #{}
        (project-rel [:snapshot/vehicle-id] xrel)))

(defn destinations [xrel]
  (into #{}
        (project-rel [:snapshot/destination] xrel)))


(defn find-by-attribute [db att v]
  (->>
   (q [:find '?e :in '$ '?val
        :where ['?e att '?val]] db (att v))
   (map (fn [[e]] (d/entity db e)))))

(defn find-route [db rt]
  (find-by-attribute db :route/cta_id rt))

(defn find-vehicle [db v]
  (find-by-attribute db :vehicle/cta_id v))

(defn find-destination [db dest]
  (find-by-attribute db :destination/name dest))


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
         (extend-rel :vehicle/cta_id :snapshot/vehicle-id))))

(defn transactions [file]
  (let [points (location-points file)
        responses (get-snapshot-reponses points)]
    (concat responses points)))

(defn snapshot-files [dirname]
  (filter
   #(.startsWith (.getName %) "vehicles")
   (.listFiles
    (File. dirname))))


(defn do-it [db-uri dirname]
  (let [conn (d/connect db-uri)]
    (doseq [file (snapshot-files dirname)]
      (try
        @(d/transact conn (transactions file))))))


(def filename "/Users/daltenburg/dev/busdata/dataslice/vehicles-120308024301.xml")

(def uri "datomic:free://localhost:4334/gotbs")

