(ns go-visual.core
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json]))

(defn extract-pipeline-instance-history 
  [pipeline]
  {:name (:name pipeline) 
   :counter (:counter pipeline)
   :statges (map 
             (fn [stages-map] {:name (get stages-map :name) :counter (get stages-map :counter)}) 
             (get pipeline :stages))})

(defn fetch-pipeline-instance
  [url username password]
  (json/read-str (get (client/get url {:basic-auth [username password]}) :body)
                 :key-fn keyword))

(defn -main
  "I don't do a whole lot ... yet."
  [& {:keys [url username password]}]

  (def pipeline-data 
    ((fn [parms] 
       (extract-pipeline-instance-history  
        (fetch-pipeline-instance 
         url 
         username 
         password))) 
     nil))
  (println pipeline-data))

