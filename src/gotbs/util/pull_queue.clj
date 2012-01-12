(ns gotbs.util.pull-queue
  (:use [gotbs.util.carousel]))

(defn make-pull-queue
  "Creates a mutable work queue. When process-all-items is called, all items on the queue are processed in batches on a single thread, using the given worker function. As each batch is processed, the publisher function is called with the results of processing the batch. It is up to the client to ungroup the results for the batch, if necessary.
Note that the worker should take a list of items. That list's length will be equivalent to the given batchsize. The publisher, hower, will receive a singe argument (which is the result of applying the worker to the batch)"
  [worker publisher batchsize]
  {:queue (ref (make-carousel))
   :batchsize batchsize
   :fetcher worker
   :publisher publisher
   :agent (agent nil)})

(defn push-work-item [pull-queue item]
  "Adds an item to the queue. If the item is already present on the queue, has no effect"
     (let [queue (:queue pull-queue)]
       (dosync
        (alter queue (fn [q] (conj q item))))))

(defn- pop-batch [pull-queue]
  (let [queue (:queue pull-queue)
        batchsize (:batchsize pull-queue)]
    (dosync
     (let [items (take batchsize @queue)]
       (alter queue (fn [q] (poptimes batchsize q)))
       items))))

(defn- loop-in-batchsize [pull-queue]
  (loop []
    (let [batch (pop-batch pull-queue)
          fetcher (:fetcher pull-queue)
          publisher (:publisher pull-queue)]
      (if (not (empty? batch))
        (do
          (publisher (fetcher batch))
          (recur))))))

(defn- async-do [pull-queue fun] )

(defn process-all-items [pull-queue]
  (send-off (:agent pull-queue) (fn [_]  (loop-in-batchsize pull-queue))))
