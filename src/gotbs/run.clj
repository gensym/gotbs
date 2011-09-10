(ns gotbs.run
  (use [gotbs.websockets.jetty]
       [gotbs.websockets.webbit])
  (require [gotbs.core]))

(run-webbit-websockets 8888)
(run-jetty-websockets #'gotbs.core/app 8080)

  
