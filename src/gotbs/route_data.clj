(ns gotbs.route-data
  (:require [gotbs.bustracker :as bustracker])
  (:require [clojure [string :as s]]))

(defn- str-contains? [s substring]
  (not (== -1 (.indexOf s substring))))

(defn- add-display-name [route]
  (assoc route :display (str (:rt route) " - " (:rtnm route))))

(defn- routes-with-display-names []
  (map
     add-display-name
     (bustracker/fetch-routes)))

(defn- contains-ignore-case? [str substring]
  (str-contains? (s/upper-case str) (s/upper-case substring)))

(defn route-descriptor [display-name]
  (dissoc
   (first (filter 
           #(= display-name (:display %))
           (routes-with-display-names)))
   :display))

(defn display-names []
  (map
   :display
   (routes-with-display-names)))

(defn matching-routes [query]
  (filter
   (fn [route] (contains-ignore-case? route query))
   (display-names)))

(defn waypoints [route-display-name direction]
  (map (fn [point] {
                   :lat (Double/parseDouble (:lat point))
                   :lon (Double/parseDouble (:lon point))})
       (bustracker/fetch-pattern-data-for-route (:rt (route-descriptor route-display-name)) direction)))

(defn route-direction [route-display-name]
  (bustracker/fetch-route-direction (:rt (route-descriptor route-display-name))))
