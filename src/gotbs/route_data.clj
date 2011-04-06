(ns gotbs.route-data
  (:require [gotbs.bustracker :as bustracker])
  (:require [clojure.contrib.str-utils2 :as s])
  (:use [clojure.contrib.seq-utils :only (find-first)]))

(defn- add-display-name [route]
  (assoc route :display (str (:rt route) " - " (:rtnm route))))

(defn- routes-with-display-names []
  (map
     add-display-name
     (bustracker/fetch-routes)))

(defn- contains-ignore-case? [str substring]
  (s/contains? (s/upper-case str) (s/upper-case substring)))

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

(defn matching-routes [query]
  (filter
   (fn [route] (contains-ignore-case? route query))
   (display-names)
   ))

(defn route-direction [route-display-name]
  (bustracker/fetch-route-direction (:rt (route-descriptor route-display-name))))
