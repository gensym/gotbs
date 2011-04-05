(ns gotbs.web.routes
  (:require [clojure.contrib.json :as json])
  (:require [gotbs.bustracker :as bustracker])
  (:require [gotbs.route-data :as routes])
  (:require [clojure.contrib.str-utils2 :as s])
  (:use net.cgrand.enlive-html))

(defn- contains-ignore-case? [str substring]
  (s/contains? (s/upper-case str) (s/upper-case substring)))

(defn- matching-routes [query]
  (filter
   (fn [route] (contains-ignore-case? route query))
   (routes/display-names)
   ))

(defn available-routes [{term "term"}]
  (json/json-str (matching-routes term)))

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

    
