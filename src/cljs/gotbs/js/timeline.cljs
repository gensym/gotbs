(ns gotbs.js.timeline)

(defn draw-path [context path]
  (if (not (empty? path))
    (let [[x y] (first path)]
      (.beginPath context)
      (.moveTo context x y)
      (doseq [[x y] (rest path)]
        (.lineTo context x y))
      (.stroke context))))

(defn draw-x [context height xlabel]
  (.beginPath context)
  (.moveTo context xlabel 0)
  (.lineTo context xlabel height)
  (.stroke context))

(defn draw-y [context width ylabel]
  (.beginPath context)
  (.moveTo context 0 ylabel)
  (.lineTo context width ylabel)
  (.stroke context))

(defn scale-x [x0 x1 width x]
  (* (/ (- x x0) (- x1 x0)) width))

(defn scale-y [y0 y1 height y]
  (* ( / (- y y0) (- y1 y0)) height))


(defn scale-path [x-scaler y-scaler path]
  (map (fn [[x y]]
         [(x-scaler x)
          (y-scaler y)])
       path))


(defn find-range [pointsets]
  (let [[x y] (ffirst pointsets)]
    (reduce
     (fn [[xn xx yn yx] [x y]]
       [(min xn x)
        (max xx x)
        (min yn y)
        (max yx y)])
     [x x y y]
     (apply concat pointsets))))



(defn set-paths! [timelines paths xlabels ylabels]
  (let [[x0 x1 y0 y1] (find-range paths)
        x-scaler (partial scale-x x0 x1 (:width timelines))
        y-scaler (partial scale-y y0 y1 (:height timelines))
        points (map (partial scale-path x-scaler y-scaler) paths)]
    (swap! (:x-scale timelines) (constantly x-scaler))
    (swap! (:y-scale timelines) (constantly y-scaler))
    (swap! (:points timelines) (constantly points))))

(defn ^:export make-timelines-graph [canvas-id paths]
  (let [elem  (.getElementById js/document canvas-id)
        context (.getContext elem "2d")
        width (.-width elem)
        height (.-height elem)]
    
    {:context context
     :width width
     :height height
     :x-scale (atom identity)
     :y-scale (atom identity)
     :points (atom [])}))

(comment
  (do
    (def timelines (.-timelines js/document))
    (def points (.-points js/document))
    (def context (.-context js/document))))

(defn ^:export draw [timelines-graph]
  (let [{width :width
         height :height
         context :context} timelines-graph
         points @(:points timelines-graph)]
    (.clearRect context 0 0 width height)
    (.save context)
    (set! (.-lineWidth context) 1.5)
    (doseq [path points]
      (draw-path context path))
    (.restore context)))

(comment
  (do
    (def canvas-id "timelines")
    (def paths (.-paths js/document))
    (def graph (make-timelines-graph canvas-id paths [] []))
    (def x-scaler (:x-scale graph))
    (def y-scaler (:y-scale graph))))
