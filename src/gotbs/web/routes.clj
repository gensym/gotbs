(ns gotbs.web.routes
  (:require [clojure.contrib.json :as json])
  (:require [gotbs.bustracker :as bustracker])
  (:require [gotbs.route-data :as routes])
  (:require [clojure.contrib.str-utils2 :as s])
  (:use net.cgrand.enlive-html))

(defn stops []
  (bustracker/fetch-stop-data 56 bustracker/north-bound))

(defn contains-ignore-case? [str substring]
  (s/contains? (s/upper-case str) (s/upper-case substring)))

(defn matching-routes [query]
  (filter
   (fn [route] (contains-ignore-case? route query))
   (routes/display-names)
   ))

(defn available-routes [{term "term"}]
  (json/json-str (matching-routes term)))

(defn route-directions [{term "term"}]
  (json/json-str (routes/route-direction term)))

(defn pattern-points []
  (bustracker/fetch-pattern-data-for-route 56 bustracker/north-bound))

(defn route-waypoints [{route "route", direction "direction"}]
  (json/json-str routes/route-pattern-points route direction))

(defsnippet route-display-div "web/routes.html"
  [:div#routes] [])

(defsnippet route-selection-form "web/routes.html"
  [:form#route-selection] [])

(deftemplate routestemplate "web/index.html"
  [req]
  [:div#main] (substitute (route-display-div) (route-selection-form)))

(defn routes [req]
  (routestemplate req))

    
