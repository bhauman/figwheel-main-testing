clean:
	rm -rf target/public

itest-cljs-339:
	rm -rf target/public
	clojure -A:fig:cljs-339:test

itest-cljs-753:
	rm -rf target/public
	clojure -A:fig:cljs-753:test

itest: itest-cljs-753 itest-cljs-339

build-753:
	rm -rf target/public
	clojure -A:fig:cljs-753:build
