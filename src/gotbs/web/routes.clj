(ns gotbs.web.routes
  (:require [clojure.contrib.json :as json])
  (:require [gotbs.bustracker :as bustracker])
  (:require [gotbs.route-data :as routes])
  (:use net.cgrand.enlive-html))

(defn route-waypoints [{route "route" direction "direction"}]
  (json/json-str (routes/waypoints route direction)))

(defn available-routes [{term "term"}]
  (json/json-str (routes/matching-routes term)))

(defn route-directions [{term "term"}]
  (json/json-str (routes/route-direction term)))

(defsnippet routes-js "web/routes.html"
  [:div#page-specific-scripts]
  [])

(defsnippet routes-body "web/routes.html"
  [:div#routes]
  [])


(deftemplate routestemplate "web/index.html"
  [req]
  [:div#main] (substitute (routes-body))
  [:div#page-specific-scripts] (substitute (routes-js)))

(defn routes [req]
  (routestemplate req))

    
