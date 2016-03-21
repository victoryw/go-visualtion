(ns go-visual.core-test
  (:require [clojure.test :refer :all]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [go-visual.core :refer :all]
            [clj-http.fake :refer :all]))

(deftest test-mock
  (testing "should success when with corret paramters"
    (let [username "username"
          password "passwword"
          url      "http://example.com"
          json-body (json/write-str {:pipelines [{:counter 1 :name "123" :stages [{:name "st-1" :counter "1" :scheduled true :result "Passed" :jobs [{:scheduled_date 1088}]}]}]})
          result (atom nil)]
      (with-fake-routes {{:address url :basic-auth {username password}} 
                         (fn [reqeust] {:status 200 :headers {} :body json-body})}
        (with-redefs-fn 
          {#'write-to-site-json (fn [statis output-file-des]  (reset! result statis))}
          (fn [] 
            (-main "--url"  url  "--username"  username "--password" password)
            (is (= '({:name "123", :counter 1, :pipeline-run-times 1, :status true, :success 1, :end-time 1088}) @result))))))))

(run-tests 'go-visual.core-test)
