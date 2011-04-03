(ns gotbs.route-data
  (:require [gotbs.bustracker :as bustracker])
  (:use [clojure.contrib.seq-utils :only (find-first)]))

(defn- add-display-name [route]
  (assoc route :display (str (:rt route) " - " (:rtnm route))))

(defn- routes-with-display-names []
  (map
     add-display-name
     (bustracker/fetch-routes)))

(defn route-descriptor [display-name]
  (dissoc 
   (find-first
    #(= display-name (:display %))
    (routes-with-display-names))
   :display))

(defn display-names []
  (map
   :display
   (routes-with-display-names)))

(defn route-direction [route-display-name]
  (bustracker/fetch-route-direction (:rt (route-descriptor route-display-name))))


(defn route-pattern-points [route-display-name direction]
  (vec
   (map (fn [point] {:lat (Double/parseDouble (:lat point)) :lon (Double/parseDouble (:lon point))})
        (bustracker/fetch-pattern-data-for-route (:rt (route-descriptor route-display-name)) direction))))
