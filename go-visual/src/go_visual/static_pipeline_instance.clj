(ns go-visual.static_pipeline_instance
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:require [go-visual.string2number :as string2number]))

(defn fetch-pipeline-datas
  [url username password]
  (json/read-str (get (client/get url {:basic-auth [username password]}) :body)
                 :key-fn keyword))

(defn extract-pipeline-instance-history 
  [pipeline]
  {:name (:name pipeline) 
   :counter (:counter pipeline)
   :stages (map 
            (fn [stages-map] {:name (get stages-map :name) :counter (get stages-map :counter) :jobs (:jobs stages-map)}) 
            (:stages pipeline))})

(defn statistic-pipeline-instace
  [pipeline-instance]
  {:name (:name pipeline-instance)
   :counter (:counter pipeline-instance)
   :end-time (:end-time pipeline-instance)
   :pipeline-run-times   (+ 1 (- 
                               (reduce + (map (comp string2number/to-number :counter) (:stages pipeline-instance))) 
                               ((comp count :stages) pipeline-instance)))})

(defn statis-pipeline-success-status
  [pipelines]
  (map 
   (fn [pipeline] 
     {:name (:name pipeline) 
      :counter (:counter pipeline) 
      :status  (every? 
                #(and 
                  (true? (:scheduled %)) 
                  (= (str (:result %)) "Passed")) 
                (:stages pipeline))})
   pipelines))

(defn statis-pipeline-accumulate-success-counter
  [fn-get-last-success-rate last-accumulate-success-list pipeline-instance]
  (if (true? (:status pipeline-instance)) 
    (assoc pipeline-instance :success (+ 1 (fn-get-last-success-rate last-accumulate-success-list)))
    (assoc pipeline-instance :success 0)))

(defn statis-pipeline-end-time
  [pipeline]
  (assoc pipeline :end-time (:scheduled_date (last (flatten  (map :jobs (:stages pipeline)))))))

(defn extract-last-pipeline-continue-failure-counter 
  [last-result]
  (if (empty? last-result) 0
      (if (list? last-result) (:success (last last-result)) (:success last-result))))

(defn statis-accumulate-pipeline-failure-counter
  [pipelines]
  (flatten 
   (reduce
    (fn [last-accumulate-result-list pipeline-instance]
      (list 
       last-accumulate-result-list
       (statis-pipeline-accumulate-success-counter 
        extract-last-pipeline-continue-failure-counter 
        last-accumulate-result-list 
        pipeline-instance))) 
    '() 
    pipelines)))

(defn filter-time-range
  [pipelines start-time end-time]
  (filter #(and (not (nil? (:end-time %))) (and (> (:end-time %) start-time) (< (:end-time %) end-time))) pipelines))

(defn statistic-each-pipeline-stage-run-time
  [url username password start-time-str end-time-str]
  (def pipelines (:pipelines (fetch-pipeline-datas url username password)))
  (def start-time (c/to-long start-time-str))
  (def end-time (c/to-long end-time-str))

  (statis-accumulate-pipeline-failure-counter 
   (map  
    (fn [statistic-pipeline status-pipeline] 
      (assoc statistic-pipeline :status (:status status-pipeline))) 
    (sort-by :counter < 
             (map 
              statistic-pipeline-instace 
              (filter-time-range  
               (map (comp statis-pipeline-end-time extract-pipeline-instance-history) pipelines)
               start-time end-time)))
    (sort-by :counter < 
             (statis-pipeline-success-status pipelines)))))

