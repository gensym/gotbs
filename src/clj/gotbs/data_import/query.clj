(ns gotbs.data-import.query
  (:use [datomic.api :only [db q] :as d]))

(comment (def uri-full "datomic:free://localhost:4334/gotbs"))
(comment (def uri  "datomic:free://localhost:4334/gotbs-test"))

(comment (def conn (d/connect uri)))
(comment (def conn-full (d/connect uri-full)))

(comment (def vid "1159"))

(comment (def t #inst "2012-03-08T02:43:00.000-00:00"))
(comment (def t2 #inst "2012-03-08T09:43:00.000-00:00"))
(comment (def r (find-run conn "1874" "73")))
(comment (def point (last-runpoint conn "1874" t)))
(comment (def run (find-run conn "1874" "73" "Clark/North" t)))

(comment (def run (find-run conn-full "1874" "73" "Clark/North" #inst "2012-04-08T09:43:00.000-00:00")))

(defn prior-run [db vehicle_id time]
  (->>
   (d/q '[:find ?run
          :in $ ?vehicle_id ?t
          :where
          [(< ?time ?t)]
          [?vehicle :vehicle/cta_id ?vehicle_id]
          [?run :run/vehicle ?vehicle]
          [?point-c :runpoint/run ?run]
          [?point-c :runpoint/time ?time]
          [(max ?time) ?mtime]
          [?point :runpoint/time ?mtime]
          [?point :runpoint/run ?run]]
        db vehicle_id time)
   (ffirst)
   (d/entity db)))

(defn next-run [db vehicle_id time]
  (->>
   (d/q '[:find ?run
          :in $ ?vehicle_id ?t
          :where
          [(> ?time ?t)]
          [?vehicle :vehicle/cta_id ?vehicle_id]
          [?run :run/vehicle ?vehicle]
          [?point-c :runpoint/run ?run]
          [?point-c :runpoint/time ?time]
          [(min ?time) ?mtime]
          [?point :runpoint/time ?mtime]
          [?point :runpoint/run ?run]]
        db vehicle_id time)
   (ffirst)
   (d/entity db)))

(defn match-run-or-nil [candidate vehicle_id route_id destination]
  (if (and (= vehicle_id (:vehicle/cta_id (:run/vehicle candidate)))
           (= route_id (:route/cta_id (:run/route candidate)))
           (= destination (:destination/name (:run/destination candidate))))
    candidate))

(defn find-run [db vehicle_id route_id  destination t]
  (let [before (prior-run db vehicle_id t)]
    (if-let [a (match-run-or-nil before vehicle_id route_id destination)]
      a
      (match-run-or-nil  (next-run db vehicle_id t) vehicle_id route_id destination))))

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
