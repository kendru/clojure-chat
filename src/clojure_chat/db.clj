(ns clojure-chat.db
  (:refer-clojure :exclude [sort find])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer :all]
            [monger.operators :refer :all]))

(def coll "messages")

(defn- initialize-database []
  (let [conn (mg/connect)
        db (mg/get-db conn "clojure-chat")]
    (mc/ensure-index db coll (array-map :time -1 :type 1))
    db))

(def mdb (delay (initialize-database)))

(defn store-msg!
  "Save a message to Mongo"
  [msg]
  (mc/insert @mdb coll msg))

(defn load-messages
  "Get all messages of type \"message\" from the past minutes-ago minutes"
  [minutes-ago]
  (let [since (- (System/currentTimeMillis)
                 (* minutes-ago 1000 60))]
    (->> (with-collection @mdb coll
           (find {:time {$gte since}
                  :type "message"})
           (fields [:type :time :user :msg])
           (sort (array-map :time 1)))
         (map #(dissoc % :_id)))))
