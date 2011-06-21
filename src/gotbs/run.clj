(ns gotbs.run
  (use [ring.adapter.jetty7])
  (require [gotbs.core]))

(run-jetty #'gotbs.core/app {:port 8080})

  
