(ns gotbs.bootstrap.webapp
  (:use [gotbs.websockets.jetty :only (make-jetty-server)]
        [gotbs.web.route-subscriber :only (location-subscriber)])
  (:require gotbs.core
            [gotbs.websockets.webbit :as webbit]
            [clojure.data.json :as json]
            [gotbs.util.scheduler :as scheduler]
            [gotbs.busdata :as busdata]
            [gotbs.util :as util]
            [gotbs.util.pull-queue :as pull-queue]
            [gotbs.websockets.connections :as ws-connections]
            [gotbs.websockets.connection-subscriber :as ws-subscriber]))

(def connection-set (ws-connections/connection-set))

(defn publisher  [[route vehicles]]
  (ws-connections/broadcast connection-set route (json/json-str vehicles)))

(defn- start-jetty [handler port]
  (let [server (make-jetty-server handler port)]
    (.start server)
    (fn [] (.stop server))))

(defn- start-webbit [port connection-subscriber]
  (let [webbit-server (webbit/make-webbit-websockets port connection-subscriber)]
    (webbit/start webbit-server)
    (fn [] (webbit/stop webbit-server))))

;; todo - fill out location subscriber
(defn start-webbit-connection-subscriber []
  (start-webbit
   8888
   (ws-subscriber/connection-subscriber connection-set location-subscriber)))

(defn start-jetty-core-app []
  (start-jetty #'gotbs.core/app 8080))

(defn start-all []
  "Return a function that, when invoked, shuts down"
  (let [stoppables
        [(start-webbit-connection-subscriber)
         (start-jetty-core-app)]]
    (fn [] (dorun (map #(%) stoppables)))))
