(ns gotbs.util.thread-utils)

(defn threads
  "Get a seq of the current threads."
  []
  (let [grp (.getThreadGroup (Thread/currentThread))
        cnt (.activeCount grp)
        ary (make-array Thread cnt)]
    (.enumerate grp ary)
    (seq ary)))
