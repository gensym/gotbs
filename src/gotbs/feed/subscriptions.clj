(ns gotbs.feed.subscriptions
  (:use [gotbs.util.carousel])
  (:require
   [gotbs.util.worker-thread :as worker-thread]
   [gotbs.util.scheduler :as scheduler]
   [gotbs.busdata :as busdata]))

(defn make-subscriptions []
     {:carousel (ref (make-carousel))
      :subscription-fns (ref {})
      :worker (worker-thread/make-worker)})

(defn- get-route [subscription-key]  subscription-key)

(defn- pop-with-actions [subscriptions]
   (dosync
    (let [key (peek @(:carousel subscriptions))
          funs (get @(:subscription-fns subscriptions) key)]
      (alter (:carousel subscriptions) pop)
      [key funs])))

(defn- act [subscriptions]
  (loop [[key funs] (pop-with-actions subscriptions)]
    (if (not (empty? funs))
      (let [vehicles (busdata/in-flight-vehicles [(get-route key)])]
        (dorun
         (map #(% vehicles) funs))
        (recur (pop-with-actions subscriptions))))))

(defn- schedule-action [subscriptions]
  (worker-thread/put (:worker subscriptions) #(act subscriptions)))

(defn subscribe [subscriptions rt subscription-fn]
  (dosync
   (alter (:carousel subscriptions) (fn [c] (conj c rt)))
   (alter (:subscription-fns subscriptions) (fn [m]
                                           (let [val (get m rt [])]
                                             (assoc m rt (conj val subscription-fn))))))
  (schedule-action subscriptions))



;; TODO - unsubscribe

(defn schedule [subscriptions]
  (dosync
   (let [to-add (keys @(:subscription-fns subscriptions))]
     (alter (:carousel subscriptions) (fn [c] (apply conj c to-add)))))
  (schedule-action subscriptions))

(defn stop [subscriptions]
  (worker-thread/stop (:worker subscriptions)))
