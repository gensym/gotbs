(ns gotbs.js.run-plots
  (:require [gotbs.js.timeline :as timeline]
            [cljs.reader :as reader]
            [goog.net.XhrIo :as gxhr]))


(defn to-xy [{x :time y :dist}] [x y])

(defn get-uri [start-date end-date]
  (let [uri
        (goog.Uri. "/")
        qr (. uri (getQueryData))]
    (.add qr "from" (.toJSON start-date))
    (.add qr "to" (.toJSON end-date))
    (.setPath uri "/runs/for_route.edn")))

(defn  get-runs-json [timelines start-date end-date]
  (gxhr/send (get-uri start-date end-date)
             (fn [message]
               (let [paths (->> message
                                (.-target)
                                (.getResponseText)
                                (reader/read-string)
                                (:runs)
                                (map (partial map to-xy)))]
                 (timeline/set-paths! timelines paths [] [])
                 (timeline/draw timelines)))))

(defn ^:export get-data [start-date end-date]
  (let [timelines (timeline/make-timelines-graph "timelines")]
    (get-runs-json timelines start-date end-date)
    timelines))
