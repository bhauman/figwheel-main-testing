(ns figwheel-main-testing.server
  (:require [ring.util.request :as req]
            [ring.util.response :as response]
            [clojure.pprint]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(defn success []
  (-> (response/response "Success")
      (response/header "Content-Type" "text/html")))

(defn fail [msg]
  (-> (response/bad-request msg)
      (response/header "Content-Type" "text/html")))

(defmulti handle-test-action :action)


(defmethod handle-test-action :default [data]
  (-> (response/not-found (format "Action %s not found" (:action data)))
      (response/header "Content-Type" "text/html")))

(defmethod handle-test-action :echo [data]
  (prn data)
  (-> (response/response (pr-str data))
      (response/header "Content-Type" "text/html")))

(defn file-touch [f]
  (.setLastModified f (System/currentTimeMillis)))

(defmethod handle-test-action :touch [{:keys [file-paths]}]
  (let [files (map io/file file-paths)
        exists-map (group-by #(.exists %) files)]
    (if (not-empty (get exists-map false))
      (fail "File not found")
      (do (doseq [f (get exists-map true)]
            (file-touch f))
          (success)))))

(defmethod handle-test-action :append-line [{:keys [file-path line]}]
  (let [file (io/file file-path)]
    (if-not (.exists file)
      (fail "File not found")
      (do
        (spit file (str "\n" line)
              :append true)
        (success)))))

(defmethod handle-test-action :truncate-file [{:keys [file-path contains]}]
  (let [file (io/file file-path)]
    (if-not (.exists file)
      (fail "File not found")
      (do
        (let [all-lines (string/split-lines (slurp file))
              lines
              (take-while
               #(not (.contains ^String % contains))
               all-lines)
              last-line
              (first (filter #(.contains ^String % contains)
                             all-lines))]
          (spit file (string/join "\n"
                                  (cond-> (vec lines)
                                    last-line (conj last-line)))))
        (success)))))

;; delete line?

(defn handle-request [{:keys [uri params]}]
  (if (= uri "/test-an-action")
    (handle-test-action (read-string (:data params)))
    (-> (response/not-found "Action not found")
        (response/header "Content-Type" "text/html"))
    ))

(defn handler [req]
  (#'handle-request req))

