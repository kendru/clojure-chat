(ns clojure-chat.handler
  (:require [clojure.java.io :as io]
            [bidi.bidi :as bidi]
            [bidi.ring :refer [make-handler ->ResourcesMaybe]]
            [org.httpkit.server :as http]
            [cheshire.core :as json]
            [clojure-chat.messages :as m]
            [clojure-chat.db :as db]))

(defn serve-file
  "Serve an HTML file from the resources directory."
  [filename]
  (fn [_]
    (let [file (io/file (io/resource filename))]
      (if (.exists file)
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body (slurp file)}
        {:status 404
         :headers {"Content-Type" "text/plain"}
         :body "Cannot find file"}))))


(defn ws-handler [req]
  (let [id (str (java.util.UUID/randomUUID))]    
    (http/with-channel req ch
      (m/register-channel! id ch)
      (println (str "[" id "] connected"))

      ;; Send user all messages from past 5 minutes
      (doseq [msg (db/load-messages 5)]
        (http/send! ch (json/generate-string msg)))

      (m/broadcast! {:type :status
                     :msg "User joined"
                     :time (System/currentTimeMillis)})

      (http/on-receive ch (fn [data]
                            (println (str "[" id "] received") data)
                            (m/broadcast! (-> data
                                              (json/parse-string true)
                                              (assoc :type :message
                                                     :time (System/currentTimeMillis))))))
      (http/on-close ch (fn [status]
                          (println (str "[" id "] disconnected"))
                          (m/broadcast! {:type :status
                                         :msg "User left"
                                         :time (System/currentTimeMillis)})
                          (m/deregister-channel! id))))))

(declare ui-config)
(defn js-config [_]
  {:status 200
   :headers {"Content-Type" "text/javascript"}
   :body (str "(function(window) { window.cfg = " (json/generate-string ui-config) "; }(window));")})

(def routes ["/" {"" {:get (serve-file "public/index.html")}
                  "config.js" js-config
                  "assets/" (->ResourcesMaybe {:prefix "public/"})
                  "ws" ws-handler}])


(def ui-config {:url (bidi/path-for routes ws-handler)})


(def app (make-handler routes))
