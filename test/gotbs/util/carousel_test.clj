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
    (expect (cq-process cq #(do-process %) 3)  => (list true false true)
            (fake (do-process 0) => true)
            (fake (do-process 1) => false)
            (fake (do-process 2) => true))))

(test/deftest should-mark-items-processed
  (let [cq1 (make-carousel (range 5))
        cq2 (cq-mark-processed cq1 3)]
    (expect (cq-process cq2 #(do-process %) 3) => (list true false)
            (fake (do-process 3) => true)
            (fake (do-process 4) => false))))

(test/deftest should-conj-items
  (let [cq (make-carousel (range 5))
        cq2 (cq-conj cq 10 11)]
    (expect (cq-take cq2 7) => (list 0 1 2 3 4 10 11))))
