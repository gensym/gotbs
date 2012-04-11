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

(defn start-jetty-core-app []
  (start-jetty #'gotbs.core/app 8080))

(defn- action-fn [[rtid direction]]
  (let  [vehicles (busdata/in-flight-vehicles [rtid])]
    (filter #(= direction (busdata/vehicle-direction %)) vehicles)))

(defn start-all []
  "Return a function that, when invoked, shuts down"
  (let [subscriptions  (feed/make-subscriptions action-fn)
        location-subscriber (subscriber/make-subscriptions subscriptions)
        stoppables
        [(start-webbit 8888 location-subscriber)
         (start-jetty-core-app)
         (start-feed-scheduler subscriptions)
         #(subscriber/stop location-subscriber)]]
    (fn [] (dorun (map #(%) stoppables)))))
