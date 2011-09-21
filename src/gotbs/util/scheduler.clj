(ns gotbs.util.scheduler
  (:import [java.util.concurrent ScheduledThreadPoolExecutor TimeUnit]))

(def ^:private pool (atom nil))

(defn- thread-pool []
  (swap! pool (fn [p] (or p (ScheduledThreadPoolExecutor. 1)))))

(defn periodically [f initial-delay period-milliseconds]
  "Periodically run a function. The function should execute quickly, since it will block the timer thread until it completes"
  (.scheduleAtFixedRate (thread-pool) f initial-delay period-milliseconds TimeUnit/MILLISECONDS))

(defn shutdown []
  (swap! pool ))
