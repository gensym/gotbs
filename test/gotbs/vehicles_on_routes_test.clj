(ns gotbs.vehicles-on-routes-test
  (:use [gotbs.vehicles-on-routes] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]))

;; Processing the routes queue sends routes (in batches) to an agent
;; for processing, so these tests are a little more complex than
;; typical unit tests. Since process-routes-queue may return before
;; the agent has finished processing, we need a way to block until the
;; agent has finished promising. The way we're doing so here is by
;; using a clojure.core/promise.
;; This approach comes with the drawback that if
;; the code breaks, the entire test run may grind to a halt (rather
;; than failing, as it should). One way to remedy that drawback is to
;; add a timeout on the deref, so that we fail the test if the agent
;; has not processed within a period of time. We're not doing that
;; yet, though.
(test/deftest should-process-in-batches-of-ten
  (let [p (promise)
        do-process (fn [val] (deliver p val))]
    (expect (let [routes (make-routes-to-publish do-process)]
              (push-route routes 42)
              (process-routes-queue routes)
              @p) => '(42))))
