(ns gotbs.web.route-subscriptions
  "Handles subscriptions by subscriber. Each subscriber (e.g., a websocket connection) may have several subscriptions. When a subscriber is dropped, this cancels all of its associated subscriptions. "
  (:require [clojure.tools.logging :as log]
            [gotbs.feed.subscriptions :as feed]))

(defn make-subscriptions [feed-subscriptions]
  {:subscriptions feed-subscriptions
   :subscriber-fns (ref {})
   :topics-by-subscriber (ref {})
   :subscribers-by-topic (ref {})})

(defn add-subscriber [subscriptions broadcast-fn subscriber]
  "broadcast-fn takes the subscriber, the topic, and the message"
  (let [on-message (partial broadcast-fn subscriber)]
    (dosync
     (ensure (:subscribers-by-topic subscriptions))
     (alter (:subscriber-fns subscriptions) (fn [m] (assoc m subscriber on-message)))
     (alter (:topics-by-subscriber subscriptions) (fn [m] (assoc m subscriber #{}))))))

(defn- remove-subscriber [subscriber topic subscribers-by-topic]
  (let [subscribers (get subscribers-by-topic topic #{})]
    (assoc subscribers-by-topic topic (disj subscribers subscriber))))

(defn unsubscribe [subscriptions subscriber topic]
  (dosync
   (ensure (:topics-by-subscriber subscriptions))
   (let [subscriber-fn  (get @(:subscriber-fns subscriptions) subscriber)]
     (feed/unsubscribe (:subscriptions subscriptions) topic subscriber-fn))
   (alter (:subscribers-by-topic subscriptions)
          (partial remove-subscriber subscriber topic))))

(defn drop-subscriber [subscriptions subscriber]
  (dosync
   (ensure (:subscribers-by-topic subscriptions))
   (dorun
    (map
     (partial unsubscribe subscriptions subscriber)
     (get @(:topics-by-subscriber subscriptions) subscriber)))
   (alter (:subscriber-fns subscriptions) (fn [m] (dissoc m subscriber)))
   (alter (:topics-by-subscriber subscriptions)
          (fn [m] (dissoc m subscriber)))))

(defn subscribe [subscriptions subscriber topic]
  (dosync
   (alter (:topics-by-subscriber subscriptions)
          (fn [m] (assoc m subscriber
                        (conj (get m subscriber) topic))))
   (alter (:subscribers-by-topic subscriptions)
          (fn [m] (assoc m topic (conj (get m topic #{}) subscriber))))
   (let [subscriber-fn  (get @(:subscriber-fns subscriptions) subscriber)]
     (feed/subscribe (:subscriptions subscriptions) topic subscriber-fn))))

(defn stop [location-subscriber]
  (feed/stop (:subscriptions location-subscriber)))
