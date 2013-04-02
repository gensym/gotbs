(ns gotbs.bootstrap.webapp
  (:use [gotbs.websockets.jetty :only (make-jetty-server)]
         [gotbs.route-data :only (route-descriptor)])
  (:require gotbs.core
            [clojure.tools.logging :as log]
            [gotbs.feed.subscriptions :as feed]
            [gotbs.web.route-subscriptions :as subscriber]
            [gotbs.websockets.webbit :as webbit]
            [gotbs.busdata :as busdata]
            [clojure.data.json :as json]
            [gotbs.util.scheduler :as scheduler]))

(comment (def datomic-uri "datomic:free://localhost:4334/gotbs"))
(comment (def conn (datomic.api/connect datomic-uri)))
(comment (def stop (start-all conn)))
(comment (stop))

(defn- start-jetty [handler port]
  (let [server (make-jetty-server handler port)]
    (.start server)
    (fn [] (.stop server))))

(defn- send-ws [connection topic message]
  (let [jsonified  (json/json-str {:type "updated" :message message})]
    (log/info "Sending to topic [" topic "] message: " jsonified)
    (.send connection jsonified)))

(defn- start-feed-scheduler [feed-subscriptions]
  (let [s (scheduler/make-scheduler #(feed/schedule feed-subscriptions) 60000 60000 )]
    #(scheduler/shutdown s)))

(defn start-webbit [port route-subscriptions]
  (let [on-open (partial subscriber/add-subscriber route-subscriptions send-ws)
        on-close (partial subscriber/drop-subscriber route-subscriptions)
        on-subscribe (partial subscriber/subscribe route-subscriptions)
        webbit-server (webbit/make-webbit-websockets port on-open on-close on-subscribe)]
    (webbit/start webbit-server)
    (fn [] (webbit/stop webbit-server))))

(defn start-jetty-core-app [database-connection]
  (start-jetty (gotbs.core/app database-connection) 8080))

(defn- action-fn [[rtid direction]]
  (let  [vehicles (busdata/in-flight-vehicles [rtid])]
    (filter #(= direction (busdata/vehicle-direction %)) vehicles)))

(defn start-all [database-connection]
  "Return a function that, when invoked, shuts down"
  (log/info "Starting webapp")
  (let [subscriptions  (feed/make-subscriptions action-fn)
        location-subscriber (subscriber/make-subscriptions subscriptions)
        stoppables
        [(start-webbit 8888 location-subscriber)
         (start-jetty-core-app database-connection)
         (start-feed-scheduler subscriptions)
         #(subscriber/stop location-subscriber)]]
    (fn []
      (log/info "Stopping webapp")
      (dorun (map #(%) stoppables)))))
