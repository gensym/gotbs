(ns gotbs.data-import.query
  (:use [datomic.api :only [db q] :as d]))

(def uri "datomic:free://localhost:4334/gotbs")

(comment (def conn (d/connect uri)))

(comment (def conn (d/connect "datomic:free://localhost:4334/gotbs-test")))

(defn known-entity-types [conn]
  (->>
   (q '[:find ?e :in $ :where [?e :meta/entity-type]] (db conn))
   (map first)
   (map (partial d/entity (db conn)))
   (map :meta/entity-type)
   (into #{})))

(defn to-map [db entity]
  (let [e (d/entity db entity)]
    (reduce (fn [m v] (assoc m v (v e))) {} (keys e))))

(defn entities-of-type [conn entity-type]
  (let [d (db conn)]
    (map (partial d/entity d) 
         (map first
              (q '[:find ?e :in $ ?t :where [?e :meta/entity-type ?t]] d entity-type)))))

(defn runpoints [conn]
  (entities-of-type conn "runpoint"))

(defn runs [conn]
  (entities-of-type conn "run"))

(defn routes [conn]
  (entities-of-type conn "route"))

(defn vehicles [conn]
  (entities-of-type conn "vehicle"))

(defn destinations [conn]
  (entities-of-type conn "destination"))
