(ns gotbs.web.runs
  (:require [clojure.tools.logging :as log]
            [gotbs.run-data :as run])
  (:use net.cgrand.enlive-html)
  (:import org.joda.time.DateTime
           org.joda.time.format.DateTimeFormat
            java.text.SimpleDateFormat))


(def formatter (DateTimeFormat/forPattern "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))


(comment (def datomic-uri "datomic:free://localhost:4334/gotbs"))
(comment (def conn (datomic.api/connect datomic-uri)))

(comment (def from "2012-04-02T11:00:00.000Z"))
(comment (def to "2012-04-08T16:00:00.000Z"))
(comment (def mdb (datomic.api/db gotbs.bootstrap.webapp/conn)))
(comment (def mdb (datomic.api/db conn)))
(comment (for-route mdb {"from" from "to" to}))

(defn- to-date [ds]
  (.toDate (.parseLocalDateTime formatter ds)))


(defn for-route [db
                 {from "from"
                  to "to"}]
    (let [start (to-date from)
          end (to-date to)]
      (do
        (log/info "Sending runs for route from " from "to" to)
         (run/for-route db start end))))

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
