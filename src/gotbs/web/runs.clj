(ns gotbs.web.runs
  (:require [clojure.data.json :as json])
  (:require [gotbs.run-data :as run])
  (:use net.cgrand.enlive-html)
  (:import java.text.SimpleDateFormat))


(defn- format-dates [run-message]
  (let [formatter (SimpleDateFormat. "yyMMddHHmmSS")]
    (assoc run-message
      :runs
      (map (fn [run]
             (map (fn [rp]
                    (assoc rp :time (.format formatter (:time rp))))
                  run))
           (:runs run-message))
      :start-time (.format formatter (:start-time run-message))
      :end-time (.format formatter (:end-time run-message)))))

(defn for-route [{start-time "from"
                  end-time "to"}]
  (let [formatter (SimpleDateFormat. "yyMMddHHmmSS")
        start (.parse formatter start-time)
        end (.parse formatter end-time)])
  (json/json-str (format-dates (run/for-route start end))))

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
