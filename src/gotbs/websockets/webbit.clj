(ns gotbs.websockets.webbit
  (:require [clojure.data.json :as json]
            [clojure.string :as s]
            [gotbs.websockets.connection-subscriber :as ws-conn])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]))

(defn- on-message [connection-set connection json-message]
  (let [topic (-> json-message json/read-json (get-in [:data :topic]))]
    (ws-conn/subscribe connection-set connection topic)))

(defn make-webbit-websockets [port connection-set]
  (doto (WebServers/createWebServer port)
    (.add "/topics"
          (proxy [WebSocketHandler] []
            (onOpen [c]  (ws-conn/open connection-set c))
            (onClose [c] (ws-conn/close connection-set c))
            (onMessage [c j] (on-message connection-set c j))))))

(defn start [webbit]
  (.start webbit))

(defn stop [webbit]
  (.stop webbit))
