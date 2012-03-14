(ns gotbs.web.route-subscriptions
  "Handles subscriptions by subscriber. Each subscriber (e.g., a websocket connection) may have several subscriptions. When a subscriber is dropped, this cancels all of its associated subscriptions. "
  (:require [clojure.tools.logging :as log]
            [gotbs.feed.subscriptions :as feed])
  (:use [gotbs.route-data :only (route-descriptor)]))

(defn make-subscriptions [feed-subscriptions]
  {:subscriptions feed-subscriptions
   :subscriber-fns (ref {})
   :routes-by-subscriber (ref {})
   :subscribers-by-route (ref {})})

(defn add-subscriber [subscriptions broadcast-fn subscriber]
  "broadcast-fn takes the subscriber, the route, and the message"
  (let [on-message (partial broadcast-fn subscriber)]
    (dosync
     (ensure (:subscribers-by-route subscriptions))
     (alter (:subscriber-fns subscriptions) (fn [m] (assoc m subscriber on-message)))
     (alter (:routes-by-subscriber subscriptions) (fn [m] (assoc m subscriber #{}))))))

(defn- remove-subscriber [subscriber route subscribers-by-route]
  (let [subscribers (get subscribers-by-route route #{})]
    (assoc subscribers-by-route route (disj subscribers subscriber))))

(defn unsubscribe [subscriptions subscriber route]
  (dosync
   (ensure (:routes-by-subscriber subscriptions))
   (let [subscriber-fn  (get @(:subscriber-fns subscriptions) subscriber)]
     (feed/unsubscribe (:subscriptions subscriptions) route subscriber-fn))
   (alter (:subscribers-by-route subscriptions)
          (partial remove-subscriber subscriber route))))

(defn drop-subscriber [subscriptions subscriber]
  (dosync
   (ensure (:subscribers-by-route subscriptions))
   (dorun
    (map
     (partial unsubscribe subscriptions subscriber)
     (get @(:routes-by-subscriber subscriptions) subscriber)))
   (alter (:subscriber-fns subscriptions) (fn [m] (dissoc m subscriber)))
   (alter (:routes-by-subscriber subscriptions)
          (fn [m] (dissoc m subscriber)))))

(defn subscribe [subscriptions subscriber [route-display-name direction]]
  (let [route (:rt (route-descriptor route-display-name))]
    (dosync
     (alter (:routes-by-subscriber subscriptions)
            (fn [m] (assoc m subscriber
                          (conj (get m subscriber) route))))
     (alter (:subscribers-by-route subscriptions)
            (fn [m] (assoc m route (conj (get m route #{}) subscriber))))
     (let [subscriber-fn  (get @(:subscriber-fns subscriptions) subscriber)]
       (feed/subscribe (:subscriptions subscriptions) route subscriber-fn)))))

(defn stop [location-subscriber]
  (feed/stop (:subscriptions location-subscriber)))
