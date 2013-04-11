(ns gotbs.websockets.connection-subscriber
  (:require [gotbs.websockets.connections :as ws-conn])
  (:use [gotbs.util.combinators]))

(defn connection-subscriber [connection-set on-subscribe]
  "on-subscribe is a fn that takes a single argument - the topic being subscribed-to"
  {:connection-set connection-set
   :subscribe_callback on-subscribe})

(defn open [subscriber connection]
  (ws-conn/open (:connection-set subscriber) connection ))

(defn close [subscriber connection]
  (ws-conn/close (:connection-set subscriber) connection))

(defn subscribe [subscriber connection topic]
  (K
   (ws-conn/subscribe (:connection-set subscriber) connection topic)
   ((:subscribe_callback subscriber) topic)))
