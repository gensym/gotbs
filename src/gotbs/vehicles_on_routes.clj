(ns gotbs.vehicles-on-routes
  (:use [gotbs.util.carousel]))

(defn make-routes-to-publish [publish-fn]
  {:queue (ref (make-carousel))
   :publisher publish-fn
   :agent (agent nil)})

(defn push-route [routes-to-publish route]
     (let [queue (:queue routes-to-publish)]
       (dosync
        (alter queue (fn [q] (conj q route))))))

(defn- fetch-and-publish-data [publisher batch]
  ;; TODO - instead of publishing the batch, fetch data from the batch
  ;; and publish that
  (publisher batch))

(defn- pop-batch [routes-to-publish batchsize]
  (let [queue (:queue routes-to-publish)]
    (dosync
     (let [items (take batchsize @queue)]
       (alter queue (fn [q] (poptimes batchsize q)))
       items))))

(defn- loop-in-batchsize [routes-to-publish batchsize]
  (loop []
    (let [batch (pop-batch routes-to-publish batchsize)]
      (if (not (empty? batch))
        (do
          (fetch-and-publish-data (:publisher routes-to-publish) batch)
          (recur))))))

(defn process-routes-queue [routes-to-publish]
  (do
    (send-off
     (:agent routes-to-publish)
     (fn [_] 
       (loop-in-batchsize routes-to-publish 10)))
    nil))

