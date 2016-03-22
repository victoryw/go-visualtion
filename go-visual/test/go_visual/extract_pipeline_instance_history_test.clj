(ns go-visual.extract_pipeline_instance_history_test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [clj-http.fake :refer :all]
            [go-visual.static_pipeline_instance :refer :all]))
(deftest extract-pipeline-instance-history-test
  (testing "should extract pipeline with all stages"
    (let [origin-instance {:name "test-pipeline" 
                           :counter 13
                           :stages [{:name "stage-1" :counter 13 :jobs [{:name 1 :scheduled_date 123}]}
                                    {:name "stage-2" :counter 14 :jobs [{:name 1 :scheduled_date 123}]}]
                           :status 15
                           :time 100}]
      (is (= (extract-pipeline-instance-history origin-instance) 
             {:name "test-pipeline" 
              :counter 13 
              :stages [{:name "stage-1" :counter 13 :jobs [{:name 1 :scheduled_date 123}]}
                       {:name "stage-2" :counter 14 :jobs [{:name 1 :scheduled_date 123}]}]}))))
  (testing "should extract pipeline successful and value is nil when not with key"
    (let [origin-instance {:name "test-pipeline" 
                           :status 15
                           :time 100}]
      (is (= (extract-pipeline-instance-history origin-instance) 
             {:name "test-pipeline" 
              :counter nil
              :stages '()})))))