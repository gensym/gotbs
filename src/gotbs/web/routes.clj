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

(defn pattern-points []
  (bustracker/fetch-pattern-data-for-route 56 bustracker/north-bound))

(defn waypoints []
  (json/json-str
   (map (fn [point] {:lat (Float/parseFloat (:lat point)) :lon (Float/parseFloat (:lon point))})
        (pattern-points))))

(defsnippet routes-js-data "web/routes.html"
  [:div#routes]
  [req]
  [:script#data] (content (str "var data = " (waypoints) ";")))

(defsnippet route-selection-form "web/routes.html"
  [:form#route-selection]
  [req])


(deftemplate routestemplate "web/index.html"
  [req]
  [:div#main] (substitute (routes-js-data req) (route-selection-form req)))

(defn routes [req]
  (routestemplate req))

    
