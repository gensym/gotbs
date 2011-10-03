(ns gotbs.util.carousel)

(deftype Carousel [items]
  clojure.lang.IPersistentStack
  (cons [this item] (Carousel. (concat items (list item))))
  (peek [this] (first items))
  (pop [this] (Carousel. (rest items)))

  clojure.lang.Seqable
  (seq [this] items))

(defn make-carousel [items]
     (Carousel. items))
