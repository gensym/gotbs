(ns gotbs.js.timeline)

(.write js/document "<p>WTF - get. it. on.</p>")
(.log js/console "FFFuuu")


(comment (def canvas-id "timelines"))

(defn draw-path [context path]
  (if (not (empty? path))
    (let [[x y] (first path)]
      (.beginPath context)
      (.moveTo context x y)
      (doseq [[x y] (rest path)]
        (.lineTo context x y))
      (.stroke context))))

(defn scale-points [x0 x1 width y0 y1 height path]
  (let [xrange (- x1 x0)
        yrange (- y1 y0)]
    (map (fn [[x y]]
           [(/ (- x x0) (* xrange width))
            (/ (- y y0) (* yrange height))])
         path)))

(defn find-range [pointsets]
  (reduce
   (fn [[xn xx yn yx] [x y]]
     [(min xn x)
      (max xx x)
      (min yn y)
      (max yx y)])
   [0 0 0 0]
   (apply concat pointsets)))

(defn ^:export draw-canvas [canvas-id paths] 

  (let [elem  (.getElementById js/document canvas-id)
        context (.getContext elem "2d")
        width (.-width elem)
        height (.-height elem)
        [x0 x1 y0 y1] (find-range paths)
        points (map (partial scale-points x0 x1 width y0 y1 height) paths)]

    (.clearRect context 0 0 width height)
    (.save context)
    (set! (.-lineWidth context) 1.5)
    (doseq [path points]
      (draw-path context path))
    (.restore context)))
