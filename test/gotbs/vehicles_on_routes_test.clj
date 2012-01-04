(ns gotbs.vehicles-on-routes-test
  (:use [gotbs.vehicles-on-routes] :reload-all
        [gotbs.busdata :only [in-flight-vehicles]]
        [midje.semi-sweet])
  (:require [clojure [test :as test]])
  (:import [java.util.concurrent TimeUnit CountDownLatch]))


(declare fetch-data)
;; Processing the routes queue sends routes (in batches) to an agent
;; for processing, so these tests are a little more complex than
;; typical unit tests. Since process-routes-queue may return before
;; the agent has finished processing, we need a way to block until the
;; agent has finished promising. The way we're doing so here is by
;; using a CountDownLatch
(test/deftest should-process-in-batches-of-ten
  (let [counter (CountDownLatch. 2)
        do-process (fn [val] (.countDown counter))]
    (expect (let [routes (make-routes-to-publish fetch-data do-process)]
              (dorun
                (map #(push-route routes %) (range 15))
                )
              (process-routes-queue routes)
              (.await counter 5 TimeUnit/MILLISECONDS)) => true
              (fake (fetch-data (range 10)) => nil)
              (fake (fetch-data (drop 10 (range 15))) => nil))))





 
