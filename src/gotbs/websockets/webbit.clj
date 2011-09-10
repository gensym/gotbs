(ns gotbs.websockets.webbit
  (:require [clojure.contrib.json :as json]
            [clojure.string :as s])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]))

(defn- on-message [connection json-message]
  (let [message (-> json-message json/read-json (get-in [:data :message]))]
    (.send connection (json/json-str {:type "upcased" :message (s/upper-case message) }))))

(defn run-webbit-websockets [port]
  (doto (WebServers/createWebServer port)
    (.add "/websocket"
          (proxy [WebSocketHandler] []
            (onOpen [c] (println "opened" c))
            (onClose [c] (println "closed" c))
            (onMessage [c j] (on-message c j))))
    (.start)))
