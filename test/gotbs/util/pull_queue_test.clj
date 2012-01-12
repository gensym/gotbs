(ns gotbs.util.pull-queue-test
  (:use [gotbs.util.pull-queue] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]])
  (:import [java.util.concurrent TimeUnit CountDownLatch]))


(declare worker)
;; Processing the routes queue sends routes (in batches) to an agent
;; for processing, so these tests are a little more complex than
;; typical unit tests. Since process-routes-queue may return before
;; the agent has finished processing, we use a CountDownLatch to block until the
;; agent has finished processing.
(test/deftest should-process-in-batches-of-ten
  (let [counter (CountDownLatch. 2)
        do-process (fn [val] (.countDown counter))]
    (expect (let [queue (make-pull-queue worker do-process 10)]
              (dorun
               (map #(push-work-item queue %) (range 15)))
              (process-all-items queue)
              (.await counter 5 TimeUnit/MILLISECONDS)) => true ;; False means we timed out
              (fake (worker (range 10)) => nil)
              (fake (worker (drop 10 (range 15))) => nil))))

(test/deftest should-process-in-batchsize-1
  (let [counter (CountDownLatch. 3)
        do-process (fn [val] (.countDown counter))]
    (expect (let [queue (make-pull-queue worker do-process 1)]
              (dorun
               (map #(push-work-item queue %) (range 3)))
              (process-all-items queue)
              (.await counter 5 TimeUnit/MILLISECONDS)) => true ;; False means we timed out
              (fake (worker '(0)) => nil)
              (fake (worker '(1)) => nil)
              (fake (worker '(2)) => nil))))
