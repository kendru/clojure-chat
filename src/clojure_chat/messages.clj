(ns clojure-chat.messages
  (:require [org.httpkit.server :as http]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.exchange :as le]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [cheshire.core :as json]
            [clojure-chat.db :as db]))

(def exchange-name "messages.all")

(def connected-ids (atom []))

(defn- initialize-rmq []
  (let [conn (rmq/connect)
        ch (lch/open conn)]
    (le/declare ch exchange-name "fanout" {:durable false :auto-delete false})
    ch))

(def rmq-channel (delay (initialize-rmq)))

(defn get-queue-name [id]
  (str "messages." id))

(defn register-channel!
  "Adds a channel to the list of channels to notify whenever an update is received."
  [id socket]
  (let [ch @rmq-channel
        queue-name (get-queue-name id)
        handler (fn [_ _ ^bytes payload]
                  (http/send! socket (String. payload "UTF-8")))]
    (swap! connected-ids conj id)
    (lq/declare ch queue-name {:exclusive false :auto-delete false})
    (lq/bind ch queue-name exchange-name)
    (lc/subscribe ch queue-name handler {:auto-ack true})))

(defn deregister-channel!
  "Removes a channel from the list to be notified. Called when client disconnects."
  [id]
  (lq/delete @rmq-channel (get-queue-name id))
  (swap! connected-ids #(remove (partial = id) %)))

(defn publish-to-exchange! [msg]
  (let [ch @rmq-channel]
    (lb/publish ch exchange-name "" msg {:content-type "application/json" :type "message.send"})))

(defn broadcast!
  "Sends message to all connected clients, and store in the database"
  [msg]
  (publish-to-exchange! (json/generate-string msg))
  (db/store-msg! msg))
