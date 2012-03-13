(ns gotbs.bootstrap.webapp
  (:use [gotbs.websockets.jetty :only (make-jetty-server)])
  (:require gotbs.core
            [gotbs.web.route-subscriptions :as subscriber]
            [gotbs.websockets.webbit :as webbit]
            [clojure.data.json :as json]
            [gotbs.util.scheduler :as scheduler]))

(defn- start-jetty [handler port]
  (let [server (make-jetty-server handler port)]
    (.start server)
    (fn [] (.stop server))))

(defn- send-ws [connection topic message]
  (.send connection (json/json-str message)))

(defn start-webbit [port route-subscriptions]
  (let [on-open (partial subscriber/add-subscriber route-subscriptions send-ws)
        on-close (partial subscriber/drop-subscriber route-subscriptions)
        on-subscribe (partial subscriber/subscribe route-subscriptions)
        webbit-server (webbit/make-webbit-websockets port on-open on-close on-subscribe)]
    (webbit/start webbit-server)
    (fn [] (webbit/stop webbit-server))))

(defn start-jetty-core-app []
  (start-jetty #'gotbs.core/app 8080))

(defn start-all []
  "Return a function that, when invoked, shuts down"
  (let [location-subscriber (subscriber/make-subscriptions)
        stoppables
        [(start-webbit 8888 location-subscriber)
         (start-jetty-core-app)
         #(subscriber/stop location-subscriber)]]
    (fn [] (dorun (map #(%) stoppables)))))
