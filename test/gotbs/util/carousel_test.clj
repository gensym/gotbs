(ns gotbs.util.carousel-test
  (:use [gotbs.util.carousel] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]))

(test/deftest should-contain-inserted-items
  (let [cq (make-carousel (range 5))]
    (expect (cq-peek cq) => 0)))

(test/deftest should-take-items
  (let [cq (make-carousel (range 5))]
    (expect (cq-take cq 10) => (range 5))))

(declare do-process)

(test/deftest should-process-items
  (let [cq (make-carousel (range 5))]
    (expect (cq-mark-processed cq 3)  => (make-carousel (list 3 4)))))

(test/deftest should-mark-items-processed
  (let [cq (make-carousel (range 5))]
    (expect (cq-process cq #(do-process %) 3)  => (list true false true)
            (fake (do-process 0) => true)
            (fake (do-process 1) => false)
            (fake (do-process 2) => true))))
