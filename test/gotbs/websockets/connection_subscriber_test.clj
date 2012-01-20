(ns gotbs.websockets.connection-subscriber-test
  (:use [gotbs.websockets.connection-subscriber] :reload-all
        [midje.semi-sweet])
  (:require [clojure.test :as test]
            [gotbs.websockets.connections :as connections]))

(declare fake-subscriber)

(test/deftest should-invoke-subscriber-callback-on-subscribe
  (let [connection-set (Object.)
        connection (Object.)]
    (expect
     (let [subscriber (connection-subscriber connection-set fake-subscriber)]
       (subscribe subscriber connection "topic")) => true
       (fake (connections/subscribe connection-set connection "topic") => true)
       (fake (fake-subscriber "topic") => false))))
