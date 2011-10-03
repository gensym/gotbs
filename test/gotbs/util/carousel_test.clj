(ns gotbs.util.carousel-test
  (:use [gotbs.util.carousel] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]))

(test/deftest should-contain-inserted-items
  (let [cq (make-carousel (range 5))]
    (expect (peek cq) => 0)))

(test/deftest should-take-items
  (let [cq (make-carousel (range 5))]
    (expect (take 10 cq) => (range 5))))

(declare do-process)

(test/deftest should-map-items
  (let [cq1 (make-carousel (range 5))
        cq2 (drop 3 cq1)]
    (expect (map #(do-process %) cq2) => (list true false)
            (fake (do-process 3) => true)
            (fake (do-process 4) => false))))

(test/deftest should-conj-items
  (let [cq (make-carousel (range 5))
        cq2 (conj cq 10 11)]
    (expect (take 7 cq2) => (list 0 1 2 3 4 10 11))))

(test/deftest should-drop-items
  (let [cq (make-carousel (range 5))
        cq2 (drop 3 cq)]
    (expect (take 5 cq2) => (list 3 4))))
