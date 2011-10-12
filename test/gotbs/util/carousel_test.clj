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
        cq2 (poptimes 3 cq1)]
    (expect (map #(do-process %) cq2) => '(true false)
            (fake (do-process 3) => true)
            (fake (do-process 4) => false))))

(test/deftest should-conj-items
  (let [cq (make-carousel (range 5))
        cq2 (conj cq 10 11)]
    (expect (take 7 cq2) => '(0 1 2 3 4 10 11))))

(test/deftest should-drop-items
  (let [cq (make-carousel (range 5))
        cq2 (poptimes 3 cq)]
    (expect (take 5 cq2) => '(3 4))))

(test/deftest should-readd-items
  (let [cq1 (make-carousel (range 5))
        cq2 (poptimes 2 cq1)
        cq3 (conj cq2 1)
        cq4 (conj cq3 0)]
    (expect (take 10 cq2) => '(2 3 4))
    (expect (take 10 cq3) => '(2 3 4 1))
    (expect (take 10 cq4) => '(2 3 4 0 1))))

(test/deftest should-make-empty-carousel
  (let [cq (make-carousel)]
    (expect (empty? cq) => true)
    (expect (take 10 cq) => '())))

(test/deftest should-make-carousel-from-empty-collection
  (let [cq (make-carousel (range 0))]
    (expect (empty? cq) => true)
    (expect (take 10 cq) => '())))

(defn ignore []
  (test/deftest should-drop-items-after-passing-through
    (let [cq1 (make-carousel (range 3))
          cq2 (pop cq1)
          cq3 (poptimes 2 cq2)
          cq4 (poptimes 3 cq3)]
      (expect (take 3 cq1) => '(0 1 2)))))


(test/deftest should-drop-items-after-passing-through
  (let [cq1 (make-carousel (range 3))
        cq2 (pop cq1)
        cq3 (poptimes 2 cq2)
        cq4 (poptimes 3 cq3)
        cq5 (-> cq4 (conj 2) (conj 1) (conj 0))]
    (expect (take 3 cq1) => '(0 1 2))
    (expect (empty? cq3) => true)
    (expect (take 3 cq5) => '(2 1 0))))

