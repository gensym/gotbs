(ns gotbs.run
  (use [ring.adapter.jetty])
  (use [gotbs.core]))

(run-jetty app {:port 8080})

  