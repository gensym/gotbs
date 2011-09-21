(ns gotbs.websockets.connections-test
  (:use [gotbs.websockets.connections] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]))

(test/deftest should-broadcast-to-nothing-when-nothing-subscribed
  (expect (broadcast (connection-set) "some-topic" "some data") => 0))

(test/deftest should-broadcast-to-subscribed-connections
  (let [cs (connection-set)
        topic "some-topic"
        conn1 "conn1"
        conn2 "conn2"]
    (subscribe cs conn1 topic)
    (subscribe cs conn2 topic)
    (expect 
     (broadcast cs topic "some data") => 2
     (fake (send-data cs conn1 "some data") => nil)
     (fake (send-data cs conn2 "some data") => nil))))

(test/deftest should-unsubscribe-when-connection-closed
  (let [cs (connection-set)
        topic "some-topic"
        conn1 "conn1"
        conn2 "conn2"]
    (subscribe cs conn1 topic)
    (subscribe cs conn2 topic)
    (close cs conn1)
    (expect 
     (broadcast cs topic "some data") => 1
     (fake (send-data cs conn2 "some data") => nil))))
