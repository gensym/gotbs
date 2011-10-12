(ns gotbs.util.carousel)

(deftype Carousel [items marked unmarked]

  clojure.lang.IPersistentStack

  (cons [this item]
        (let [new-queue (if (or
                             (contains? marked item)
                             (contains? unmarked item))
                          items
                          (conj items item))]
          (Carousel. new-queue (conj marked item) (disj unmarked item))))

  (peek [this] (first (seq this)))

  (pop [this]
       (loop [all items
              m marked
              um unmarked]
         (let [item (first all)]
           (cond
            (empty? all) (Carousel. clojure.lang.PersistentQueue/EMPTY #{} #{})
            (contains? m item) (Carousel.
                                      (conj (pop all) item)
                                      (disj m item)
                                      (conj um item))
            (contains? um item) (recur (conj (pop all) item) m (disj um item))
            :else (recur (pop all) m um)))))

  clojure.lang.Seqable
  (seq [this]
       (let [filtered (filter #(contains? marked %) items)]
         (if (empty? filtered) nil filtered))))

(defn poptimes [n coll]
  (nth (iterate pop coll) n))

(defn make-carousel 
  "Create a Carousel - a data structure that functions as a circular queue.

    When items are popped and then readded to the queue, their original order is preserved, regardless of the order in which they are readded. However, when the queue has been cycled through without readding the item, the item gets dropped, and if it is then readded, it is placed on the end. Adding an item that is already on the queue has no effect. 

   Why would you want to use such a bizarre structure? Probably because you processing items and scheduling them to be processed on independent timelines, and you want to ensure fairness when the processing gets backed up. 
"
  ([]
     (Carousel. clojure.lang.PersistentQueue/EMPTY #{} #{}))
  ([items]
     (if (empty? items)
       (make-carousel)
       (Carousel. (apply (partial conj clojure.lang.PersistentQueue/EMPTY) items) (set items) #{}))))
