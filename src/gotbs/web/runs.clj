(ns gotbs.web.runs
  (:use net.cgrand.enlive-html))

(deftemplate runstemplate "web/runs.html"
  [req])

(defn runs [req]
  (runstemplate req))
