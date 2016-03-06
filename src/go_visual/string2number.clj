(ns go-visual.string2number)

(defn to-number 
  [str]
  (let [n (read-string str)]
    (if (number? n) n nil)))