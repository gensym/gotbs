(ns gotbs.util.carousel)

(defn make-carousel [items]
  {:items items})

(defn cq-peek [cq]
  (first (:items cq)))

(defn cq-take [cq n]
  (take n (:items cq)))

(defn cq-process [cq f n]
  (map f (cq-take cq n)))

(defn cq-mark-processed [cq n]
  (make-carousel (drop n (:items cq))))

(defn cq-conj
  ([cq x]
     (make-carousel (concat (:items cq) (list x))))
  ([cq x & xs]
     (make-carousel (concat (:items cq) (list x) xs))))
