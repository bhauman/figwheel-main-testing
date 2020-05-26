clean:
	rm -rf target/public

itest-cljs-339:
	rm -rf target/public
	clojure -A:fig:cljs-339:test

itest-cljs-753:
	rm -rf target/public
	clojure -A:fig:cljs-753:test

itest-cljs-773:
	rm -rf target/public
	clojure -A:fig:cljs-773:test

itest-cljs-773-bundle:
	rm -rf target/public
	clojure -A:fig:cljs-773:test-bundle

itest: itest-cljs-339 itest-cljs-773 itest-cljs-773-bundle

build-753:
	rm -rf target/public
	clojure -A:fig:cljs-753:build

