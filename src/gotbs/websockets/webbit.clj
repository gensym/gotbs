(ns gotbs.websockets.webbit
  (:require [clojure.data.json :as json])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]))

(defn make-webbit-websockets [port on-open on-close on-subscribe]
  "on-open and on-close take a connection. on-subscribe takes a connection and the topic"
  (doto (WebServers/createWebServer port)
    (.add "/topics"
          (proxy [WebSocketHandler] []
            (onOpen [c]  (on-open c))
            (onClose [c] (on-close c))
            (onMessage [c j]
                       (let [topic (-> j json/read-json (get-in [:data :topic]))]
                         (on-subscribe c topic)))))))

(defn start [webbit]
  (.start webbit))

(defn stop [webbit]
  (.stop webbit))
