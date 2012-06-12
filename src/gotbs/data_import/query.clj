(ns gotbs.data-import.query
  (:use [datomic.api :only [db q] :as d]))

(def uri "datomic:dev://localhost:4334/2012-JUN")

(def conn (d/connect uri))


(def results (q '[:find ?e :where [?e :snapshot/vehicle-id]] (db conn)))

(def vid (:snapshot/vehicle-id (d/entity (db conn) (ffirst results))))

(def snapshots (q '[:find ?e :in $ ?vid :where [?e :snapshot/vehicle-id ?vid]] (db conn) vid))

(def sorted-snapshots
  (sort-by :snapshot/update-time
           (map #(d/entity (db conn) (first  %)) snapshots)))


(def distances (map :snapshot/travelled-distance sorted-snapshots))

(defn noop [coll]
  (reduce
   (fn [a b] (conj a b))
   []
   coll))

(defn mps [coll]
  (reduce
   (fn [a b]
     (conj a
           {:id (:db/id b)
            :pdist (:snapshot/travelled-distance b)
            :dest (:snapshot/destination b)
            :newrun (or (nil? (last a))
                        (not (= (:snapshot/destination b) (:dest (last a)))))
            }))
   []
   coll))

(def r (mps sorted-snapshots))

(def lastid (atom (d/tempid :db.part/user)))

(defn infinite-ids []
  (lazy-seq
   (cons (d/tempid :db.part/user) (infinite-ids))))

(def entities
  (let [ids (infinite-ids)]
    (map (fn[x] {:db/id x}) ids)))

(def txns
  (reduce
   (fn [a b]
     (conj a
           (let [runid
                 (if (:newrun b)
                   (swap! lastid (fn [_] (d/tempid :db.part/user)))
                   @lastid)]
             {:db/id (:id b)
              :snapshot/run runid })))
   []
   r))

;;@(d/transact conn txns)