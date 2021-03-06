(ns scio-back.scraper-test
  (:require [clojure.test :refer :all]
            [clojure.string]
            [scio-back.scraper :refer :all]
            [scio-back.core :refer [read-config]]))

(def test-text-lowercase "
    md5: be5ee729563fa379e71d82d61cc3fdcf
    sha256: 103cb6c404ba43527c2deac40fbe984f7d72f0b2366c0b6af01bd0b4f1a30c74
    sha1: 3c07cb361e053668b4686de6511d6a904a9c4495
    %2fchessbase.com
    %2Fchessbase.com
    twitter.com
    %2ftwitter.com
    %2Ftwitter.com
    127.0.0.1
    CVE-1991-1234
    CVE-1992-12345
    CVE-1993-123456
    CVE-1994-12
    CVE-1994-1234567
    www.nytimes3xbfgragh.onion
    fe80::ea39:35ff:fe12:2d71/64
    The mail address user@fastmail.fm is not real
    www.mnemonic.no")

(def test-text-uppercase
  (clojure.string/upper-case test-text-lowercase))

(deftest test-tld-list
  (let [tlds-all (tlds-from-files ["test/data/tld-list-1.txt" "test/data/tld-list-2.txt"])
        tlds-first (tlds-from-files ["test/data/tld-list-1.txt"])]
    (testing "tld-config-files"
      (is (= tlds-all["com" "no" "onion"]))
      (is (= tlds-first ["com" "no"])))))

(deftest test-scraper
  (let [indicators (raw-text->indicators (read-config) test-text-lowercase)]
    (testing "scrape md5 lowercase"
      (is (= (:md5 indicators) '("be5ee729563fa379e71d82d61cc3fdcf"))))

    (testing "scrape sha1 lowercase"
      (is  (= (:sha1 indicators) '("3c07cb361e053668b4686de6511d6a904a9c4495"))))

    (testing "scrape sha256 lowercase"
      (is  (= (:sha256 indicators) '("103cb6c404ba43527c2deac40fbe984f7d72f0b2366c0b6af01bd0b4f1a30c74"))))

    (testing "scrape email lowercase"
      (is  (= (:email indicators) '("user@fastmail.fm"))))

    (testing "scrape ipv4 lowercase"
      (is  (= (:ipv4 indicators) '("127.0.0.1"))))

    (testing "scrape fqdn lowercase"
      (is  (= (:fqdn indicators) '("chessbase.com" "chessbase.com" "twitter.com" "twitter.com" "twitter.com" "www.nytimes3xbfgragh.onion" "fastmail.fm" "www.mnemonic.no"))))

    (testing "scrape ipv6 lowercase"
      (is  (= (:ipv6 indicators) '("fe80::ea39:35ff:fe12:2d71"))))

    (testing "scrape cve lowercase"
      (is (= (:cve indicators) '("cve-1991-1234" "cve-1992-12345" "cve-1993-123456" "cve-1994-1234567"))))))
