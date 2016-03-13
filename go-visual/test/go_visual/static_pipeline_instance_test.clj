(ns go-visual.static_pipeline_instance_test
  (:require [clojure.test :refer :all]
            [go-visual.static_pipeline_instance :refer :all]))

(deftest extract-pipeline-instance-history-test
  (testing "should extract pipeline with all stages"
    (let [origin-instance {:name "test-pipeline" 
                           :counter 13
                           :stages [{:name "stage-1" :counter 13 :jobs [{:name 1}]}
                                    {:name "stage-2" :counter 14 :jobs [{:name 1}]}]
                           :status 15
                           :time 100}]
      (is (= (extract-pipeline-instance-history origin-instance) 
              {:name "test-pipeline" 
               :counter 13 
               :stages [{:name "stage-1" :counter 13}
                        {:name "stage-2" :counter 14}]})))))

(run-tests 'go-visual.static_pipeline_instance_test)
