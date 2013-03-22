(ns gotbs.web.runs
  (:require [clojure.data.json :as json])
  (:require [gotbs.run-data :as run])
  (:use net.cgrand.enlive-html))

(defn for-route [req]
  (json/json-str (run/for-route)))

(defsnippet runs-body "web/runs.html"
  [:div#runs]
  [])

(defsnippet runs-js "web/runs.html"
  [:div#page-specific-scripts]
  [])

(deftemplate runstemplate "web/index.html"
  [req]
  [:div#main] (substitute (runs-body))
  [:div#page-specific-scripts (substitute (runs-js))])



(defn runs [req]
  (runstemplate req))
