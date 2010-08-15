(ns gotbs.run
  (use [ring.adapter.jetty])
  (require [gotbs.core]))

(run-jetty #'gotbs.core/app {:port 8080})

  