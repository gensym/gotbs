(ns gotbs.util.pull-queue
  (:use [gotbs.util.carousel]))

(defn make-pull-queue
  "Creates a work queue. When process-all-items is called, items are processed (in batches if batchsize > 1) on a single thread, using the given worker function. As each batch is processed, the publisher function is called with the results of processing the batch. It is up to the client to ungroup the results for the batch, if necessary"
  ([worker publisher] (make-pull-queue worker publisher 1))
  ([worker publisher batchsize]
     {:queue (ref (make-carousel))
      :batchsize batchsize
      :fetcher worker
      :publisher publisher
      :agent (agent nil)}))

(defn push-work-item [pull-queue item]
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

(defn process-all-items [pull-queue]
  (send-off
   (:agent pull-queue)
   (fn [_]  (loop-in-batchsize pull-queue))))

