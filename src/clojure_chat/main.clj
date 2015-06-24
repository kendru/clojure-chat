(ns clojure-chat.main
  (:require [clojure-chat.handler :as handler]
            [org.httpkit.server :refer [run-server]]
            [environ.core :refer [env]])
  (:gen-class))

(defn -main
  "Server entry-point"
  [& args]
  (run-server handler/app {:port (Integer/parseInt (get env :http-port "3000"))}))
