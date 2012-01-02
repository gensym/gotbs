(ns gotbs.vehicles-on-routes-test
  (:use [gotbs.vehicles-on-routes] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]))

(test/deftest should-process-in-batches-of-ten
  (let [p (promise)
        do-process (fn [val] (deliver p val))]
    (expect (let [routes (make-routes-to-publish do-process)]
              (push-route routes 42)
              (process-routes-queue routes)
              @p) => '(42))))
