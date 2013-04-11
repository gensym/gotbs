(ns gotbs.util.worker-thread
  (:use gotbs.util.combinators)
  (:import java.util.concurrent.LinkedBlockingQueue))

(defn- worker-loop [mailbox done]

  ;; todo - catch the interrupted exception
  (loop []
    (let [f (.take mailbox)] (f))
    (if (not @done) (recur))))

(defn make-worker []
  (let [mailbox  (LinkedBlockingQueue.)
        done  (atom false)
        thread  (Thread. (fn [] (worker-loop mailbox done)))]
    (K 
     {:mailbox mailbox
      :done done
      :thread thread}
     (.start thread))))

(defn put [worker f]
  (.add (:mailbox worker) f))

(defn stop [worker]
  (swap! (:done worker) (fn [_] true))
  (.interrupt (:thread worker)))
