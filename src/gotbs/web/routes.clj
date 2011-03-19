(ns gotbs.web.routes
  (:require [clojure.contrib.json :as json])
  (:require [gotbs.bustracker :as bustracker])
  (:use net.cgrand.enlive-html))

(defn stops []
  (bustracker/fetch-stop-data 56 bustracker/north-bound))

(defn available-routes [{term "term"}]
  (json/json-str (cons term ["56 - Milwaukee" "123 - Test"])))

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

    
