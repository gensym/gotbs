(ns gotbs.run
  (use [gotbs.websockets.jetty])
  (require [gotbs.core]))

(run-jetty-websockets #'gotbs.core/app 8080)

  
