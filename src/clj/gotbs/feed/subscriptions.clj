(ns gotbs.feed.subscriptions
  (:use [gotbs.util.carousel])
  (:require
   [clojure.tools.logging :as log]
   [gotbs.util.worker-thread :as worker-thread]
   [gotbs.util.scheduler :as scheduler]))

(defn make-subscriptions [action-fn]
  "action-fn takes a single key - whatever the key used to subscribe was"
     {:carousel (ref (make-carousel))
      :subscriptions (ref {})
      :action-fn action-fn
      :worker (worker-thread/make-worker)})

(defn- pop-with-actions [subscriptions]
  (let [[k f]
        (dosync
         (ensure (:subscriptions subscriptions))
         (let [key (peek @(:carousel subscriptions))
               funs (get @(:subscriptions subscriptions) key)]
           (alter (:carousel subscriptions) pop)
           [key funs]))]
    (log/info (str "[feed.subscriptions] returning - " k))
    [k f]))

(defn- act [subscriptions]
  (loop [[key funs] (pop-with-actions subscriptions)]
    (log/info (str "[feed.subscriptions] Invoking " (count funs) " callback functions"))
    (if (not (empty? funs))
      (let [results ((:action-fn subscriptions) key)]
        (dorun
         (map #(% key results) funs))
        (recur (pop-with-actions subscriptions))))))

(defn- schedule-action [subscriptions]
  (worker-thread/put (:worker subscriptions) #(act subscriptions)))

(defn unsubscribe [subscriptions key subscription-fn]
  (dosync
   (alter (:subscriptions subscriptions)
          (fn [m]
            (let [with (get m key [])
                  without (remove (partial = subscription-fn) with)]
              (if (empty? without)
                (dissoc m key)
                (assoc m key without)))))))

(defn subscribe [subscriptions key subscription-fn]
  "Returns the function to unsubscribe. subscription-fn takes the key and the message"
  (if (nil? key) (throw (NullPointerException. "key")))
  (log/info "[feed.subscriptions] Subscribing to key " key)
  (dosync
   (alter (:carousel subscriptions) (fn [c] (conj c key)))
   (alter (:subscriptions subscriptions) (fn [m]
                                           (let [val (get m key [])]
                                             (assoc m key (conj val subscription-fn))))))
  (schedule-action subscriptions)
  #(unsubscribe subscriptions key subscription-fn))

(defn schedule [subscriptions]
  (log/info "[feed.subscriptions] scheduling...")
  (dosync
   (if-let [to-add (keys @(:subscriptions subscriptions))]
     (alter (:carousel subscriptions) (fn [c] (apply conj c to-add)))))
  (schedule-action subscriptions))

(defn stop [subscriptions]
  (worker-thread/stop (:worker subscriptions)))
