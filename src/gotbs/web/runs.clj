(ns gotbs.web.runs
  (:require [clojure.data.json :as json])
  (:require [gotbs.run-data :as run])
  (:use net.cgrand.enlive-html)
  (:import org.joda.time.DateTime)
  (:import org.joda.time.format.DateTimeFormat)
  (:import java.text.SimpleDateFormat))


(def ds "2013-03-27T00:29:41.568Z")
(def formatter (DateTimeFormat/forPattern "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))

(defn- to-date [ds]
  (.toDate (.parseLocalDateTime formatter ds)))

(defn- to-string [d]
  (.toString (DateTime. d) formatter))

(defn- format-dates [run-message]
  (assoc run-message
    :runs
    (map (fn [run]
           (map (fn [rp]
                  (assoc rp :time (to-string (:time rp))))
                run))
         (:runs run-message))
    :start-time (to-string (:start-time run-message))
    :end-time (to-string (:end-time run-message))))

(defn for-route [{start-time "from"
                  end-time "to"}]
  (let [start (to-date start-time)
        end (to-date end-time)]
    (json/json-str (format-dates (run/for-route start end)))))

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
