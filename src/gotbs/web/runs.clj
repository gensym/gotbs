(ns gotbs.web.runs
  (:use net.cgrand.enlive-html))

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
