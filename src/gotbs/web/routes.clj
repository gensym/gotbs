(ns gotbs.web.routes
  (:require [clojure.contrib.json :as json])
  (:require [gotbs.bustracker :as bustracker])
  (:use net.cgrand.enlive-html))

(defn stops []
  (bustracker/fetch-stop-data 56 bustracker/north-bound))


(defn pattern-points []
  (bustracker/fetch-pattern-data-for-route 56 bustracker/north-bound))

(defn waypoints []
  (json/json-str
   (map (fn [point] {:lat (Float/parseFloat (:lat point)) :lon (Float/parseFloat (:lon point))})
        (pattern-points))))

(defsnippet routes-content "web/routes.html"
  [:div#routes]
  [req]
  [:script#data] (content (str "var data = " (waypoints) ";")))

(deftemplate routestemplate "web/index.html"
  [req]
  [:div#main] (substitute (routes-content req)))

(defn routes [req]
  (routestemplate req))

    
