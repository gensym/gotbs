(ns gotbs.feed.subscriptions
  (:use [gotbs.util.carousel])
  (:require
   [gotbs.util.worker-thread :as worker-thread]
   [gotbs.util.scheduler :as scheduler]
   [gotbs.busdata :as busdata]))


(defn make-subscriptions []
     {:carousel (ref (make-carousel))
      :subscriptions (ref {})
      :worker (worker-thread/make-worker)})


(defn- get-route [subscription-key]  subscription-key)

(defn- pop-with-actions [subscriptions]
   (dosync
    (let [key (peek @(:carousel subscriptions))
          funs (get @(:subscriptions subscriptions) key)]
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

(defn- unsubscribe [subscriptions rt subscription-fn]
  (dosync
   (alter (:subscriptions subscriptions)
          (fn [m]
            (let [with (get m rt [])
                  without (remove (partial = subscription-fn) with)]
              (if (empty? without)
                (dissoc m rt)
                (assoc m rt without)))))))

(defn subscribe [subscriptions rt subscription-fn]
  "Returns the function to unsubscribe"
  (dosync
   (alter (:carousel subscriptions) (fn [c] (conj c rt)))
   (alter (:subscriptions subscriptions) (fn [m]
                                           (let [val (get m rt [])]
                                             (assoc m rt (conj val subscription-fn))))))
  (schedule-action subscriptions)
  #(unsubscribe subscriptions rt subscription-fn))

(defn schedule [subscriptions]
  (dosync
   (if-let [to-add (keys @(:subscriptions subscriptions))]
     (alter (:carousel subscriptions) (fn [c] (apply conj c to-add)))))
  (schedule-action subscriptions))

(defn stop [subscriptions]
  (worker-thread/stop (:worker subscriptions)))
