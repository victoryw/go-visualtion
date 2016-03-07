(ns go-visual.core
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:require [go-visual.string2number :as string2number]))

(defn extract-pipeline-instance-history 
  [pipeline]
  {:name (:name pipeline) 
   :counter (:counter pipeline)
   :statges (map 
             (fn [stages-map] {:name (get stages-map :name) :counter (get stages-map :counter)}) 
             (get pipeline :stages))})

(defn fetch-pipeline-datas
  [url username password]
  (json/read-str (get (client/get url {:basic-auth [username password]}) :body)
                 :key-fn keyword))

(defn write-to-site-json
  [statis]
  (spit "../go-visual-site/data.json" (json/write-str
   {:categories (map #(format "%s-%s" (:name %) (:counter %)) statis)
    :data (map #(- (:statges-run-times %) (:statges %)) statis)})))

(defn statistic-pipeline-instace
  [pipeline-instance]
  {:name (:name pipeline-instance)
   :counter (:counter pipeline-instance)
   :statges-run-times   (reduce + (map (comp string2number/to-number :counter) (:statges pipeline-instance)))
   :statges ((comp count :statges) pipeline-instance)})

(defn -main
  "I don't do a whole lot ... yet."
  [& {:keys [url username password]}]
  (def statis 
    (map (comp  statistic-pipeline-instace 
                extract-pipeline-instance-history)
         (take 15 (:pipelines (fetch-pipeline-datas url username password)))))
  (println statis))

