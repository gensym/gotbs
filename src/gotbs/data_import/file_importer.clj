(ns gotbs.data-import.file-importer
  (:use [datomic.api :only [db q tempid] :as d]
        [clojure.set :only [join]]
        [gotbs.util.relational :only [extend-rel
                                      project-rel
                                      add-seq
                                      rename-rel]])
  (:require [gotbs.cta.bustracker-data-parser :as parser])
  (:import java.io.File java.text.SimpleDateFormat))

(def filename "/Users/daltenburg/dev/busdata/dataslice/vehicles-120308024301.xml")

(defn fname-timestamp [fname]
  (let [tstring
        (-> fname
            java.io.File.
            .getName
            (.substring 9 21))]
    (.parse (SimpleDateFormat. "yyMMddHHmmSS") tstring)))

(defn parse-timestamp [snapshot]
  (.parse  (SimpleDateFormat. "yyyyMMdd HH:mm") (:tmstmp snapshot)))

(defn to-reponses [xrel]
  (concat 
   (->>
    (into #{}
          (project-rel [:filename :snapshot/response] xrel))
    (rename-rel :snapshot/response :db/id))
   xrel))

(defn transactions [filename]
  (->> filename
       (slurp)
       (parser/as-vehicle-data)
       (join
        [{:file-timestamp (fname-timestamp filename)}])
       (join [{:filename filename}])
       (join [{:snapshot/response (tempid :db.paFrt/user)}])
       (extend-rel :snapshot/update-time parse-timestamp)
       (extend-rel :snapshot/latitude #(Double/parseDouble (:lat %)))
       (extend-rel :snapshot/longitude #(Double/parseDouble (:lon %)))
       (extend-rel :snapshot/heading #(Float/parseFloat (:hdg %)))
       (extend-rel :snapshot/pattern-id :pid)
       (extend-rel :snapshot/travelled-distance #(Float/parseFloat (:pdist %)))
       (extend-rel :snapshot/route-id :rt)
       (extend-rel :snapshot/destination :des)
       (extend-rel :snapshot/vehicle-id :vid)))

(defn snapshot-files [dirname]
  (filter
   #(.startsWith (.getName %) "vehicles")
   (.listFiles
    (File. dirname))))


(defn do-it [db-uri dirname]
  (let [conn (d/connect db-uri)]
    (doseq [file (snapshot-files dirname)]
      (try
        @(d/transact conn (transactions filename))))))